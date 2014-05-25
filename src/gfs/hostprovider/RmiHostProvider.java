package gfs.hostprovider;

import gfs.MasterClientInterface;
import gfs.ReplicaClientInterface;
import gfs.ReplicaMasterInterface;
import gfs.ReplicaReplicaInterface;
import gfs.data.Host;
import gfs.data.HostRmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;

import utils.Rmi;

public class RmiHostProvider implements MasterClientInterfaceProvider,
                                        ReplicaClientInterfaceProvider,
                                        ReplicaMasterInterfaceProvider,
                                        ReplicaReplicaInterfaceProvider {

    @Override
    public MasterClientInterface getMCI(Host h)
        throws RemoteException, NotBoundException {

        return (MasterClientInterface)Rmi.getRemoteObject((HostRmi)h);
    }

    @Override
    public ReplicaClientInterface getRCI(Host h)
            throws RemoteException, NotBoundException {

        return (ReplicaClientInterface)Rmi.getRemoteObject((HostRmi)h);
    }

    @Override
    public ReplicaMasterInterface getRMI(Host h)
        throws RemoteException, NotBoundException {

        return (ReplicaMasterInterface)Rmi.getRemoteObject((HostRmi)h);
    }

    @Override
    public ReplicaReplicaInterface getRRI(Host h)
        throws RemoteException, NotBoundException {

        return (ReplicaReplicaInterface)Rmi.getRemoteObject((HostRmi)h);
    }
}
