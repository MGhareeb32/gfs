package sim.helper;

import gfs.MasterClientInterface;
import gfs.ReplicaClientInterface;
import gfs.data.Host;

public interface SimHelper {

    void start() throws Exception;
    MasterClientInterface getMaster();
    ReplicaClientInterface getReplica(Host h);
}
