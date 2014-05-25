package sim.helper;

import java.io.File;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import utils.Files;
import utils.Rmi;
import logger.StdLogger;
import gfs.Master;
import gfs.MasterClientInterface;
import gfs.Replica;
import gfs.ReplicaClientInterface;
import gfs.data.Host;
import gfs.data.HostRmi;
import gfs.hostprovider.RmiHostProvider;

public class RmiLocalSimHelper implements SimHelper {

    private final String root;
    private final RmiHostProvider provider;
    private final HostRmi masterHost;
    private final HostRmi[] replicaHosts;
    private final boolean verbose;

    public RmiLocalSimHelper(String root, int nReplica, boolean verbose) {
        this.root = root;
        this.provider = new RmiHostProvider();
        this.masterHost = new HostRmi("localhost", 2000, "Master");
        this.replicaHosts = new HostRmi[nReplica];
        for (int i = 0; i < replicaHosts.length; i++)
            replicaHosts[i] = new HostRmi("localhost", 2001 + i,
                                          String.format("replica-%02d", i));
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
        Rmi.registerLocalObject(masterHost, master);
        // create replicas
        List<Replica> replicas = new ArrayList<Replica>();
        for (int i = 0; i < replicaHosts.length; i++) {
            Replica r = new Replica();
            r.setMe(replicaHosts[i]);
            r.setRoot("gfs" + File.separator + root + File.separator +
                      replicaHosts[i].objName);
            if (verbose)
                r.setLogger(new StdLogger(r.getMe().toString()));
            replicas.add(r);
            Rmi.registerLocalObject(replicaHosts[i], r);
        }
        // give replicas to master
        for (Replica r : replicas)
            master.addReplica(r.getMe());
        // give replicas to replicas
        for (Replica replica : replicas)
            for (Replica r : replicas)
                replica.addReplica(r.getMe());
        // assign provider
        master.setReplicaProvider(provider);
        for (Replica replica : replicas)
            replica.setReplicaProvider(provider);
        // init
        for (Replica replica : replicas)
            replica.init();
        master.init();
        // start simulation
        Thread.sleep(1000);
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
    public MasterClientInterface getMaster()
        throws RemoteException, NotBoundException {

        return provider.getMCI(masterHost);
    }

    @Override
    public ReplicaClientInterface getReplica(Host h)
        throws RemoteException, NotBoundException {

        return provider.getRCI(h);
    }

}
