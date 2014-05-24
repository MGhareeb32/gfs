package gfs;

import gfs.data.FileContent;
import gfs.data.ReadMsg;
import gfs.data.WriteMsg;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface MasterClientInterface extends Remote {

    public ReadMsg clientRead(String fileName)
       throws RemoteException;

    public WriteMsg clientWrite(FileContent data)
        throws RemoteException, IOException;
}
