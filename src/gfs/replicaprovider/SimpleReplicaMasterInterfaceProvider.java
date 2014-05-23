package gfs.replicaprovider;

import java.util.Map;
import java.util.TreeMap;

import gfs.ReplicaMasterInterface;
import gfs.data.Host;

public class SimpleReplicaMasterInterfaceProvider
    implements ReplicaMasterInterfaceProvider {

    private final Map<Host, ReplicaMasterInterface> map
        = new TreeMap<Host, ReplicaMasterInterface>();

    public void add(Host h, ReplicaMasterInterface r) {
        map.put(h, r);
    }

    @Override
    public ReplicaMasterInterface get(Host h) {
        return map.get(h);
    }
}
