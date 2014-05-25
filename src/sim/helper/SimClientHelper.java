package sim.helper;

import gfs.data.FileContent;
import gfs.data.Host;
import gfs.data.ReadMsg;
import gfs.data.WriteMsg;

import java.util.Random;

public class SimClientHelper {

    public static FileContent read(SimHelper sim, String fileName)
        throws Exception {

        FileContent c = new FileContent(fileName, null);
        ReadMsg readMsg = sim.getMaster().clientRead(fileName);
        Host replica = readMsg.locs[new Random().nextInt(readMsg.locs.length)];
        return sim.getReplica(replica).clientRead(readMsg.txnId, c);
    }

    public static WriteMsg write
        (SimHelper sim, FileContent c, WriteMsg lastWriteMsg)
        throws Exception {

        return sim.getReplica(lastWriteMsg.loc)
            .clientWrite(lastWriteMsg.txnId, lastWriteMsg.seqNum, c);
    }

    public static WriteMsg write(SimHelper sim, FileContent c)
        throws Exception {

        WriteMsg writeMsg = sim.getMaster().clientWrite(c);
        return write(sim, c, writeMsg);
    }

    public static boolean commit(SimHelper sim, WriteMsg lastWriteMsg)
        throws Exception {

        return sim.getReplica(lastWriteMsg.loc)
            .clientCommit(lastWriteMsg.txnId, lastWriteMsg.seqNum);
    }

    public static boolean abort(SimHelper sim, WriteMsg lastWriteMsg)
        throws Exception {

        return sim.getReplica(lastWriteMsg.loc)
            .clientAbort(lastWriteMsg.txnId);
    }
}
