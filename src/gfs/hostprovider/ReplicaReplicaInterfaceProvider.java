package gfs.hostprovider;

import gfs.ReplicaReplicaInterface;
import gfs.data.Host;

public interface ReplicaReplicaInterfaceProvider {

    ReplicaReplicaInterface getRRI(Host h) throws Exception;
}
