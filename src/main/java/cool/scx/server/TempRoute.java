package cool.scx.server;

import cool.scx.annotation.ScxMapping;

import java.lang.reflect.Method;

class TempRoute {
    String url;
    Method method;
    Class<?> clazz;
    ScxMapping scxMapping;

    /**
     * <p>Constructor for TempRoute.</p>
     *
     * @param url        a {@link java.lang.String} object.
     * @param method     a {@link java.lang.reflect.Method} object.
     * @param clazz      a {@link java.lang.Class} object.
     * @param scxMapping a {@link cool.scx.annotation.ScxMapping} object.
     */
    public TempRoute(String url, Method method, Class<?> clazz, ScxMapping scxMapping) {
        this.url = url;
        this.method = method;
        this.clazz = clazz;
        this.scxMapping = scxMapping;
    }
}
