package gfs.hostprovider;

import gfs.ReplicaMasterInterface;
import gfs.data.Host;

public interface ReplicaMasterInterfaceProvider {

    ReplicaMasterInterface get(Host h);
}
