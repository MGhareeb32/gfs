package gfs;

import gfs.data.FileContent;
import gfs.data.ReadMsg;
import gfs.data.WriteMsg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;

public interface MasterClientInterface {

    public ReadMsg read(String fileName)
       throws FileNotFoundException, IOException, RemoteException;

    public WriteMsg write(FileContent data)
        throws RemoteException, IOException;
}
