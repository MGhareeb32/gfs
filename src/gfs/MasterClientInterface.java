package gfs;

import gfs.data.FileContent;
import gfs.data.ReadMsg;
import gfs.data.WriteMsg;

import java.io.IOException;
import java.rmi.RemoteException;

public interface MasterClientInterface {

    public ReadMsg clientRead(String fileName)
       throws RemoteException;

    public WriteMsg clientWrite(FileContent data)
        throws RemoteException, IOException;
}
