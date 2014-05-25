package gfs.hostprovider;

import gfs.MasterClientInterface;
import gfs.data.Host;

public interface MasterClientInterfaceProvider {

    MasterClientInterface getMCI(Host h) throws Exception;
}
