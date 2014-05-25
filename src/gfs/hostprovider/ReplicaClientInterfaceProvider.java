package gfs.hostprovider;

import gfs.ReplicaClientInterface;
import gfs.data.Host;

public interface ReplicaClientInterfaceProvider {

    ReplicaClientInterface getRCI(Host h) throws Exception;
}
