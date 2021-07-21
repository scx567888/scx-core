package cool.scx.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.IMarkerFactory;
import org.slf4j.spi.MDCAdapter;
import org.slf4j.spi.SLF4JServiceProvider;

public class ScxServiceProvider implements SLF4JServiceProvider {

    private static final String REQUESTED_API_VERSION = "1.8.99";
    private final ILoggerFactory loggerFactory = new ScxLoggerFactory();
    private final IMarkerFactory markerFactory = new ScxMarkerFactory();
    private final MDCAdapter mdcAdapter = new ScxMDCAdapter();

    public ScxServiceProvider() {

    }

    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }

    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }

    public MDCAdapter getMDCAdapter() {
        return this.mdcAdapter;
    }

    public String getRequesteApiVersion() {
        return REQUESTED_API_VERSION;
    }

    public void initialize() {

    }

}
