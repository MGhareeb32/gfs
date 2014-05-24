package utils.rmitest;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface StateInterface extends Remote {

    int nextValue() throws RemoteException;
}
