package cool.scx.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

/**
 * <p>ScxLoggerFactory class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public class ScxLoggerFactory implements ILoggerFactory {

    /**
     * <p>Constructor for ScxLoggerFactory.</p>
     */
    public ScxLoggerFactory() {
    }

    /**
     * {@inheritDoc}
     */
    public Logger getLogger(String name) {
        return new ScxLogger(name);
    }
}
