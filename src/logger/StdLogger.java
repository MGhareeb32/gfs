package logger;

import java.sql.Time;

import utils.Exceptions;

public class StdLogger implements Logger {

    private final String owner;

    public StdLogger(String owner) {
        this.owner = owner;
    }

    private String getTime() {
        return new Time(System.currentTimeMillis()).toString();
    }

    @Override
    public void log(String s) {
        synchronized (this) {
            System.out.println("LOG " + getTime() + "[" + owner + "] " + s);
        }
    }

    @Override
    public void err(String s) {
        synchronized (this) {
            System.out.println("ERR " + getTime() + "[" + owner + "] " + s);
        }
    }

    @Override
    public void err(Throwable e) {
        err(Exceptions.toString(e));
    }
}
