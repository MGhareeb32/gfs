package gfs.data;

public abstract class Host implements Comparable<Host> {

    public Host(String host) {
    }

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
