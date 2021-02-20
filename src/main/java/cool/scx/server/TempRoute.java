package cool.scx.server;

import cool.scx.annotation.ScxMapping;

import java.lang.reflect.Method;

class TempRoute {
    String url;
    Method method;
    Class<?> clazz;
    ScxMapping scxMapping;

    public TempRoute(String url, Method method, Class<?> clazz, ScxMapping scxMapping) {
        this.url = url;
        this.method = method;
        this.clazz = clazz;
        this.scxMapping = scxMapping;
    }
}