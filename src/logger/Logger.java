package logger;

public interface Logger {

    void log(String s);
    void err(String s);
    void err(Throwable e);
}
