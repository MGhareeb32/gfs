package scripts;

import gfs.data.HostRmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ReadConfig {

    public static List<HostRmi> read(File config) {
        List<HostRmi> hosts = new ArrayList<HostRmi>();
        try {
            Scanner scan = new Scanner(config);
            while (scan.hasNext())
                hosts.add(new HostRmi(scan.nextLine()));
            scan.close();
        } catch (FileNotFoundException e) {
            System.err.println("File '" + config + "' not found!");
        }
        return hosts;
    }
}
