package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.CryptoUtils;
import cool.scx.util.FileUtils;
import cool.scx.util.Tidy;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Https class.</p>
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class Https {
    /**
     * 是否开启 https
     */
    public boolean isOpen;
    /**
     * ssl 证书路径 字符串值
     */
    public String sslPath;
    /**
     * ssl 证书路径 真实值
     */
    @JsonIgnore
    public File sslPathValue;
    /**
     * ssl 证书密码 字符串值
     */
    public String sslPassword;
    /**
     * ssl 证书密码 真实值 (解密后)
     */
    @JsonIgnore
    public String sslPasswordValue;

    private Https() {
    }

    /**
     * 从配置文件加载
     *
     * @param needFixConfig a {@link java.util.concurrent.atomic.AtomicBoolean} object.
     * @return a {@link cool.scx.config.example.Https} object.
     */
    public static Https from(AtomicBoolean needFixConfig) {
        var https = new Https();

        https.isOpen = ScxConfig.value("scx.https.is-open", false,
                s -> Ansi.OUT.green("Y 是否开启 https                       \t -->\t " + (s ? "是" : "否")).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.https.is-open           \t -->\t 已采用默认值 : " + f).ln();
                });

        https.sslPath = ScxConfig.value("scx.https.ssl-path", "",
                s -> Ansi.OUT.green("Y 证书路径                            \t -->\t " + FileUtils.getFileByRootModulePath(s)).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.https.ssl-path         \t -->\t 请检查证书路径是否正确").ln();
                });

        https.sslPathValue = FileUtils.getFileByRootModulePath(https.sslPath);

        https.sslPassword = ScxConfig.value("scx.https.ssl-password", "",
                Tidy::NoCode,
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.https.ssl-password      \t -->\t 请检查证书密码是否正确").ln();
                });

        if (https.isOpen) {
            try {
                https.sslPasswordValue = CryptoUtils.decryptText(https.sslPassword);
            } catch (Exception e) {
                Ansi.OUT.red("N 解密 scx.https.ssl-password  出错        \t -->\t 请检查证书密码是否正确").ln();
            }
        } else {
            https.sslPasswordValue = "";
        }
        return https;
    }
}
