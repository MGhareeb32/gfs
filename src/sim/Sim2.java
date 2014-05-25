package sim;

import gfs.data.FileContent;
import gfs.data.MsgNotFoundException;
import gfs.data.WriteMsg;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;

import logger.DummyLogger;
import logger.FileLogger;
import logger.Logger;
import logger.StdLogger;
import sim.helper.OfflineSimHelper;
import sim.helper.SimClientHelper;
import sim.helper.SimHelper;

/** Tests random concurrent reads and writes for multiple file. **/
public class Sim2 implements Sim {

    private final String[] fileName;
    private final int nClient = 4, nReq = 10, nFile = 2;

    public Sim2() {
        fileName = new String[nFile];
        for (int i = 0; i < fileName.length; i++)
            fileName[i] = String.format("%c.txt", 'a' + i);
    }

    // CLIENT THREAD

    private class RandomClient extends Thread {
        private static final int READ = 0, WRITE = 1, COMMIT = 2;

        private final int clientId;
        private final SimHelper sim;
        private Logger log;
        private final Random rand = new Random();
        private final Map<String, WriteMsg> file2WriteMsg;

        public RandomClient(SimHelper sim, int clientId) {
            this.clientId = clientId;
            String loggerName = String.format("client%d", clientId);
            try {
                this.log = new FileLogger(loggerName, loggerName + ".txt");
            } catch (FileNotFoundException e) {
                this.log = new DummyLogger();
            }
            this.sim = sim;
            this.file2WriteMsg = new TreeMap<String, WriteMsg>();
        }

        @Override
        public void run() {
            int nReq = Sim2.this.nReq;
            while (nReq-- > 0)
                switch (rand.nextInt(3)) {
                case READ: {
                    randomRead();
                    break;
                } case WRITE: {
                    randomWrite();
                    break;
                } case COMMIT: {
                    randomCommit();
                    break;
                }
                }
        }

        private void randomRead() {
            String read = fileName[rand.nextInt(fileName.length)];
            log.log("READ FILE " + read);
            try {
                FileContent content = SimClientHelper.read(sim, read);
                log.log(read + "=[" + content.data + "]");
            } catch (FileNotFoundException e) {
                log.log(read + " not found");
            } catch (IOException e) {
                log.err(read + " can't read");
            }            
        }

        private void randomWrite() {
            String write = fileName[rand.nextInt(fileName.length)];
            log.log("WRITE FILE " + write);
            try {
                FileContent c = new FileContent(write, clientId + "\n");
                // new file
                WriteMsg writeMsg = null;
                if (!file2WriteMsg.containsKey(write))
                    writeMsg = SimClientHelper.write(sim, c);
                // old file
                else
                    writeMsg = SimClientHelper.write
                        (sim, c, file2WriteMsg.get(write));
                // write
                file2WriteMsg.put(write, writeMsg);
                log.log(write + " file written");
            } catch (IOException e1) {
                log.err(write + " can't write");
                e1.printStackTrace();
            }
        }

        private void randomCommit() {
            if (file2WriteMsg.isEmpty())
                return;
            // pick file
            String[] files = new String[file2WriteMsg.size()];
            file2WriteMsg.keySet().toArray(files);
            String commit = files[rand.nextInt(files.length)];
            // commit
            log.log("COMMIT FILE " + commit);
            try {
                SimClientHelper.commit(sim, file2WriteMsg.get(commit));
                file2WriteMsg.remove(commit);
                log.log(commit + " file committed");
            } catch (MsgNotFoundException e) {
                log.err(commit + " bad txnId");
            } catch (IOException e) {
                log.err(commit + " can't commit");
            }            
        }
    }

    @Override
    public void sim(SimHelper sim) {
        // start sim
        try {
            sim.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // start clients
        RandomClient[] clients = new RandomClient[nClient];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = new RandomClient(sim, i);
            clients[i].start();
        }
        // done
        for (int i = 0; i < clients.length; i++)
            try {
                clients[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    public static void main(String[] args) throws Exception {
        OfflineSimHelper sim = new OfflineSimHelper("./gfs", 1);
        new Sim2().sim(sim);
        System.exit(0);
    }

}
