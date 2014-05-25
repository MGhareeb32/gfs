package sim;

import gfs.ReplicaClientInterface;
import gfs.data.FileContent;
import gfs.data.ReadMsg;
import gfs.data.WriteMsg;
import logger.Logger;
import logger.StdLogger;
import sim.helper.OfflineSimHelper;
import sim.helper.RmiLocalSimHelper;
import sim.helper.SimClientHelper;
import sim.helper.SimHelper;

/** Makes sure concurrent commits on the same file are executed correctly. **/
public class Sim3 implements Sim {

    private final int nWrites = 3;

    @Override
    public void sim(final SimHelper sim) {
        String fileName = "file.txt";
        final FileContent[] data = new FileContent[nWrites];
        final Logger logger = new StdLogger("sim");
        for (int i = 0; i < data.length; i++)
            data[i] = new FileContent(fileName, Integer.toString(i));
        final Logger[] clientLog = new Logger[data.length];
        for (int i = 0; i < clientLog.length; i++)
            clientLog[i] = new StdLogger("client" + i);
        try {
            sim.start();
        } catch (Exception e) {
            logger.err(e);
        }
        // write
        final WriteMsg[] writeMsg = new WriteMsg[data.length];
        for (int i = 0; i < data.length; i++)
            try {
                clientLog[i].log("write(" + data[i].data + ")");
                writeMsg[i] = SimClientHelper.write(sim, data[i]);
            } catch (Exception e) {
                clientLog[i].err(e);
            }
        // commit
        Thread[] ths = new Thread[data.length];
        for (int i = 0; i < data.length; i++) {
            final int dI = i;
            ths[i] = new Thread() {
                @Override
                    public void run() {
                    try {
                        clientLog[dI].log("commit(" + data[dI].data + ")");
                        SimClientHelper.commit(sim, writeMsg[dI]);
                    } catch (Exception e) {
                        clientLog[dI].err(e);
                    }
                }
            };
        }
        for (int i = 0; i < ths.length; i++)
            ths[i].start();
        // wait
        for (int i = 0; i < ths.length; i++)
            try {
                ths[i].join();
            } catch (Exception e) {
                logger.err(e);
            }
        // read and make sure all got the same file
        try {
            String output = null;
            ReadMsg readMsg = sim.getMaster().clientRead(fileName);
            for (int i = 0; i < readMsg.locs.length; i++) {
                ReplicaClientInterface r = sim.getReplica(readMsg.locs[i]);
                FileContent file = r.clientRead(readMsg.txnId, data[0]);
                if (output == null)
                    output = file.data;
                if (!file.data.equals(output)) {
                    logger.err("REPLICA MISMATCH");
                    logger.err("  " + file.data);
                    logger.err("  " + output);
                }
                logger.log("read(" + readMsg.locs[i] + ") = " + file.data);
            }
        } catch (Exception e) {
            logger.err(e);
        }
        // done
        logger.log("DONE");
    }

    public static void main(String[] args) throws Exception {
         // OfflineSimHelper sim = new OfflineSimHelper("sim3", 100, false);
        RmiLocalSimHelper sim = new RmiLocalSimHelper("sim3", 100, false);
        new Sim3().sim(sim);
        System.exit(0);
    }

}
