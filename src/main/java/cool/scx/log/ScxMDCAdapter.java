package cool.scx.log;

import org.slf4j.spi.MDCAdapter;

import java.util.Map;


/**
 * <p>ScxMDCAdapter class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public class ScxMDCAdapter implements MDCAdapter {

    /**
     * <p>clear.</p>
     */
    public void clear() {
    }

    /**
     * {@inheritDoc}
     */
    public String get(String key) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void put(String key, String val) {
    }

    /**
     * {@inheritDoc}
     */
    public void remove(String key) {
    }

    /**
     * <p>getCopyOfContextMap.</p>
     *
     * @return a {@link java.util.Map} object
     */
    public Map<String, String> getCopyOfContextMap() {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void setContextMap(Map<String, String> contextMap) {
        // NOP
    }

}
