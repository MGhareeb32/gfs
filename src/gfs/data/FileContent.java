package gfs.data;

import java.io.Serializable;

public class FileContent implements Comparable<FileContent>, Serializable {

    private static final long serialVersionUID = 5160516437600848340L;

    public final String path;
    public final String data;

    public FileContent(String path, String data) {
        this.path = path;
        this.data = data;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileContent)
            return path.equals(((FileContent)obj).path);
        return super.equals(obj);
    }

    @Override
    public int compareTo(FileContent o) {
        return path.compareTo(o.path);
    }

    @Override
    public String toString() {
        return path;
    }
}
