package cool.scx.log;

import cool.scx.enumeration.AnsiColor;
import cool.scx.util.Ansi;
import org.slf4j.Marker;
import org.slf4j.event.Level;
import org.slf4j.helpers.AbstractLogger;
import org.slf4j.helpers.MessageFormatter;

/**
 * <p>ScxLogger class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public class ScxLogger extends AbstractLogger {

    private final String name;

    /**
     * <p>Constructor for ScxLogger.</p>
     *
     * @param name a {@link java.lang.String} object
     */
    protected ScxLogger(String name) {
        this.name = name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return "SCX";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getFullyQualifiedCallerName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
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


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraceEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isTraceEnabled(Marker marker) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDebugEnabled(Marker marker) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isInfoEnabled(Marker marker) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWarnEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isWarnEnabled(Marker marker) {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErrorEnabled() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isErrorEnabled(Marker marker) {
        return true;
    }

}
