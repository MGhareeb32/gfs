package gfs;

import gfs.data.FileContent;
import gfs.data.Host;
import gfs.data.HostRmi;
import gfs.data.MsgNotFoundException;
import gfs.data.WriteMsg;
import gfs.data.WriteTxnState;
import gfs.hostprovider.ReplicaReplicaInterfaceProvider;
import gfs.hostprovider.RmiHostProvider;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import logger.DummyLogger;
import logger.FileLogger;
import logger.Logger;
import utils.Files;
import utils.Rmi;

public class Replica extends UnicastRemoteObject
                     implements ReplicaClientInterface,
                                ReplicaReplicaInterface,
                                ReplicaMasterInterface, Runnable {

    private static final long serialVersionUID = -7414743350021030181L;

    private File root = null;

    private Host me;
    private ReplicaReplicaInterfaceProvider replicaProvider;
    private final List<Host> replicas = new ArrayList<Host>();

    private final Map<Long, WriteTxnState> txnId2State
        = new TreeMap<Long, WriteTxnState>(); // do we need locks?

    private Object lockTxnIdState = new Object();
    private Map<String, Object> path2commitLock
        = new TreeMap<String, Object>();

    private Logger log = new DummyLogger();

    public Replica() throws RemoteException {
        super();
    }

    // SETTERS

    public Host getMe() {
        return me;
    }

    public void setMe(Host me) {
        this.me = me;
    }

    public void setRoot(String root) {
        this.root = new File(root);
    }

    public void addReplica(Host replica) {
        replicas.add(replica);
    }

    public void setReplicaProvider
        (ReplicaReplicaInterfaceProvider replicaProvider) {

        this.replicaProvider = replicaProvider;
    }

    public void setLogger(Logger log) {
        this.log = log;
    }

    // SERVER STATE

    @Override
    public boolean heartbeat() throws RemoteException {
        return true;
    }

    // REPLICA-REPLICA

    @Override
    public WriteMsg write(long txnID, long msgSeqNum, FileContent data)
        throws RemoteException, IOException {

        log.log(String.format("  write(%s,%d,%d)", data.path,
                              txnID, msgSeqNum));
        WriteTxnState txnState = null;
        synchronized (lockTxnIdState) {
            // new txn
            if (!txnId2State.containsKey(txnID))
                txnId2State.put(txnID, new WriteTxnState(txnID, data.path));
            // make sure if good seqNum
            if (msgSeqNum != txnId2State.get(txnID).getLastSeqNum() + 1)
                throw new IOException("Bad msgSeqNum");
            // write
            txnState = txnId2State.get(txnID);
        }
        txnState.write(data);
        return new WriteMsg(txnID, System.currentTimeMillis(),
                            me, txnState.getLastSeqNum() + 1);
    }

    @Override
    public boolean commit(long txnID, long numOfMsgs)
        throws RemoteException, MsgNotFoundException, IOException {

        synchronized (lockTxnIdState) {
            log.log(String.format("  commit(%d)", txnID));
            // bad txnID
            if (!txnId2State.containsKey(txnID))
                throw new MsgNotFoundException();
            // commit
            txnId2State.get(txnID).commit(root);
            txnId2State.remove(txnID);
        }
        return true;
    }

    @Override
    public boolean abort(long txnID) throws RemoteException {
        log.log(String.format("abort(%d)", txnID));
        synchronized (lockTxnIdState) {
            txnId2State.remove(txnID);
        }
        return true;
    }

    // CLIENT-REPLICA

    @Override
    public WriteMsg clientWrite
        (final long txnID, final long msgSeqNum, final FileContent data)
        throws RemoteException, IOException {

        log.log(String.format("write(%s,%d,%d)", data.path, txnID, msgSeqNum));
        // write to other replicas in parallel
        List<Thread> ths = new ArrayList<Thread>();
        for (Host r : replicas)
            if (!me.equals(r)) {
                final Host otherR = r;
                Thread th = new Thread() {
                    public void run() {
                        try {
                            replicaProvider.getRRI(otherR)
                                .write(txnID, msgSeqNum, data);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                th.start();
                ths.add(th);
            }
        // make sure they all finish
        for (Thread th : ths)
            try {
                th.join();
            } catch (Exception e) {
                e.printStackTrace();
            }
        // write to self
        WriteMsg out = write(txnID, msgSeqNum, data);
        log.log(String.format("  done write(%s,%d, %d)",
                              data.path, txnID, msgSeqNum));
        return out;
    }

    @Override
    public boolean clientCommit(long txnID, long numOfMsgs)
        throws RemoteException, MsgNotFoundException, IOException {

        // make sure only one commit is executed per file
        if (!path2commitLock.containsKey(txnId2State.get(txnID).path))
            path2commitLock.put(txnId2State.get(txnID).path, new Object());
        synchronized (path2commitLock.get(txnId2State.get(txnID).path)) {
            // commit self
            log.log(String.format("commit(%d)", txnID));
            boolean commited = commit(txnID, numOfMsgs);
            // commit replicas
            for (Host r : replicas)
                if (!me.equals(r))
                    try {
                        ReplicaReplicaInterface replica
                            = replicaProvider.getRRI(r);
                        commited = commited
                                 && replica.commit(txnID, numOfMsgs);
                    } catch (Exception e) {
                        commited = false;
                    }
            log.log(String.format(commited ? "  done commit(%d,%d)"
                                           : "  fail commit(%d,%d)",
                                  txnID, numOfMsgs));
            return commited;
        }
    }

    @Override
    public boolean clientAbort(long txnID) throws RemoteException {

        boolean aborted = abort(txnID);
        for (Host r : replicas)
            if (!me.equals(r))
                try {
                    if (!replicaProvider.getRRI(r).abort(txnID))
                        aborted = false;
                } catch (Exception e) {
                    aborted = false;
                }
        log.log(String.format(aborted ? "  done abort(%d)"
                                      : "  fail abort(%d)",
                              txnID));
        return aborted;
    }

    @Override
    public FileContent clientRead(long txnID, FileContent data)
        throws RemoteException, IOException {

        log.log(String.format("read(%s)", data.path));
        FileContent c = Files.readFile(root, data);
        log.log(String.format("  done read(%s)", data.path));
        return c;
    }

    //

    public void init() {
        log.log("init()");
        root.mkdirs();
        Files.deleteDir(root);
        root.mkdirs();
        log.log(String.format("%s cleaned", root));
        // TODO Auto-generated method stub
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            try {
                Thread.sleep(1000);
                log.log(String.format("%d uncommitted txns",
                                      txnId2State.size()));
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public String toString() {
        return me.toString() + "[" + root + "]";
    }

    public static void main(String[] args) throws RemoteException, FileNotFoundException {
        System.out.println(Arrays.toString(args));
        HostRmi replicaHost = null;
        List<HostRmi> replicaHosts = new ArrayList<HostRmi>();
        try {
            replicaHost = new HostRmi(args[0]);
            for (int i = 1; i < args.length; i++)
                replicaHosts.add(new HostRmi(args[i]));
        } catch (Exception e) {
            System.out.println("Usage:");
            System.out.println("./Replica.jar <replica> <all-replicas>");
            System.out.println("    <master>      <ip>:<port>/<rmi_name>");
            System.out.println("    <replica>     <ip>:<port>/<rmi_name>");
            System.exit(1);
        }
        String root = "./gfs" + File.separator + replicaHost.objName;
        String logs = root + ".log";
        new File(root).mkdirs();
        // create master
        Replica replica = new Replica();
        replica.setMe(replicaHost);
        replica.setRoot(root);
        replica.setLogger(new FileLogger(replicaHost.objName, logs));
        // rmi
        Rmi.registerLocalObject(replicaHost, replica);
        // give replicas to master
        for (HostRmi r : replicaHosts)
            replica.addReplica(r);
        // assign provider
        replica.setReplicaProvider(new RmiHostProvider());
        // run
        replica.init();
        System.out.println(replicaHost + " RUNNING");
        replica.run();
    }
}
