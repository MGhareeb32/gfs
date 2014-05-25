package gfs.data;

import java.io.Serializable;

public abstract class Host implements Comparable<Host>, Serializable {

    private static final long serialVersionUID = 6843546101606129023L;

    public Host(String host) {
    }

    public abstract String getRoot();

    @Override
    public final boolean equals(Object obj) {
        if (obj instanceof HostTcp)
            return toString().equals(obj.toString());
        return super.equals(obj);
    }

    @Override
    public final int compareTo(Host o) {
        return toString().compareTo(o.toString());
    }

    @Override
    public final int hashCode() {
        return toString().hashCode();
    }
}
