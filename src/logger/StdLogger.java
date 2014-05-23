package logger;

import java.sql.Time;

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
            System.out.println(getTime() + " [" + owner + "] " + s);
        }
    }

    @Override
    public void err(String s) {
        synchronized (this) {
            System.err.println(getTime() + " [" + owner + "] " + s);
        }
    }

}
