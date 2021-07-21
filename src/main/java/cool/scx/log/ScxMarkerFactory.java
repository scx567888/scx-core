package cool.scx.log;

import org.slf4j.IMarkerFactory;
import org.slf4j.Marker;
import org.slf4j.helpers.BasicMarker;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * An almost trivial implementation of the {@link org.slf4j.IMarkerFactory}
 * interface which creates {@link org.slf4j.helpers.BasicMarker} instances.
 *
 * <p>Simple logging systems can conform to the SLF4J API by binding
 * {@link org.slf4j.MarkerFactory} with an instance of this class.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @version 1.3.0
 */
public class ScxMarkerFactory implements IMarkerFactory {

    private final ConcurrentMap<String, Marker> markerMap = new ConcurrentHashMap<String, Marker>();

    /**
     * Regular users should <em>not</em> create
     * <code>BasicMarkerFactory</code> instances. <code>Marker</code>
     * instances can be obtained using the static {@link
     * org.slf4j.MarkerFactory#getMarker} method.
     */
    public ScxMarkerFactory() {
    }

    /**
     * {@inheritDoc}
     * <p>
     * Manufacture a {@link BasicMarker} instance by name. If the instance has been
     * created earlier, return the previously created instance.
     */
    public Marker getMarker(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Marker name cannot be null");
        }

        Marker marker = markerMap.get(name);
        if (marker == null) {
            marker = new ScxMarker(name);
            Marker oldMarker = markerMap.putIfAbsent(name, marker);
            if (oldMarker != null) {
                marker = oldMarker;
            }
        }
        return marker;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Does the name marked already exist?
     */
    public boolean exists(String name) {
        if (name == null) {
            return false;
        }
        return markerMap.containsKey(name);
    }

    /**
     * {@inheritDoc}
     */
    public boolean detachMarker(String name) {
        if (name == null) {
            return false;
        }
        return (markerMap.remove(name) != null);
    }

    /**
     * {@inheritDoc}
     */
    public Marker getDetachedMarker(String name) {
        return new ScxMarker(name);
    }

}
