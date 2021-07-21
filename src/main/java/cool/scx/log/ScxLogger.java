package cool.scx.log;

import cool.scx.enumeration.AnsiColor;
import cool.scx.util.Ansi;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.MessageFormatter;

public class ScxLogger extends AbstractLogger {

    private final String name;

    protected ScxLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return "SCX";
    }

    @Override
    protected String getFullyQualifiedCallerName() {
        return name;
    }

    @Override
    protected void handleNormalizedLoggingCall(Level level, Marker marker, String msg, Object[] arguments, Throwable throwable) {
        var c = AnsiColor.DEFAULT_COLOR;
        switch (level) {
            case ERROR:
                c = AnsiColor.BRIGHT_RED;
                break;
            case WARN:
                c = AnsiColor.BRIGHT_YELLOW;
                break;
            case INFO:
                c = AnsiColor.BRIGHT_MAGENTA;
                break;
            case DEBUG:
                c = AnsiColor.BRIGHT_GREEN;
                break;
            case TRACE:
                c = AnsiColor.BRIGHT_BLUE;
                break;
        }

        var messageFormatter = MessageFormatter.arrayFormat(msg, arguments, throwable);

        var logMessage = Ansi.out()
                .color("[", c)
                .color(name, c)
                .color("]", c)
                .color(" ")
                .color(messageFormatter.getMessage(), c)
                .color(" ");

        if (throwable != null) {
            logMessage.color(messageFormatter.getThrowable()).println();
        } else {
            logMessage.println();
        }
    }


    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    @Override
    public boolean isTraceEnabled(Marker marker) {
        return true;
    }

    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    @Override
    public boolean isDebugEnabled(Marker marker) {
        return true;
    }

    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    @Override
    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    @Override
    public boolean isWarnEnabled(Marker marker) {
        return true;
    }

    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    @Override
    public boolean isErrorEnabled(Marker marker) {
        return true;
    }

}
