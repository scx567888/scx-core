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

    public static JsonNode getScj() {
        return scj;
    }

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
            jsonPath=scxConfigJsons[0];
            rootNode = mapper.readTree(jsonPath);
            LogUtils.println("✔ 已加载配置文件                       \t -->\t " + jsonPath.getPath(), Color.GREEN);
        } catch (Exception e) {
            jsonPath=new File(PackageUtils.getAppRoot(),"scx-default.json");
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
        ce = new Scx(jsonPath,scj);
        LogUtils.println("ScxConfig 初始化完成...", Color.BRIGHT_BLUE);
    }

    public static void reloadConfig() {
        scj = getScxJsonConfig();
        ce = new Scx(jsonPath,scj);
        LogUtils.println("ScxConfig 重新加载完成...", Color.BRIGHT_BLUE);
    }

    public static String cookieKey() {
        return "S-Cookie";
    }

    public static String tokenKey() {
        return "S-Token";
    }

    public static boolean openHttps() {
        return ce.https.isOpen;
    }

    public static int port() {
        return ce.port;
    }

    public static File certPath() {
        return ce.https.certPathValue;
    }

    public static String certPassword() {
        return ce.https.certificatePasswordValue;
    }

    public static boolean showLog() {
        return ce.showLog;
    }

    public static boolean fixTable() {
        return ce.fixTable;
    }

    public static boolean realDelete() {
        return ce.realDelete;
    }

    public static int loginErrorLockTimes() {
        return ce.loginErrorLockTimes;
    }

    public static long loginErrorLockSecond() {
        return ce.loginErrorLockSecond;
    }

    public static File cmsRoot() {
        return ce.cms.rootValue;
    }

    public static File pluginRoot() {
        return ce.plugin.rootValue;
    }

    public static Set<String> pluginDisabledList() {
        return ce.plugin.disabledList;
    }

    public static long bodyLimit() {
        return ce.bodyLimitValue;
    }

    public static File cmsFaviconIcoPath() {
        return ce.cms.faviconIcoPathValue;
    }

    public static String allowedOrigin() {
        return ce.allowedOrigin;
    }

    public static String cmsResourceUrl() {
        return ce.cms.resourceHttpUrl;
    }

    public static File cmsResourceLocations() {
        return ce.cms.resourceLocationsValue;
    }

    public static String dataSourceUrl() {
        return ce.dataSource.url;
    }

    public static String dataSourceUsername() {
        return ce.dataSource.username;
    }

    public static String dataSourcePassword() {
        return ce.dataSource.passwordValue;
    }

    public static DateTimeFormatter dateTimeFormatter() {
        return ce.dateTimeFormatter;
    }

    public static File uploadFilePath() {
        return ce.uploadFilePathValue;
    }

    public static String scxVersion() {
        return "0.9.7";
    }

    public static boolean showGui() {
        return ce.showGui;
    }

    public static String license() {
        return ce.license;
    }

    public static String cmsResourceSuffix() {
        return ce.cms.resourceHttpUrl;
    }

    public static boolean confusionLoginError() {
        return ce.confusionLoginError;
    }

    public static String AppKey() {
        return "H8QS91GcuNGP9735";
    }
}
