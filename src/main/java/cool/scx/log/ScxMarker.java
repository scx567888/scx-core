package cool.scx.log;

import org.slf4j.Marker;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * A simple implementation of the {@link org.slf4j.Marker} interface.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Joern Huxhorn
 * @version 1.3.0
 */
public class ScxMarker implements Marker {

    private static final long serialVersionUID = -2849567615646933777L;
    private static String OPEN = "[ ";
    private static String CLOSE = " ]";
    private static String SEP = ", ";
    private final String name;
    private List<Marker> referenceList = new CopyOnWriteArrayList<>();

    ScxMarker(String name) {
        if (name == null) {
            throw new IllegalArgumentException("A marker name cannot be null");
        }
        this.name = name;
    }

    /**
     * <p>Getter for the field <code>name</code>.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    public void add(Marker reference) {
        if (reference == null) {
            throw new IllegalArgumentException("A null value cannot be added to a Marker as reference.");
        }

        // no point in adding the reference multiple times
        if (this.contains(reference)) {
            return;

        } else if (reference.contains(this)) { // avoid recursion
            // a potential reference should not hold its future "parent" as a reference
            return;
        } else {
            referenceList.add(reference);
        }
    }

    /**
     * <p>hasReferences.</p>
     *
     * @return a boolean
     */
    public boolean hasReferences() {
        return (referenceList.size() > 0);
    }

    /**
     * <p>hasChildren.</p>
     *
     * @return a boolean
     */
    @Deprecated
    public boolean hasChildren() {
        return hasReferences();
    }

    /**
     * <p>iterator.</p>
     *
     * @return a {@link java.util.Iterator} object
     */
    public Iterator<Marker> iterator() {
        return referenceList.iterator();
    }

    /**
     * {@inheritDoc}
     */
    public boolean remove(Marker referenceToRemove) {
        return referenceList.remove(referenceToRemove);
    }

    /**
     * <p>contains.</p>
     *
     * @param other a {@link org.slf4j.Marker} object
     * @return a boolean
     */
    public boolean contains(Marker other) {
        if (other == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }

        if (this.equals(other)) {
            return true;
        }

        if (hasReferences()) {
            for (Marker ref : referenceList) {
                if (ref.contains(other)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     * <p>
     * This method is mainly used with Expression Evaluators.
     */
    public boolean contains(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Other cannot be null");
        }

        if (this.name.equals(name)) {
            return true;
        }

        if (hasReferences()) {
            for (Marker ref : referenceList) {
                if (ref.contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Marker))
            return false;

        final Marker other = (Marker) obj;
        return name.equals(other.getName());
    }

    /**
     * <p>hashCode.</p>
     *
     * @return a int
     */
    public int hashCode() {
        return name.hashCode();
    }

    /**
     * <p>toString.</p>
     *
     * @return a {@link java.lang.String} object
     */
    public String toString() {
        if (!this.hasReferences()) {
            return this.getName();
        }
        Iterator<Marker> it = this.iterator();
        Marker reference;
        StringBuilder sb = new StringBuilder(this.getName());
        sb.append(' ').append(OPEN);
        while (it.hasNext()) {
            reference = it.next();
            sb.append(reference.getName());
            if (it.hasNext()) {
                sb.append(SEP);
            }
        }
        sb.append(CLOSE);

        return sb.toString();
    }
}
