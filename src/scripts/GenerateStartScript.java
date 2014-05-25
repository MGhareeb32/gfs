package scripts;

import gfs.data.HostRmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerateStartScript {

    private static final String STR_BLOCK = "";
    private static final String STR_NOTBLOCK = "&";

    private static void runNode
        (PrintWriter out,
         HostRmi master, String masterJar, List<HostRmi> replicas,
         String block) {

        StringBuilder cmd = new StringBuilder();
        cmd.append(String.format("./%s.jar", masterJar));
        String arg0 = master.toString();
        arg0 = arg0.substring(arg0.indexOf('@') + 1);
        cmd.append(" " + arg0);
        for (HostRmi r : replicas) {
            String arg = r.toString();
            arg = arg.substring(arg.indexOf('@') + 1);
            cmd.append(" " + arg);
        }

        out.printf("ssh %s '%s' %s\n", master.ip, cmd, block);
    }

    public static void main(String[] args) {
        // read config
        File config = new File(args.length >= 1 ? args[0] : "system.config");
        List<HostRmi> hosts = ReadConfig.read(config);
        HostRmi master = hosts.get(0);
        List<HostRmi> replicas = new ArrayList<HostRmi>();
        for (int i = 1; i < hosts.size(); i++)
            replicas.add(hosts.get(i));
        // generate script
        File script = new File
            (args.length >= 2 ? args[1] : "scripts/start.sh");
        try {
            PrintWriter out = new PrintWriter(script);
            System.out.println("Generating " + script);
            // run replica jars
            for (HostRmi replica : replicas)
                runNode(out, replica, "Replica", replicas, STR_NOTBLOCK);
            //
            out.print("echo 'REPLICAS STARTED' &\n");
            // run master jar
            runNode(out, master, "Master", replicas, STR_BLOCK);
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println("File '" + script + "' bad!");
        }
    }
}
