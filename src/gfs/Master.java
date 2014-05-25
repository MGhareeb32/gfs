package gfs;

import gfs.data.FileContent;
import gfs.data.Host;
import gfs.data.HostRmi;
import gfs.data.ReadMsg;
import gfs.data.WriteMsg;
import gfs.hostprovider.ReplicaMasterInterfaceProvider;
import gfs.hostprovider.RmiHostProvider;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import utils.Rmi;
import logger.DummyLogger;
import logger.Logger;
import logger.StdLogger;

public class Master extends UnicastRemoteObject
                    implements MasterClientInterface, Runnable {

    private static final long serialVersionUID = 6490583536479586297L;

    private File root;

    private Host me;
    private final List<Host> replicas = new ArrayList<Host>();

    private long txnId = 0;
    private ReplicaMasterInterfaceProvider replicaProvider;
    private final Set<Host> aliveReplicas = new HashSet<Host>();

    private int lastAssignedReplica = -1;
    private final Map<FileContent, Host> file2Replica
        = new HashMap<FileContent, Host>();

    private Object lockTxnId = new Object();
    private Object lockLastAssignedReplica = new Object();
    private Object lockAliveReplicas = new Object();

    private Logger log = new DummyLogger();

    public Master() throws RemoteException {
        super();
    }

    // SETTERS

    public Host getMe() {
        return me;
    }

    public void setMe(Host me) {
        this.me = me;
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

    private Host assignReplica(FileContent data) {
        synchronized (lockLastAssignedReplica) {
            lastAssignedReplica++;
            lastAssignedReplica %= replicas.size();
            log.log(String.format
                ("%s assigned to %s",
                 data.path, replicas.get(lastAssignedReplica).toString()));
            file2Replica.put(data, replicas.get(lastAssignedReplica));
            return replicas.get(lastAssignedReplica);
        }
    }

    private Host[] heartbeat() {
        Host[] alive = null;
        synchronized (lockAliveReplicas) {
            aliveReplicas.clear();
            for (Host r : replicas) {
                try {
                    if (replicaProvider.getRMI(r).heartbeat())
                        aliveReplicas.add(r);
                } catch (Exception e) {}
            }
            alive = new Host[aliveReplicas.size()];
            aliveReplicas.toArray(alive);
        }
        return alive;
    }

    // GFS

    @Override
    public ReadMsg clientRead(String fileName) throws RemoteException {
        return new ReadMsg(nextTxnId(), System.currentTimeMillis(),
                           heartbeat());
    }

    @Override
    public WriteMsg clientWrite(FileContent data)
        throws RemoteException, IOException {

        // new file
        if (!file2Replica.containsKey(data))
            assignReplica(data);
        // exists
        return new WriteMsg(nextTxnId(), System.currentTimeMillis(),
                            file2Replica.get(data), 0);
    }

    //

    public void init() {
        log.log("init()");
        // TODO Auto-generated method stub
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        while (true) {
            try {
                Thread.sleep(1000);
                Host[] alive = heartbeat();
                log.log("heartbeat() = " + Arrays.toString(alive));
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public String toString() {
        return me.toString() + "[" + root + "]";
    }

    public static void main(String[] args) throws RemoteException {
        HostRmi masterHost = null;
        List<HostRmi> replicaHosts = new ArrayList<HostRmi>();
        try {
            masterHost = new HostRmi(args[0]);
            for (int i = 1; i < args.length; i++)
                replicaHosts.add(new HostRmi(args[i]));
        } catch (Exception e) {
            System.out.println("Usage:");
            System.out.println("./Master.jar <master> <all-replicas>");
            System.out.println("    <master>      <ip>:<port>/<rmi_name>");
            System.out.println("    <replica>     <ip>:<port>/<rmi_name>");
            System.exit(1);
        }
        // create master
        Master master = new Master();
        master.setMe(masterHost);
        master.setLogger(new StdLogger(masterHost.toString()));
        // rmi
        Rmi.registerLocalObject(masterHost, master);
        // give replicas to master
        for (HostRmi r : replicaHosts)
            master.addReplica(r);
        // assign provider
        master.setReplicaProvider(new RmiHostProvider());
        // run
        master.init();
        master.run();
    }
}
