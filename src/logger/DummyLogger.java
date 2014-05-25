package logger;

public class DummyLogger implements Logger {

    @Override
    public void log(String s) {}

    @Override
    public void err(String s) {}

    @Override
    public void err(Throwable e) {}
}
