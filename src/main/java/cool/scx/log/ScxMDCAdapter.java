package cool.scx.log;

import org.slf4j.spi.MDCAdapter;

import java.util.Map;


public class ScxMDCAdapter implements MDCAdapter {

    public void clear() {
    }

    public String get(String key) {
        return null;
    }

    public void put(String key, String val) {
    }

    public void remove(String key) {
    }

    public Map<String, String> getCopyOfContextMap() {
        return null;
    }

    public void setContextMap(Map<String, String> contextMap) {
        // NOP
    }

}
