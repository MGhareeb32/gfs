package utils.rmitest;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import utils.Rmi;

public class RmiClient {

    public static void main(String[] args)
        throws RemoteException, NotBoundException {

        StateInterface state
            = (StateInterface)Rmi.getRemoteObject("127.0.1.1", 55555, "State");
        for (int i = 0; i < 10; i++)
            System.out.println(state.nextValue());
    }
}
