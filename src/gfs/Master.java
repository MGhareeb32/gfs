package gfs;

import gfs.data.FileContent;
import gfs.data.Host;
import gfs.data.ReadMsg;
import gfs.data.WriteMsg;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Master implements MasterClientInterface, Runnable {

    private File root;

    private Host me;
    private final List<Host> replicas = new ArrayList<Host>();

    private long txnId = 0;
    private final Set<Host> aliveReplicas = new HashSet<Host>();

    private int lastAssignedReplica = 0;
    private final Map<FileContent, Host> file2Replica
        = new HashMap<FileContent, Host>();

    private Object lockTxnId = new Object();
    private Object lockLastAssignedReplica = new Object();
    private Object lockAliveReplicas = new Object();

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

    private long nextTxnId() {
        synchronized (lockTxnId) {
            return txnId++;
        }
    }

    private Host assignReplica(String path) {
        synchronized (lockLastAssignedReplica) {
            lastAssignedReplica++;
            lastAssignedReplica %= replicas.size();
            return replicas.get(lastAssignedReplica);
        }
    }

    private ReplicaMasterInterface getReplica(Host r) {
        // TODO Auto-generated method stub
        return null;
    }

    private Host[] heartbeat() {
        Host[] alive = null;
        synchronized (lockAliveReplicas) {
            aliveReplicas.clear();
            for (Host r : replicas) {
                try {
                    if (getReplica(r).isAlive())
                        aliveReplicas.add(r);
                } catch (RemoteException e) {}
            }
            alive = new Host[aliveReplicas.size()];
            aliveReplicas.toArray(alive);
        }
        return alive;
    }

    // GFS

    @Override
    public ReadMsg read(String fileName)
        throws FileNotFoundException, IOException, RemoteException {

        return new ReadMsg(nextTxnId(), System.currentTimeMillis(),
                           heartbeat());
    }

    @Override
    public WriteMsg write(FileContent data)
        throws RemoteException, IOException {

        // exists
        if (file2Replica.containsKey(data))
            return new WriteMsg(nextTxnId(), System.currentTimeMillis(),
                                file2Replica.get(data));
        // new file
        return new WriteMsg(nextTxnId(), System.currentTimeMillis(),
                            assignReplica(data.path));
    }

    //

    @Override
    public String toString() {
        return me.toString() + "[" + root + "]";
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
    }

    // Master [ip] [port] [dir]
    public static void main(String[] args) {
        Master m = new Master();
        m.setMe(new Host(args[0], Integer.parseInt(args[1])));
        m.setRoot(new File(args[2]));
        System.out.println("Starting " + m);
        new Thread(m).start();
    }
}
