package gfs.data;

public class HostTcp extends Host {

    private static final long serialVersionUID = -2399671822372835640L;

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

    public static void main(String[] args) {
        System.out.println(new HostTcp("192.168.1.1:55555"));
        System.out.println(new HostTcp("localhost:12345"));
    }
}
