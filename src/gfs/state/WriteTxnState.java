package gfs.state;

import gfs.data.FileContent;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
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

    public boolean commit() throws IOException {
        for (FileContent c : writes)
                Files.writeFile(c);
        return true;
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
        WriteTxnState txn = new WriteTxnState(10, "lala");
        txn.write(new FileContent("lala", "A\n"));
        txn.write(new FileContent("lala", "B\n"));
        txn.write(new FileContent("lala", "C\n"));
        System.out.println(txn.commit());
        System.out.println(Files.readFile(new FileContent("lala", null)).data);
    }
}
