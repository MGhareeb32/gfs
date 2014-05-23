package sim;

import gfs.Master;
import gfs.Replica;
import gfs.data.Host;
import gfs.replicaprovider.SimpleReplicaMasterInterfaceProvider;
import gfs.replicaprovider.SimpleReplicaReplicaInterfaceProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import logger.StdLogger;

public class OfflineSim {

    public static void main(String[] args) {
        File root = new File("gfs");
        int nReplica = 1;
        // create master
        Master master = new Master();
        master.setMe(new Host("localhost", 2000));
        master.setRoot(root);
        master.setLogger(new StdLogger(master.getMe().toString()));
        // create replicas
        List<Replica> replicas = new ArrayList<Replica>();
        for (int i = 1; i <= nReplica; i++) {
            Replica r = new Replica();
            r.setMe(new Host("localhost", 2000 + i));
            r.setRoot(root);
            r.setLogger(new StdLogger(r.getMe().toString()));
            replicas.add(r);
        }
        // create providers
        SimpleReplicaMasterInterfaceProvider replicaMasterProvider
            = new SimpleReplicaMasterInterfaceProvider();
        for (Replica replica : replicas)
            replicaMasterProvider.add(replica.getMe(), replica);
        SimpleReplicaReplicaInterfaceProvider replicaReplicaProvider
            = new SimpleReplicaReplicaInterfaceProvider();
        for (Replica replica : replicas)
            replicaReplicaProvider.add(replica.getMe(), replica);
        // give replicas
        for (Replica r : replicas)
            master.addReplica(r.getMe());
        for (Replica replica : replicas)
            for (Replica r : replicas)
                replica.addReplica(r.getMe());
        // give providers
        master.setReplicaProvider(replicaMasterProvider);
        for (Replica replica : replicas)
            replica.setReplicaProvider(replicaReplicaProvider);
        // start simulation
        for (Replica replica : replicas)
            new Thread(replica).start();
        new Thread(master).start();
    }
}
