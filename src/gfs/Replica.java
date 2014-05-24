package gfs;

import gfs.data.FileContent;
import gfs.data.Host;
import gfs.data.MsgNotFoundException;
import gfs.data.WriteMsg;
import gfs.data.WriteTxnState;
import gfs.hostprovider.ReplicaReplicaInterfaceProvider;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import logger.DummyLogger;
import logger.Logger;
import utils.Files;

public class Replica implements ReplicaClientInterface,
                                ReplicaReplicaInterface,
                                ReplicaMasterInterface, Runnable {

    private File root;

    private Host me;
    private ReplicaReplicaInterfaceProvider replicaProvider;
    private final List<Host> replicas = new ArrayList<Host>();

    private final Map<Long, WriteTxnState> txnId2State
        = new TreeMap<Long, WriteTxnState>(); // do we need locks?

    private Logger log = new DummyLogger();

    // SETTERS

    public Host getMe() {
        return me;
    }

    public void setMe(Host me) {
        this.me = me;
    }

    public void setRoot(File root) {
        this.root = root;
    }

    public File getRoot() {
        return root;
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

        log.log(String.format("write(%s,%d,%d)", data.path, txnID, msgSeqNum));
        // new txn
        if (!txnId2State.containsKey(txnID))
            txnId2State.put(txnID, new WriteTxnState(txnID, data.path));
        // make sure if good seqNum
        if (msgSeqNum != txnId2State.get(txnID).getLastSeqNum() + 1)
            throw new IOException("Bad msgSeqNum");
        // write
        WriteTxnState txnState = txnId2State.get(txnID);
        txnState.write(data);
        return new WriteMsg(txnID, System.currentTimeMillis(),
                            me, txnState.getLastSeqNum() + 1);
    }

    @Override
    public boolean commit(long txnID, long numOfMsgs)
        throws MsgNotFoundException, RemoteException, IOException {

        log.log(String.format("commit(%d,%d)", txnID, numOfMsgs));
        // bad txnID
        if (!txnId2State.containsKey(txnID))
            throw new MsgNotFoundException();
        // commit
        txnId2State.get(txnID).commit(root);
        txnId2State.remove(txnID);
        return true;
    }

    @Override
    public boolean abort(long txnID) throws RemoteException {
        log.log(String.format("abort(%d)", txnID));
        txnId2State.remove(txnID);
        return true;
    }

    // CLIENT-REPLICA

    @Override
    public WriteMsg clientWrite(long txnID, long msgSeqNum, FileContent data)
        throws RemoteException, IOException {

        for (Host r : replicas)
            if (!me.equals(r))
                replicaProvider.get(r).write(txnID, msgSeqNum, data);
        WriteMsg out = write(txnID, msgSeqNum, data);
        log.log(String.format("  done write(%s,%d, %d)",
                              data.path, txnID, msgSeqNum));
        return out;
    }

    @Override
    public boolean clientCommit(long txnID, long numOfMsgs)
        throws MsgNotFoundException, RemoteException, IOException {

        boolean commited = commit(txnID, numOfMsgs);
        for (Host r : replicas)
            if (!me.equals(r))
                try {
                    if (!replicaProvider.get(r).commit(txnID, numOfMsgs))
                        commited = false;
                } catch (Exception e) {
                    commited = false;
                }
        log.log(String.format(commited ? "  done commit(%d,%d)"
                                       : "  fail commit(%d,%d)",
                              txnID, numOfMsgs));
        return commited;
    }

    @Override
    public boolean clientAbort(long txnID) throws RemoteException {

        boolean aborted = abort(txnID);
        for (Host r : replicas)
            if (!me.equals(r))
                try {
                    if (!replicaProvider.get(r).abort(txnID))
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
        log.log("Root cleaned");
        // TODO Auto-generated method stub
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

    @Override
    public String toString() {
        return me.toString() + "[" + root + "]";
    }

    // Replica [ip:port] [dir] [replica...]
    public static void main(String[] args) {
        Replica r = new Replica();
        r.setMe(new Host(args[0]));
        r.setRoot(new File(args[1]));
        for (int i = 2; i < args.length; i++)
            r.addReplica(new Host(args[i]));
        System.out.println("Starting " + r);
        new Thread(r).start();
    }

}
