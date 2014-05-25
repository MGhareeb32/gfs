package utils.rmitest;

import gfs.data.HostRmi;
import utils.Rmi;

public class RmiTest {

    public static void main(String[] args) throws Exception {
        HostRmi host = new HostRmi("127.0.1.1:12345/State");
        // server
        State server = new State();
        Rmi.registerLocalObject(host, server);
        // client
        StateInterface state
            = (StateInterface)Rmi.getRemoteObject(host);
        for (int i = 0; i < 10; i++)
            System.out.println(state.nextValue());
    }
}
