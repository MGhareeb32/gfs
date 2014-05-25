package sim;

import gfs.data.FileContent;
import gfs.data.MsgNotFoundException;
import gfs.data.WriteMsg;

import java.io.FileNotFoundException;
import java.io.IOException;

import logger.Logger;
import logger.StdLogger;
import sim.helper.RmiLocalSimHelper;
import sim.helper.SimClientHelper;
import sim.helper.SimHelper;

/** Tests read and write for multiple files. **/
public class Sim1 implements Sim {

    @Override
    public void sim(SimHelper sim) {
        final int nFile = 3, nChunk = 3;
        Logger log = new StdLogger("client");
        String[] fileName = new String[nFile];
        for (int i = 0; i < fileName.length; i++)
            fileName[i] = String.format("file%d.txt", i);
        FileContent[][] data = new FileContent[fileName.length][nChunk];
        for (int i = 0; i < data.length; i++)
            for (int j = 0; j < data[i].length; j++)
                data[i][j] = new FileContent(fileName[i], Integer.toString(j));
        try {
            sim.start();
        } catch (Exception e) {
            log.err("Can't start simulation");
            e.printStackTrace();
        }
        // write files
        log.log("WRITE FILE");
        WriteMsg writeMsg[] = new WriteMsg[data.length];
        for (int i = 0; i < data.length; i++)
            try {
                writeMsg[i] = SimClientHelper.write(sim, data[i][0]);
                for (int j = 1; j < data[i].length; j++)
                    writeMsg[i]
                        = SimClientHelper.write(sim, data[i][j], writeMsg[i]);
                log.log(fileName[i] + " file written");
            } catch (IOException e1) {
                log.err(fileName[i] + " can't write");
            } catch (Exception e) {
                log.err(e);
            }
        // commit files
        log.log("COMMIT FILE");
        for (int i = 0; i < data.length; i++)
            try {
                SimClientHelper.commit(sim, writeMsg[i]);
                log.log(fileName[i] + " file committed");
            } catch (MsgNotFoundException e) {
                log.err(fileName[i] + " bad txnId");
            } catch (IOException e) {
                log.err(fileName[i] + " can't commit");
            } catch (Exception e) {
                log.err(e);
            }
        // read file
        log.log("READ FILE");
        for (int i = 0; i < data.length; i++)
            try {
                FileContent file = SimClientHelper.read(sim, fileName[i]);
                log.log(file.path + "=[" + file.data + "]");
            } catch (FileNotFoundException e) {
                log.err(fileName[i] + " not found");
            } catch (IOException e) {
                log.err(fileName[i] + " can't read");
            } catch (Exception e) {
                log.err(e);
            }
        // done
        log.log("DONE");
    }

    public static void main(String[] args) throws Exception {
        // OfflineSimHelper sim = new OfflineSimHelper("./gfs", 3, false);
        RmiLocalSimHelper sim = new RmiLocalSimHelper("./gfs", 3, false);
        new Sim1().sim(sim);
        System.exit(0);
    }

}
