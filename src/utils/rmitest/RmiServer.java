package utils.rmitest;

import java.rmi.RemoteException;

import utils.Rmi;

public class RmiServer {

    public static void main(String[] args) throws RemoteException {
        State s = new State();
        Rmi.registerLocalObject("127.0.1.1", 55555, "State", s);
    }
}
