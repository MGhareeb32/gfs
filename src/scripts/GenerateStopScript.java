package scripts;

import gfs.data.HostRmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class GenerateStopScript {

    private static void stopNode
        (PrintWriter out,
         HostRmi node, String masterJar, String replicaJar) {

        StringBuilder cmd = new StringBuilder();
        cmd.append(String.format("pkill -f '%s*.jar'\n", masterJar));
        cmd.append(String.format("pkill -f '%s*.jar'\n", replicaJar));
        cmd.append("echo '");
        cmd.append(String.format("%s CLOSED", node));
        cmd.append("'");

        out.printf("(ssh %s \"%s\")\n\n", node.ip, cmd);
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
            (args.length >= 2 ? args[1] : "scripts/stop.sh");
        try {
            PrintWriter out = new PrintWriter(script);
            System.out.println("Generating " + script);
            // stop master
            stopNode(out, master, "Master", "Replica");
            // stop replicas
            for (HostRmi replica : replicas)
                stopNode(out, replica, "Master", "Replica");
            //
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println("File '" + script + "' bad!");
        }
    }
}
