package gfs;

import java.rmi.RemoteException;

public interface ReplicaMasterInterface {

    public boolean isAlive() throws RemoteException;
}
