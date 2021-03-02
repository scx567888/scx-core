package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.enumeration.Color;
import cool.scx.util.CryptoUtils;
import cool.scx.util.LogUtils;
import cool.scx.util.NoCode;
import cool.scx.util.PackageUtils;

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
     * ssh 证书路径 字符串值
     */
    public String certPath;
    /**
     * ssh 证书路径 真实值
     */
    @JsonIgnore
    public File certPathValue;
    /**
     * ssh 证书密码 字符串值
     */
    public String certPassword;
    /**
     * ssh 证书密码 真实值 (解密后)
     */
    @JsonIgnore
    public String certificatePasswordValue;

    /**
     * <p>Constructor for Https.</p>
     *
     * @param needFixConfig a {@link java.util.concurrent.atomic.AtomicBoolean} object.
     * @return a {@link cool.scx.config.example.Https} object.
     */
    public static Https from(AtomicBoolean needFixConfig) {
        var https = new Https();
        https.isOpen = getConfigValue("scx.https.is-open", false,
                s -> LogUtils.println("✔ 是否开启 https                       \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.https.is-open            \t -->\t 已采用默认值 : " + f, Color.RED);
                }, JsonNode::asBoolean, Boolean::valueOf);

        https.certPath = getConfigValue("scx.https.cert-path", "",
                s -> LogUtils.println("✔ 证书路径                           \t -->\t " + PackageUtils.getFileByAppRoot(s), Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.https.cert-path       \t -->\t 请检查证书路径是否正确", Color.RED);
                }, JsonNode::asText, a -> a);

        https.certPathValue = PackageUtils.getFileByAppRoot(https.certPath);

        https.certPassword = getConfigValue("scx.https.cert-password", "",
                NoCode::NoCode,
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.https.cert-password      \t -->\t 请检查证书密码是否正确", Color.RED);
                }, JsonNode::asText, a -> a);

        if (https.isOpen) {
            var tempCertificatePasswordValue = "";
            try {
                tempCertificatePasswordValue = CryptoUtils.decryptText(https.certPassword);
            } catch (Exception e) {
                LogUtils.println("✘ 解密 scx.https.certificate-password  出错        \t -->\t 请检查证书密码是否正确", Color.RED);
            }
            https.certificatePasswordValue = tempCertificatePasswordValue;
        } else {
            https.certificatePasswordValue = "";
        }
        return https;
    }
}
