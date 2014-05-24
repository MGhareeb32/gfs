package gfs.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import utils.Files;

public class WriteTxnState implements Comparable<WriteTxnState> {

    public final long id;
    private final String path;
    private final List<FileContent> writes = new ArrayList<FileContent>();

    public WriteTxnState(long id, String path) {
        this.id = id;
        this.path = path;
    }

    public void write(FileContent data) {
        if (!path.equals(data.path))
            throw new IllegalArgumentException("Path name mismatch.");
        writes.add(data);
    }

    public boolean commit(File root) throws IOException {
        for (FileContent c : writes)
                Files.writeFile(root, c);
        return true;
    }

    public long getLastSeqNum() {
        return writes.size() - 1;
    }

    @Override
    public int hashCode() {
        return (int)(id + (id >> 32) * 31);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof WriteTxnState)
            return id == ((WriteTxnState)o).id;
        return super.equals(o);
    }

    @Override
    public int compareTo(WriteTxnState o) {
        return Long.compare(id, o.id);
    }

    @Override
    public String toString() {
        return Long.toString(id);
    }

    public static void main(String[] args) throws IOException {
        File root = new File("./gfs");
        WriteTxnState txn = new WriteTxnState(10, "lala");
        txn.write(new FileContent("lala", "A\n"));
        txn.write(new FileContent("lala", "B\n"));
        txn.write(new FileContent("lala", "C\n"));
        System.out.println(txn.commit(root));
        System.out.println(Files.readFile(root, new FileContent("lala", null)).data);
    }
}
