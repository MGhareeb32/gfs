package gfs.hostprovider;

import gfs.MasterClientInterface;
import gfs.data.Host;

public interface MasterClientInterfaceProvider {

    MasterClientInterface get(Host h);
}
