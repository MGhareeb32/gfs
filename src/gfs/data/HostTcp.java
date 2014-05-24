package gfs.data;

public class HostTcp extends Host {

    public final String ip;
    public final int port;

    public HostTcp(String ipColonPort) {
        super(ipColonPort);
        int colon = ipColonPort.lastIndexOf(":");
        ip = ipColonPort.substring(0, colon);
        port = Integer.parseInt(ipColonPort.substring(colon + 1));
    }

    public HostTcp(String ip, int port) {
        this(ip + ":" + port);
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
