package logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.sql.Time;

import utils.Exceptions;

public class FileLogger implements Logger {

    private final String owner;
    private final PrintWriter print;

    public FileLogger(String owner, String path)
        throws FileNotFoundException {

        this.owner = owner;
        this.print = new PrintWriter(new File(path));
    }

    private String getTime() {
        return new Time(System.currentTimeMillis()).toString();
    }

    @Override
    public void log(String s) {
        synchronized (this) {
            print.println("LOG " + getTime() + "[" + owner + "] " + s);
            print.flush();
        }
    }

    @Override
    public void err(String s) {
        synchronized (this) {
            print.println("ERR " + getTime() + "[" + owner + "] " + s);
            print.flush();
        }
    }

    @Override
    public void err(Throwable e) {
        err(Exceptions.toString(e));
    }
}
