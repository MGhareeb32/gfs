package gfs.replicaprovider;

import gfs.ReplicaReplicaInterface;
import gfs.data.Host;

public interface ReplicaReplicaInterfaceProvider {

    ReplicaReplicaInterface get(Host h);
}
