package sim.helper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import utils.Files;
import logger.StdLogger;
import gfs.Master;
import gfs.MasterClientInterface;
import gfs.Replica;
import gfs.ReplicaClientInterface;
import gfs.data.Host;
import gfs.data.HostTcp;
import gfs.hostprovider.SimpleHostInterfaceProvider;

public class OfflineSimHelper implements SimHelper {

    private final String root;
    private final SimpleHostInterfaceProvider provider;
    private final HostTcp masterHost;
    private final HostTcp[] replicaHosts;
    private final boolean verbose;

    public OfflineSimHelper(String root, int nReplica, boolean verbose) {
        this.root = root;
        this.provider = new SimpleHostInterfaceProvider();
        this.masterHost = new HostTcp("localhost", 2000);
        this.replicaHosts = new HostTcp[nReplica];
        for (int i = 0; i < replicaHosts.length; i++) {
            replicaHosts[i] = new HostTcp("localhost", 2001 + i);
        }
        this.verbose = verbose;
    }

    @Override
    public void start() throws Exception {

        Files.deleteDir(new File("gfs" + File.separator + root));
        // create master
        Master master = new Master();
        master.setMe(masterHost);
        if (verbose)
            master.setLogger(new StdLogger(master.getMe().toString()));
        // create replicas
        List<Replica> replicas = new ArrayList<Replica>();
        for (int i = 0; i < replicaHosts.length; i++) {
            Replica r = new Replica();
            r.setMe(replicaHosts[i]);
            r.setRoot("gfs" + File.separator + root + File.separator +
                      String.format("replica-%02d", i));
            if (verbose)
                r.setLogger(new StdLogger(r.getMe().toString()));
            replicas.add(r);
        }
        // give replicas to master
        for (Replica r : replicas)
            master.addReplica(r.getMe());
        // give replicas to replicas
        for (Replica replica : replicas)
            for (Replica r : replicas)
                replica.addReplica(r.getMe());
        // create provider
        provider.add(master.getMe(), master);
        for (Replica replica : replicas)
            provider.add(replica.getMe(), replica);
        // assign provider
        master.setReplicaProvider(provider);
        for (Replica replica : replicas)
            replica.setReplicaProvider(provider);
        // init
        for (Replica replica : replicas)
            replica.init();
        master.init();
        // start simulation
        for (Replica replica : replicas) {
            Thread t = new Thread(replica);
            t.setDaemon(true);
            t.start();
        }
        Thread t = new Thread(master);
        t.setDaemon(true);
        t.start();
    }

    @Override
    public MasterClientInterface getMaster() {
        return provider.getMCI(masterHost);
    }

    @Override
    public ReplicaClientInterface getReplica(Host h) {
        return provider.getRCI(h);
    }

}
