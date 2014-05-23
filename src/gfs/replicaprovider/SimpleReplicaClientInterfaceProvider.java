package gfs.replicaprovider;

import java.util.Map;
import java.util.TreeMap;

import gfs.ReplicaClientInterface;
import gfs.data.Host;

public class SimpleReplicaClientInterfaceProvider
    implements ReplicaClientInterfaceProvider {

    private final Map<Host, ReplicaClientInterface> map
        = new TreeMap<Host, ReplicaClientInterface>();

    public void add(Host h, ReplicaClientInterface r) {
        map.put(h, r);
    }

    @Override
    public ReplicaClientInterface get(Host h) {
        return map.get(h);
    }
}
