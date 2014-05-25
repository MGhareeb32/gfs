package gfs.data;

public class HostRmi extends HostTcp {

    private static final long serialVersionUID = -8006633551475577905L;

    public final String objName;

    public HostRmi(String ipColonPortSlashObj) {
        super(ipColonPortSlashObj
                  .substring(0, ipColonPortSlashObj.indexOf('/')));
        objName = ipColonPortSlashObj
                      .substring(ipColonPortSlashObj.indexOf('/') + 1);
    }

    public HostRmi(String ip, int port, String objName) {
        this(ip + ":" + port + "/" + objName);
    }

    @Override
    public String toString() {
        return ip + ":" + port + "/" + objName;
    }

    public static void main(String[] args) {
        System.out.println(new HostRmi("192.168.1.1:55555/Object"));
        System.out.println(new HostRmi("localhost:12345/Home"));
    }
}
