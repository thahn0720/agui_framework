package thahn.java.agui.utils;


/**
 * Simple interface for printing text, allowing redirection to various
 * targets.  Standard implementations are {@link android.util.LogPrinter},
 * {@link android.util.StringBuilderPrinter}, and
 * {@link android.util.PrintWriterPrinter}.
 */
public interface Printer {
    /**
     * Write a line of text to the output.  There is no need to terminate
     * the given string with a newline.
     */
    void println(String x);
}
