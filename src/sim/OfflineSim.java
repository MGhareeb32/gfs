package sim;

import gfs.Master;
import gfs.MasterClientInterface;
import gfs.Replica;
import gfs.data.FileContent;
import gfs.data.Host;
import gfs.data.ReadMsg;
import gfs.data.WriteMsg;
import gfs.replicaprovider.SimpleReplicaClientInterfaceProvider;
import gfs.replicaprovider.SimpleReplicaMasterInterfaceProvider;
import gfs.replicaprovider.SimpleReplicaReplicaInterfaceProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import logger.Logger;
import logger.StdLogger;

public class OfflineSim {

    private static MasterClientInterface offlineServerStart
        (SimpleReplicaClientInterfaceProvider clientReplicas) {

        // SERVER
        File root = new File("./gfs");
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
        for (Replica replica : replicas)
            clientReplicas.add(replica.getMe(), replica);
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
        // initalize
        for (Replica replica : replicas)
            replica.init();
        master.init();
        // start simulation
        for (Replica replica : replicas)
            new Thread(replica).start();
        new Thread(master).start();
        return master;
    }

    public static void main(String[] args) throws Exception {
        SimpleReplicaClientInterfaceProvider replicas 
            = new SimpleReplicaClientInterfaceProvider();
        MasterClientInterface master = offlineServerStart(replicas);
        // CLIENT
        Logger log = new StdLogger("client");
        String fileName0 = "file0.txt";
        // read non existent file
        log.log("Reading " + fileName0);
        try {
            master.clientRead(fileName0);
            log.err(fileName0 + " found");
        } catch (IOException e) {
            log.log(fileName0 + " not found");
        }
        // write file0
        log.log("Writing " + fileName0);
        FileContent[] file0 = new FileContent[3];
        for (int i = 0; i < file0.length; i++)
            file0[i] = new FileContent(fileName0, Integer.toString(i));
        WriteMsg write0 = master.clientWrite(file0[0]);
        for (int i = 0; i < file0.length; i++)
            replicas.get(write0.loc).clientWrite(write0.txnId, i, file0[i]);
        // make sure file0 doesn't exist yet
        log.log("Reading " + fileName0);
        try {
            master.clientRead(fileName0);
            log.err(fileName0 + " found");
        } catch (IOException e) {
            log.log(fileName0 + " not found");
        }
        // commit file0
        log.log("Committing " + fileName0);
        replicas.get(write0.loc).clientCommit(write0.txnId, file0.length);
        // read file0
        log.log("Reading " + fileName0);
        ReadMsg read0 = master.clientRead(fileName0);
        FileContent fileContent0
            = replicas.get(read0.locs[0]).clientRead
                (read0.txnId, new FileContent(fileName0, null));
        log.log(fileName0 + " [" + fileContent0.data + "]");
    }

}
