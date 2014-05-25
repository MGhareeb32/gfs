package gfs.hostprovider;

import gfs.MasterClientInterface;
import gfs.ReplicaClientInterface;
import gfs.ReplicaMasterInterface;
import gfs.ReplicaReplicaInterface;
import gfs.data.Host;

import java.util.Map;
import java.util.TreeMap;

public class SimpleHostInterfaceProvider
    implements MasterClientInterfaceProvider,
               ReplicaClientInterfaceProvider,
               ReplicaMasterInterfaceProvider,
               ReplicaReplicaInterfaceProvider {

    private final Map<Host, Object> map = new TreeMap<Host, Object>();

    public void add(Host h, Object r) {
        map.put(h, r);
    }

    @Override
    public MasterClientInterface getMCI(Host h) {
        return (MasterClientInterface)map.get(h);
    }

    @Override
    public ReplicaClientInterface getRCI(Host h) {
        return (ReplicaClientInterface)map.get(h);
    }

    @Override
    public ReplicaMasterInterface getRMI(Host h) {
        return (ReplicaMasterInterface)map.get(h);
    }

    @Override
    public ReplicaReplicaInterface getRRI(Host h) {
        return (ReplicaReplicaInterface)map.get(h);
    }
}
