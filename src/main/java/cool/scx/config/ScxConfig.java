package cool.scx.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import cool.scx.boot.ScxApp;
import cool.scx.config.example.Scx;
import cool.scx.enumeration.Color;
import cool.scx.util.LogUtils;
import cool.scx.util.NetUtils;
import cool.scx.util.NoCode;
import cool.scx.util.PackageUtils;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 配置文件类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxConfig {

    private static Scx ce;
    private static JsonNode scj;
    private static File jsonPath;

    /**
     * <p>Getter for the field <code>scj</code>.</p>
     *
     * @return a {@link com.fasterxml.jackson.databind.JsonNode} object.
     */
    public static JsonNode getScj() {
        return scj;
    }

    /**
     * <p>checkPort.</p>
     *
     * @param p a int.
     * @return a int.
     */
    public static int checkPort(int p) {
        while (NetUtils.isLocalePortUsing(p)) {
            p = p + 1;
            LogUtils.println("✘ 端口号 [ " + (p - 1) + " ] 已被占用 !!!         \t -->\t 新端口号 : " + p, Color.RED);
        }
        return p;
    }

    /**
     * <p>getScxJsonConfig.</p>
     *
     * @return a {@link com.fasterxml.jackson.databind.JsonNode} object.
     */
    public static JsonNode getScxJsonConfig() {
        JsonNode rootNode = null;
        var mapper = new ObjectMapper();
        try {
            var scxConfigJsons = PackageUtils.getAppRoot().listFiles(file -> file.isFile() && file.getName().startsWith("scx") && file.getName().endsWith(".json"));
            jsonPath = scxConfigJsons[0];
            rootNode = mapper.readTree(jsonPath);
            LogUtils.println("✔ 已加载配置文件                       \t -->\t " + jsonPath.getPath(), Color.GREEN);
        } catch (Exception e) {
            jsonPath = new File(PackageUtils.getAppRoot(), "scx-default.json");
            LogUtils.println("✘ 配置文件已损坏或丢失!!!", Color.RED);
        }
        return rootNode;
    }

    /**
     * <p>getConfigValue.</p>
     *
     * @param keyPath a {@link java.lang.String} object.
     * @param <T>     a T object.
     * @return a T object.
     */
    public static <T> T getConfigValue(String keyPath) {
        return getConfigValue(keyPath, null);
    }

    /**
     * <p>getConfigValue.</p>
     *
     * @param keyPath    a {@link java.lang.String} object.
     * @param defaultVal a T object.
     * @param <T>        a T object.
     * @return a T object.
     */
    @SuppressWarnings("unchecked")
    public static <T> T getConfigValue(String keyPath, T defaultVal) {
        return (T) getConfigValue(keyPath, defaultVal, NoCode::NoCode, NoCode::NoCode, c -> {
            if (c.isArray()) {
                var tempList = new ArrayList<Object>();
                c.forEach(cc -> tempList.add(getValueByJsonNode(cc)));
                return tempList;
            } else {
                return getValueByJsonNode(c);
            }
        }, a -> a);
    }

    /**
     * <p>getConfigValue.</p>
     *
     * @param keyPath       a {@link java.lang.String} object.
     * @param defaultVal    a T object.
     * @param successFun    a {@link java.util.function.Consumer} object.
     * @param failFun       a {@link java.util.function.Consumer} object.
     * @param convertFun    a {@link java.util.function.Function} object.
     * @param convertArgFun a {@link java.util.function.Function} object.
     * @param <T>           a T object.
     * @return a T object.
     */
    public static <T> T getConfigValue(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun, Function<JsonNode, T> convertFun, Function<String, T> convertArgFun) {
        return getConfigValue(keyPath, defaultVal, successFun, failFun, convertFun, convertArgFun, scj);
    }

    /**
     * <p>getConfigValue.</p>
     *
     * @param keyPath       a {@link java.lang.String} object.
     * @param defaultVal    a T object.
     * @param successFun    a {@link java.util.function.Consumer} object.
     * @param failFun       a {@link java.util.function.Consumer} object.
     * @param convertFun    a {@link java.util.function.Function} object.
     * @param convertArgFun a {@link java.util.function.Function} object.
     * @param jsonNodeVal   a {@link com.fasterxml.jackson.databind.JsonNode} object.
     * @param <T>           a T object.
     * @return a T object.
     */
    public static <T> T getConfigValue(String keyPath, T defaultVal, Consumer<T> successFun, Consumer<T> failFun, Function<JsonNode, T> convertFun, Function<String, T> convertArgFun, JsonNode jsonNodeVal) {
        for (String parameter : ScxApp.parameters()) {
            if (parameter.startsWith("--" + keyPath + "=")) {
                String[] split = parameter.split("=");
                if (split.length == 2) {
                    T c = convertArgFun.apply(split[1]);
                    successFun.accept(c);
                    return c;
                }
            }
        }
        var split = keyPath.split("\\.");
        for (String s : split) {
            try {
                jsonNodeVal = jsonNodeVal.get(s);
            } catch (Exception ignored) {
                failFun.accept(defaultVal);
                return defaultVal;
            }
        }
        if (jsonNodeVal != null) {
            T c = convertFun.apply(jsonNodeVal);
            successFun.accept(c);
            return c;
        } else {
            failFun.accept(defaultVal);
            return defaultVal;
        }
    }

    private static Object getValueByJsonNode(JsonNode jsonNode) {
        if (jsonNode.isInt()) {
            return jsonNode.asInt();
        }
        if (jsonNode.isLong()) {
            return jsonNode.asLong();
        }
        if (jsonNode.isDouble()) {
            return jsonNode.asDouble();
        }
        if (jsonNode.isBoolean()) {
            return jsonNode.asBoolean();
        }
        return jsonNode.asText();
    }


    /**
     * <p>init.</p>
     */
    public static void init() {
        LogUtils.println("ScxConfig 初始化中...", Color.BRIGHT_BLUE);
        scj = getScxJsonConfig();
        ce = new Scx(jsonPath, scj);
        LogUtils.println("ScxConfig 初始化完成...", Color.BRIGHT_BLUE);
    }

    /**
     * <p>reloadConfig.</p>
     */
    public static void reloadConfig() {
        scj = getScxJsonConfig();
        ce = new Scx(jsonPath, scj);
        LogUtils.println("ScxConfig 重新加载完成...", Color.BRIGHT_BLUE);
    }

    /**
     * <p>cookieKey.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String cookieKey() {
        return "S-Cookie";
    }

    /**
     * <p>tokenKey.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String tokenKey() {
        return "S-Token";
    }

    /**
     * <p>openHttps.</p>
     *
     * @return a boolean.
     */
    public static boolean openHttps() {
        return ce.https.isOpen;
    }

    /**
     * <p>port.</p>
     *
     * @return a int.
     */
    public static int port() {
        return ce.port;
    }

    /**
     * <p>certPath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File certPath() {
        return ce.https.certPathValue;
    }

    /**
     * <p>certPassword.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String certPassword() {
        return ce.https.certificatePasswordValue;
    }

    /**
     * <p>showLog.</p>
     *
     * @return a boolean.
     */
    public static boolean showLog() {
        return ce.showLog;
    }

    /**
     * <p>fixTable.</p>
     *
     * @return a boolean.
     */
    public static boolean fixTable() {
        return ce.fixTable;
    }

    /**
     * <p>realDelete.</p>
     *
     * @return a boolean.
     */
    public static boolean realDelete() {
        return ce.realDelete;
    }

    /**
     * <p>loginErrorLockTimes.</p>
     *
     * @return a int.
     */
    public static int loginErrorLockTimes() {
        return ce.loginErrorLockTimes;
    }

    /**
     * <p>loginErrorLockSecond.</p>
     *
     * @return a long.
     */
    public static long loginErrorLockSecond() {
        return ce.loginErrorLockSecond;
    }

    /**
     * <p>cmsRoot.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File cmsRoot() {
        return ce.cms.rootValue;
    }

    /**
     * <p>pluginRoot.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File pluginRoot() {
        return ce.plugin.rootValue;
    }

    /**
     * <p>pluginDisabledList.</p>
     *
     * @return a {@link java.util.Set} object.
     */
    public static Set<String> pluginDisabledList() {
        return ce.plugin.disabledList;
    }

    /**
     * <p>bodyLimit.</p>
     *
     * @return a long.
     */
    public static long bodyLimit() {
        return ce.bodyLimitValue;
    }

    /**
     * <p>cmsFaviconIcoPath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File cmsFaviconIcoPath() {
        return ce.cms.faviconIcoPathValue;
    }

    /**
     * <p>allowedOrigin.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String allowedOrigin() {
        return ce.allowedOrigin;
    }

    /**
     * <p>cmsResourceUrl.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String cmsResourceUrl() {
        return ce.cms.resourceHttpUrl;
    }

    /**
     * <p>cmsResourceLocations.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File cmsResourceLocations() {
        return ce.cms.resourceLocationsValue;
    }

    /**
     * <p>dataSourceUrl.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourceUrl() {
        return ce.dataSource.url;
    }

    /**
     * <p>dataSourceUsername.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourceUsername() {
        return ce.dataSource.username;
    }

    /**
     * <p>dataSourcePassword.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String dataSourcePassword() {
        return ce.dataSource.passwordValue;
    }

    /**
     * <p>dateTimeFormatter.</p>
     *
     * @return a {@link java.time.format.DateTimeFormatter} object.
     */
    public static DateTimeFormatter dateTimeFormatter() {
        return ce.dateTimeFormatter;
    }

    /**
     * <p>uploadFilePath.</p>
     *
     * @return a {@link java.io.File} object.
     */
    public static File uploadFilePath() {
        return ce.uploadFilePathValue;
    }

    /**
     * <p>scxVersion.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String scxVersion() {
        return "0.9.7";
    }

    /**
     * <p>showGui.</p>
     *
     * @return a boolean.
     */
    public static boolean showGui() {
        return ce.showGui;
    }

    /**
     * <p>license.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String license() {
        return ce.license;
    }

    /**
     * <p>cmsResourceSuffix.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String cmsTemplateSuffix() {
        return ce.cms.templateSuffix;
    }

    /**
     * <p>confusionLoginError.</p>
     *
     * @return a boolean.
     */
    public static boolean confusionLoginError() {
        return ce.confusionLoginError;
    }

    /**
     * <p>AppKey.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String AppKey() {
        return "H8QS91GcuNGP9735";
    }
}
