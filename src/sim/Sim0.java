package sim;

import gfs.data.FileContent;
import gfs.data.MsgNotFoundException;
import gfs.data.WriteMsg;

import java.io.FileNotFoundException;
import java.io.IOException;

import logger.Logger;
import logger.StdLogger;
import sim.helper.OfflineSimHelper;
import sim.helper.RmiLocalSimHelper;
import sim.helper.SimClientHelper;
import sim.helper.SimHelper;

/** Tests read and write for a single file. **/
public class Sim0 implements Sim {

    @Override
    public void sim(SimHelper sim) {
        Logger log = new StdLogger("client");
        String fileName = "file0.txt";
        FileContent[] data = new FileContent[3];
        for (int i = 0; i < data.length; i++)
            data[i] = new FileContent(fileName, Integer.toString(i));
        try {
            sim.start();
        } catch (Exception e) {
            log.err("Can't start simulation");
            e.printStackTrace();
        }
        // read non existent file
        log.log("READ NON-EXISTENT FILE");
        try {
            SimClientHelper.read(sim, fileName);
            log.err(fileName + " found");
        } catch (FileNotFoundException e) {
            log.log(fileName + " not found");
        } catch (Exception e) {
            log.err(fileName + " can't read");
        }
        // write file
        log.log("WRITE FILE");
        WriteMsg writeMsg = null;
        try {
            writeMsg = SimClientHelper.write(sim, data[0]);
            for (int i = 1; i < data.length; i++)
                writeMsg = SimClientHelper.write(sim, data[i], writeMsg);
            log.log(fileName + " file written");
        } catch (IOException e1) {
            log.err(fileName + " can't write");
            log.err(e1);
        } catch (Exception e) {
            log.err(e);
        }
        // make sure file doesn't exist yet
        log.log("MAKE SURE FILE IS NOT FLUSHED YET");
        try {
            SimClientHelper.read(sim, fileName);
            log.err(fileName + " found");
        } catch (FileNotFoundException e) {
            log.log(fileName + " not found");
        } catch (IOException e) {
            log.err(fileName + " can't read");
            log.err(e);
        } catch (Exception e) {
            log.err(e);
        }
        // commit file
        log.log("COMMIT FILE");
        try {
            SimClientHelper.commit(sim, writeMsg);
            log.log(fileName + " file committed");
        } catch (MsgNotFoundException e) {
            log.err(fileName + " bad txnId");
            log.err(e);
        } catch (IOException e) {
            log.err(fileName + " can't commit");
            log.err(e);
        } catch (Exception e) {
            log.err(e);
        }
        // read file
        log.log("READ FILE");
        try {
            FileContent file = SimClientHelper.read(sim, fileName);
            log.log(file.path + "=[" + file.data + "]");
        } catch (FileNotFoundException e) {
            log.err(fileName + " not found");
            log.err(e);
        } catch (IOException e) {
            log.err(fileName + " can't read");
            log.err(e);
        } catch (Exception e) {
            log.err(e);
        }
        // done
        log.log("DONE");
    }

    public static void main(String[] args) throws Exception {
        OfflineSimHelper sim = new OfflineSimHelper("sim0", 3, false);
        // RmiLocalSimHelper sim = new RmiLocalSimHelper("sim0", 4, false);
        new Sim0().sim(sim);
        System.exit(0);
    }

}
