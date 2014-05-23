package gfs.replicaprovider;

import java.util.Map;
import java.util.TreeMap;

import gfs.ReplicaReplicaInterface;
import gfs.data.Host;

public class SimpleReplicaReplicaInterfaceProvider
    implements ReplicaReplicaInterfaceProvider {

    private final Map<Host, ReplicaReplicaInterface> map
        = new TreeMap<Host, ReplicaReplicaInterface>();

    public void add(Host h, ReplicaReplicaInterface r) {
        map.put(h, r);
    }

    @Override
    public ReplicaReplicaInterface get(Host h) {
        return map.get(h);
    }
}
