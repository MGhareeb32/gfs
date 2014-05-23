package gfs;

import java.rmi.RemoteException;

public interface ReplicaMasterInterface {

    public boolean heartbeat() throws RemoteException;
}
