package scripts;

import gfs.data.HostRmi;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class GenerateUploadScript {

    private static void uploadJar(PrintWriter out, String jar, String host) {
        out.printf("echo 'UPLOADING %s.jar to %s'\n", jar, host);
        out.printf("scp %s.jar %s:%s.jar\n", jar, host, jar);
        out.printf("ssh %s 'chmod 777 %s.jar && chmod u+x %s.jar'\n",
                   host, jar, jar);
        out.printf("\n");
    }

    public static void main(String[] args) {
        // read config
        File config = new File(args.length >= 1 ? args[0] : "system.config");
        List<HostRmi> hosts = ReadConfig.read(config);
        HostRmi master = hosts.get(0);
        Set<HostRmi> replicas = new TreeSet<HostRmi>();
        for (int i = 1; i < hosts.size(); i++)
            replicas.add(hosts.get(i));
        // generate script
        File script = new File
            (args.length >= 2 ? args[1] : "scripts/upload.sh");
        try {
            PrintWriter out = new PrintWriter(script);
            System.out.println("Generating " + script);
            // upload master jar
            uploadJar(out, "Master", master.ip);
            // upload replica jars
            for (HostRmi replica : replicas)
                uploadJar(out, "Replica", replica.ip);
            //
            out.printf("echo 'UPLOAD FINISHED'\n");
            out.close();
        } catch (FileNotFoundException e) {
            System.err.println("File '" + script + "' bad!");
        }
    }
}
