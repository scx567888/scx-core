package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import cool.scx.util.Ansi;
import cool.scx.util.NetUtils;
import cool.scx.util.PackageUtils;
import cool.scx.util.Tidy;
import cool.scx.util.file.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static cool.scx.config.ScxConfig.getConfigValue;

/**
 * <p>Scx class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class Scx {
    /**
     * 端口号
     */
    public int port;
    /**
     * 允许的 域
     */
    public String allowedOrigin;

    /**
     * 文件上传路径
     */
    public String uploadFilePath;

    @JsonIgnore
    public File uploadFilePathValue;

    /**
     * 混淆登录
     */
    public boolean confusionLoginError;
    /**
     * license
     */
    public String license;

    /**
     * 是否显示 log
     */
    public boolean showLog;
    /**
     * 是否显示 gui
     */
    public boolean showGui;
    /**
     * 是否真实删除
     */
    public boolean realDelete;

    /**
     * 登录错误锁定次数
     */
    public int loginErrorLockTimes;
    /**
     * 登录错误锁定时间
     */
    public int loginErrorLockSecond;
    /**
     * 修复表格
     */
    public boolean fixTable;

    /**
     * 日期格式化格式
     */
    @JsonIgnore
    public DateTimeFormatter dateTimeFormatter;

    public String dateTimePattern;

    /**
     * request body 大小限制 字符串值
     */
    public String bodyLimit;
    /**
     * 真实值
     */
    @JsonIgnore
    public long bodyLimitValue;

    public Plugin plugin;
    public DataSource dataSource;
    public Https https;
    public Cms cms;

    /**
     * 在获取各个值时 如果发生错误就 修复 配置文件
     *
     * @param configPath  配置文件的路径 当配置文件损坏时会根据此路径修复配置文件
     * @param oldJsonNode 读取到的旧配置文件 如果 配置文件损坏会根据旧配置文件 保留其旧配置项
     * @return a {@link cool.scx.config.example.Scx} object.
     */
    public static Scx from(File configPath, JsonNode oldJsonNode) {
        //是否需要修复配置文件
        var needFixConfig = new AtomicBoolean(false);
        var scx = new Scx();

        scx.port = getConfigValue("scx.port", 8080,
                s -> Ansi.ANSI.green("✔ 服务器 IP 地址                       \t -->\t " + NetUtils.getLocalAddress() + "\r\n✔ 端口号                               \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.port                  \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asInt, Integer::parseInt);

        scx.cms = Cms.from(needFixConfig);

        scx.plugin = Plugin.from(needFixConfig);

        scx.uploadFilePath = getConfigValue("scx.upload-file-path", "/scxUploadFile/",
                s -> Ansi.ANSI.green("✔ 文件上传目录                         \t -->\t " + PackageUtils.getFileByAppRoot(s)).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.upload-file-path        \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asText, a -> a);

        scx.uploadFilePathValue = PackageUtils.getFileByAppRoot(scx.uploadFilePath);

        scx.bodyLimit = getConfigValue("scx.body-limit", "16384KB",
                s -> Ansi.ANSI.green("✔ 请求体大小限制                       \t -->\t " + FileUtils.longToDisplaySize(FileUtils.displaySizeToLong(s))).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.body-limit             \t -->\t 已采用默认值 : " + FileUtils.longToDisplaySize(FileUtils.displaySizeToLong(f))).ln();
                }, JsonNode::asText, a -> a);

        scx.bodyLimitValue = FileUtils.displaySizeToLong(scx.bodyLimit);

        scx.confusionLoginError = getConfigValue("scx.confusion-login-error", false,
                s -> Ansi.ANSI.green("✔ 是否混淆登录错误                     \t -->\t " + (s ? "是" : "否")).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.confusion-login-error \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asBoolean, Boolean::valueOf);

        scx.loginErrorLockTimes = getConfigValue("scx.login-error-lock-times", 999,
                s -> Ansi.ANSI.green("✔ 登录错误锁定次数                     \t -->\t " + s + " 次").ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.login-error-lock-times \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asInt, Integer::valueOf);

        scx.loginErrorLockSecond = getConfigValue("scx.login-error-lock-second", 10,
                s -> Ansi.ANSI.green("✔ 登录错误锁定的时间                    \t -->\t " + s + " 秒").ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.login-error-lock-second \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asInt, Integer::valueOf);

        scx.showLog = getConfigValue("scx.show-log", true,
                s -> Ansi.ANSI.green("✔ 是否将错误日志打印到控制台             \t -->\t " + (s ? "是" : "否")).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.show-log              \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asBoolean, Boolean::valueOf);

        scx.showGui = getConfigValue("scx.show-gui", false,
                s -> Ansi.ANSI.green("✔ 是否将显示 GUI                      \t -->\t " + (s ? "是" : "否")).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.show-gui              \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asBoolean, Boolean::valueOf);

        scx.realDelete = getConfigValue("scx.real-delete", false,
                s -> Ansi.ANSI.green("✔ 数据库删除方式为                     \t -->\t " + (s ? "物理删除" : "逻辑删除")).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.real-delete           \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asBoolean, Boolean::valueOf);

        scx.license = getConfigValue("scx.license", "", Tidy::NoCode,
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.license               \t -->\t 请检查 license 是否正确").ln();
                }, JsonNode::asText, (a) -> a);

        scx.https = Https.from(needFixConfig);

        scx.dateTimePattern = getConfigValue("scx.date-time-pattern", "yyyy-MM-dd HH:mm:ss",
                s -> Ansi.ANSI.green("✔ 日期格式为                          \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.date-time-pattern        \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asText, (a) -> a);

        scx.dateTimeFormatter = DateTimeFormatter.ofPattern(scx.dateTimePattern);


        scx.allowedOrigin = getConfigValue("scx.allowed-origin", "*",
                s -> Ansi.ANSI.green("✔ 允许的请求源                         \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.allowed-origin           \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asText, (a) -> a);

        scx.dataSource = DataSource.from(needFixConfig);

        scx.fixTable = getConfigValue("scx.fix-table", false,
                s -> Ansi.ANSI.green("✔ 修复数据表                          \t -->\t " + (s ? "是" : "否")).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.ANSI.red("✘ 未检测到 scx.fix-table               \t -->\t 已采用默认值 : " + f).ln();
                },
                JsonNode::asBoolean, Boolean::valueOf);

        //如果需要修复配置文件
        if (needFixConfig.get()) {
            try (var outputStream = new FileOutputStream(configPath)) {
                //为了保证原来配置文件中的数据不被覆盖 这里采用深拷贝 并合并对象的方式
                var objectMapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.KEBAB_CASE);
                var newConfig = new HashMap<String, Scx>();
                newConfig.put("scx", scx);
                //合并新的和旧的配置文件 并写入到 配置文件的路径中
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(outputStream, objectMapper.updateValue(oldJsonNode.deepCopy(), newConfig));
                Ansi.ANSI.brightGreen("✔ 因配置文件损坏,已自动修复配置文件           \t -->\t " + configPath).ln();
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return scx;
    }

}
