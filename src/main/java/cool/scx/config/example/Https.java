package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.util.Ansi;
import cool.scx.util.CryptoUtils;
import cool.scx.util.PackageUtils;
import cool.scx.util.Tidy;

import java.io.File;
import java.util.concurrent.atomic.AtomicBoolean;

import static cool.scx.config.ScxConfig.getConfigValue;

/**
 * <p>Https class.</p>
 *
 * @author scx56
 * @version $Id: $Id
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

    /**
     * <p>Constructor for Https.</p>
     *
     * @param needFixConfig a {@link java.util.concurrent.atomic.AtomicBoolean} object.
     * @return a {@link cool.scx.config.example.Https} object.
     */
    public static Https from(AtomicBoolean needFixConfig) {
        var https = new Https();
        https.isOpen = getConfigValue("scx.https.is-open", false,
                s -> Ansi.OUT.green("✔ 是否开启 https                       \t -->\t " + (s ? "是" : "否")).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("✘ 未检测到 scx.https.is-open            \t -->\t 已采用默认值 : " + f).ln();
                }, JsonNode::asBoolean, Boolean::valueOf);

        https.sslPath = getConfigValue("scx.https.ssl-path", "",
                s -> Ansi.OUT.green("✔ 证书路径                           \t -->\t " + PackageUtils.getFileByAppRoot(s)).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("✘ 未检测到 scx.https.ssl-path       \t -->\t 请检查证书路径是否正确").ln();
                }, JsonNode::asText, a -> a);

        https.sslPathValue = PackageUtils.getFileByAppRoot(https.sslPath);

        https.sslPassword = getConfigValue("scx.https.ssl-password", "",
                Tidy::NoCode,
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("✘ 未检测到 scx.https.ssl-password      \t -->\t 请检查证书密码是否正确").ln();
                }, JsonNode::asText, a -> a);

        if (https.isOpen) {
            var tempSSLPasswordValue = "";
            try {
                tempSSLPasswordValue = CryptoUtils.decryptText(https.sslPassword);
            } catch (Exception e) {
                Ansi.OUT.red("✘ 解密 scx.https.ssl-password  出错        \t -->\t 请检查证书密码是否正确").ln();
            }
            https.sslPasswordValue = tempSSLPasswordValue;
        } else {
            https.sslPasswordValue = "";
        }
        return https;
    }
}
