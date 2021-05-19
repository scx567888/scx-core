package cool.scx.boot;


import cool.scx.base.BaseModule;

/**
 * 最基本的参数处理
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class ScxParameters {

    private static String[] parameters = new String[0];

    /**
     * 初始化 参数
     *
     * @param _args an array of {@link java.lang.String} objects.
     */
    public static void initParameters(String... _args) {
        parameters = _args;
    }

    /**
     * 获取 从外部传来的参数 (java -jar scx.jar  xxx)
     *
     * @return 外部传来的参数
     */
    public static String[] parameters() {
        return parameters;
    }

}
