package utils.rmitest;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class State extends UnicastRemoteObject implements StateInterface {

    private static final long serialVersionUID = 5087790859680371522L;
    private int i = 0;

    public State() throws RemoteException {
        super();
    }

    @Override
    public synchronized int nextValue() throws RemoteException {
        return ++i;
    }

}
