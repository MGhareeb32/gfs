package gfs.replicaprovider;

import gfs.ReplicaClientInterface;
import gfs.data.Host;

public interface ReplicaClientInterfaceProvider {

    ReplicaClientInterface get(Host h);
}
