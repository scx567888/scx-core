package cool.scx;

/**
 * <p>ScxClassLoader class.</p>
 *
 * @author scx567888
 * @version 1.3.0
 */
public class ScxClassLoader extends ClassLoader {
    /**
     * {@inheritDoc}
     */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return super.findClass(name);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        return super.loadClass(name);
    }
}
