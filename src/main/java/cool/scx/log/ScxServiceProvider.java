package cool.scx.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

/**
 * <p>ScxServiceProvider class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public class ScxServiceProvider implements SLF4JServiceProvider {

    private static final String REQUESTED_API_VERSION = "1.8.99";
    private final ILoggerFactory loggerFactory = new ScxLoggerFactory();
    private final IMarkerFactory markerFactory = new ScxMarkerFactory();
    private final MDCAdapter mdcAdapter = new ScxMDCAdapter();

    /**
     * <p>Constructor for ScxServiceProvider.</p>
     */
    public ScxServiceProvider() {

    }

    /**
     * <p>Getter for the field <code>loggerFactory</code>.</p>
     *
     * @return a {@link org.slf4j.ILoggerFactory} object
     */
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    /**
     * <p>Getter for the field <code>markerFactory</code>.</p>
     *
     * @return a {@link org.slf4j.IMarkerFactory} object
     */
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }

    /**
     * <p>getMDCAdapter.</p>
     *
     * @return a {@link org.slf4j.spi.MDCAdapter} object
     */
    public MDCAdapter getMDCAdapter() {
        return this.mdcAdapter;
    }

    /**
     * <p>getRequesteApiVersion.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getRequesteApiVersion() {
        return REQUESTED_API_VERSION;
    }

    /**
     * <p>initialize.</p>
     */
    public void initialize() {

    }

}
