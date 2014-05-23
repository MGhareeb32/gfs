package gfs.data;

public class Host implements Comparable<Host> {
    public final String ip;
    public final int port;

    public Host(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public Host(String ip, String port) {
        this(ip, Integer.parseInt(port));
    }

    public Host(String ipColonPort) {
        this(ipColonPort.substring(0, ipColonPort.lastIndexOf(":")),
             ipColonPort.substring(ipColonPort.lastIndexOf(":") + 1));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Host)
            return toString().equals(obj.toString());
        return super.equals(obj);
    }

    @Override
    public int compareTo(Host o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }

    public static void main(String[] args) {
        System.out.println(new Host("localhost:80"));
        System.out.println(new Host("192.168.1.1:80"));
    }
}
