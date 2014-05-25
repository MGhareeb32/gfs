package utils;

import java.io.PrintWriter;
import java.io.StringWriter;

public class Exceptions {

    public static String toString(Throwable e) {
        StringWriter w = new StringWriter();
        PrintWriter out = new PrintWriter(w);
        e.printStackTrace(out);
        return w.toString();
    }
}
