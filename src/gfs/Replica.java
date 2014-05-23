package gfs;

import gfs.data.FileContent;
import gfs.data.Host;
import gfs.data.MsgNotFoundException;
import gfs.data.WriteMsg;
import gfs.state.WriteTxnState;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import utils.Files;

public class Replica implements ReplicaClientInterface,
                                ReplicaReplicaInterface,
                                ReplicaMasterInterface, Runnable {

    private File root;

    private Host me;
    private final List<Host> replicas = new ArrayList<Host>();

    private final Map<Long, WriteTxnState> txnId2State
        = new TreeMap<Long, WriteTxnState>(); // do we need locks?

    // SETTERS

    public void setMe(Host me) {
        this.me = me;
    }
    
    public void setRoot(File root) {
        this.root = root;
    }

    public void addReplica(Host replica) {
        replicas.add(replica);
    }

    // SERVER STATE

    @Override
    public boolean isAlive() throws RemoteException {
        return true;
    }


    private ReplicaReplicaInterface getReplica(Host r) {
        // TODO Auto-generated method stub
        return null;
    }

    // REPLICA-REPLICA

    @Override
    public WriteMsg write(long txnID, long msgSeqNum, FileContent data)
        throws RemoteException, IOException {

        // new txn
        if (!txnId2State.containsKey(txnID))
            txnId2State.put(txnID, new WriteTxnState(txnID, data.path));
        // write
        txnId2State.get(txnID).write(data);
        return new WriteMsg(txnID, System.currentTimeMillis(), me);
    }

    @Override
    public boolean commit(long txnID, long numOfMsgs)
        throws MsgNotFoundException, RemoteException, IOException {

        // bad txnID
        if (!txnId2State.containsKey(txnID))
            throw new MsgNotFoundException();
        // commit
        txnId2State.get(txnID).commit();
        txnId2State.remove(txnID);
        return true;
    }

    @Override
    public boolean abort(long txnID) throws RemoteException {
        txnId2State.remove(txnID);
        return true;
    }

    // CLIENT-REPLICA

    @Override
    public WriteMsg clientWrite(long txnID, long msgSeqNum, FileContent data)
        throws RemoteException, IOException {
        for (Host r : replicas)
            getReplica(r).write(txnID, msgSeqNum, data);
        return write(txnID, msgSeqNum, data);
    }

    @Override
    public boolean clientCommit(long txnID, long numOfMsgs)
        throws MsgNotFoundException, RemoteException, IOException {

        boolean commited = commit(txnID, numOfMsgs);
        for (Host r : replicas)
            try {
                if (!getReplica(r).commit(txnID, numOfMsgs))
                    commited = false;
            } catch (Exception e) {
                commited = false;
            }
        return commited;
    }

    @Override
    public boolean clientAbort(long txnID) throws RemoteException {

        boolean aborted = abort(txnID);
        try {
        for (Host r : replicas)
            if (!getReplica(r).abort(txnID))
                aborted = false;
        } catch (Exception e) {
            aborted = false;
        }
        return aborted;
    }

    @Override
    public FileContent clientRead(long txnID, FileContent data)
        throws RemoteException, IOException {
        return Files.readFile(data);
    }

    //

    @Override
    public void run() {
        root.mkdirs();
        // TODO Auto-generated method stub
        
    }

    @Override
    public String toString() {
        return me.toString() + "[" + root + "]";
    }

    // Replica [ip] [port] [dir]
    public static void main(String[] args) {
        Replica r = new Replica();
        r.setMe(new Host(args[0], Integer.parseInt(args[1])));
        r.setRoot(new File(args[2]));
        System.out.println("Starting " + r);
        new Thread(r).start();
    }
}
