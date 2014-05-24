package gfs;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReplicaMasterInterface extends Remote {

    public boolean heartbeat() throws RemoteException;
}
