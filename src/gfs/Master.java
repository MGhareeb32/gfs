package gfs;

import gfs.data.FileContent;
import gfs.data.Host;
import gfs.data.ReadMsg;
import gfs.data.WriteMsg;
import gfs.replicaprovider.ReplicaMasterInterfaceProvider;

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

import logger.DummyLogger;
import logger.Logger;

public class Master implements MasterClientInterface, Runnable {

    private File root;

    private Host me;
    private final List<Host> replicas = new ArrayList<Host>();

    private long txnId = 0;
    private ReplicaMasterInterfaceProvider replicaProvider;
    private final Set<Host> aliveReplicas = new HashSet<Host>();

    private int lastAssignedReplica = 0;
    private final Map<FileContent, Host> file2Replica
        = new HashMap<FileContent, Host>();

    private Object lockTxnId = new Object();
    private Object lockLastAssignedReplica = new Object();
    private Object lockAliveReplicas = new Object();

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

    public void addReplica(Host replica) {
        replicas.add(replica);
    }

    public void setReplicaProvider
        (ReplicaMasterInterfaceProvider replicaProvider) {

        this.replicaProvider = replicaProvider;
    }

    public void setLogger(Logger log) {
        this.log = log;
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

    private Host[] heartbeat() {
        Host[] alive = null;
        synchronized (lockAliveReplicas) {
            aliveReplicas.clear();
            for (Host r : replicas) {
                try {
                    if (replicaProvider.get(r).heartbeat())
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
        log.log("Master started");
        // TODO Auto-generated method stub
    }

    // Master [ip:port] [dir] [replica...]
    public static void main(String[] args) {
        Master m = new Master();
        m.setMe(new Host(args[0]));
        m.setRoot(new File(args[1]));
        for (int i = 2; i < args.length; i++)
            m.addReplica(new Host(args[i]));
        System.out.println("Starting " + m);
        new Thread(m).start();
    }
}
