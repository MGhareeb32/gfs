package gfs;

import gfs.data.FileContent;
import gfs.data.MsgNotFoundException;
import gfs.data.WriteMsg;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ReplicaClientInterface extends Remote {

   public WriteMsg clientWrite(long txnID, long msgSeqNum, FileContent data)
       throws RemoteException, IOException;

   public boolean clientCommit(long txnID, long numOfMsgs)
       throws MsgNotFoundException, RemoteException, IOException;

   public boolean clientAbort(long txnID) throws RemoteException;

   public FileContent clientRead(long txnID, FileContent data)
       throws RemoteException, IOException;
}
