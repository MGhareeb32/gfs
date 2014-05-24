package gfs.hostprovider;

import gfs.MasterClientInterface;
import gfs.data.Host;

import java.util.Map;
import java.util.TreeMap;

public class SimpleMasterClientInterfaceProvider
    implements MasterClientInterfaceProvider {

    private final Map<Host, MasterClientInterface> map
        = new TreeMap<Host, MasterClientInterface>();

    public void add(Host h, MasterClientInterface r) {
        map.put(h, r);
    }

    @Override
    public MasterClientInterface get(Host h) {
        return map.get(h);
    }
}
