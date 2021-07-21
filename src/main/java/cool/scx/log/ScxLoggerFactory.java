package cool.scx.log;

import org.slf4j.ILoggerFactory;
import org.slf4j.Logger;

public class ScxLoggerFactory implements ILoggerFactory {

    public ScxLoggerFactory() {
    }

    public Logger getLogger(String name) {
        return new ScxLogger(name);
    }
}
