package sim.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import logger.StdLogger;
import gfs.Master;
import gfs.MasterClientInterface;
import gfs.Replica;
import gfs.ReplicaClientInterface;
import gfs.data.Host;
import gfs.hostprovider.SimpleMasterClientInterfaceProvider;
import gfs.hostprovider.SimpleReplicaClientInterfaceProvider;
import gfs.hostprovider.SimpleReplicaMasterInterfaceProvider;
import gfs.hostprovider.SimpleReplicaReplicaInterfaceProvider;

public class OfflineSimHelper implements SimHelper {

    private final File root;
    private final SimpleReplicaClientInterfaceProvider clientReplicas;
    private final SimpleMasterClientInterfaceProvider clientMaster;
    private final Host masterHost;
    private final Host[] replicaHosts;
    private final List<Thread> threads;

    public OfflineSimHelper(String root, int nReplica) {
        this.root = new File(root);
        this.clientReplicas = new SimpleReplicaClientInterfaceProvider();
        this.clientMaster = new SimpleMasterClientInterfaceProvider();
        this.masterHost = new Host("localhost", 2000);
        this.replicaHosts = new Host[nReplica];
        for (int i = 0; i < replicaHosts.length; i++)
            replicaHosts[i] = new Host("localhost", 2001 + i);
        this.threads = new ArrayList<Thread>();
    }

    @Override
    public void start() {

        // create master
        Master master = new Master();
        master.setMe(masterHost);
        master.setRoot(root);
        master.setLogger(new StdLogger(master.getMe().toString()));
        // create replicas
        List<Replica> replicas = new ArrayList<Replica>();
        for (int i = 0; i < replicaHosts.length; i++) {
            Replica r = new Replica();
            r.setMe(replicaHosts[i]);
            r.setRoot(root);
            r.setLogger(new StdLogger(r.getMe().toString()));
            replicas.add(r);
        }
        // create providers for client
        clientMaster.add(master.getMe(), master);
        for (Replica replica : replicas)
            clientReplicas.add(replica.getMe(), replica);
        // create providers for replicas
        SimpleReplicaMasterInterfaceProvider replicaMasterProvider
            = new SimpleReplicaMasterInterfaceProvider();
        for (Replica replica : replicas)
            replicaMasterProvider.add(replica.getMe(), replica);
        SimpleReplicaReplicaInterfaceProvider replicaReplicaProvider
            = new SimpleReplicaReplicaInterfaceProvider();
        for (Replica replica : replicas)
            replicaReplicaProvider.add(replica.getMe(), replica);
        // give replicas to master
        for (Replica r : replicas)
            master.addReplica(r.getMe());
        // give replicas to replicas
        for (Replica replica : replicas)
            for (Replica r : replicas)
                replica.addReplica(r.getMe());
        // assign providers
        master.setReplicaProvider(replicaMasterProvider);
        for (Replica replica : replicas)
            replica.setReplicaProvider(replicaReplicaProvider);
        // initalize
        for (Replica replica : replicas)
            replica.init();
        master.init();
        // start simulation
        for (Replica replica : replicas) {
            Thread t = new Thread(replica);
            threads.add(t);
            t.start();
        }
        Thread t = new Thread(master);
        threads.add(t);
        t.start();
    }

    @Override
    public void stop() {
        for (int i = threads.size() - 1; i >= 0; i--) {
            threads.get(i).stop();
            threads.remove(i);
        }
    }

    @Override
    public MasterClientInterface getMaster() {
        return clientMaster.get(masterHost);
    }

    @Override
    public ReplicaClientInterface getReplica(Host h) {
        return clientReplicas.get(h);
    }

}
