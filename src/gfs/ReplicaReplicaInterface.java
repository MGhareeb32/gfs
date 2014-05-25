package gfs;

import gfs.data.FileContent;
import gfs.data.MsgNotFoundException;
import gfs.data.WriteMsg;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReplicaReplicaInterface extends Remote {

    public WriteMsg write(long txnID, long msgSeqNum, FileContent data)
            throws RemoteException, IOException;

    public boolean commit(long txnID, long numOfMsgs)
        throws RemoteException, MsgNotFoundException, IOException;

    public boolean abort(long txnID) throws RemoteException;

}
