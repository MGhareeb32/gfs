package gfs.data;

public class Host {
    public final String ip;
    public final int port;

    public Host(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    @Override
    public String toString() {
        return ip + ":" + port;
    }
}
