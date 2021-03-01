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

public class Https {
    /**
     * 是否开启 https
     */
    public final boolean isOpen;
    /**
     * ssh 证书路径 字符串值
     */
    public final String certificatePath;
    /**
     * ssh 证书路径 真实值
     */
    @JsonIgnore
    public final File certificatePathValue;
    /**
     * ssh 证书密码 字符串值
     */
    public final String certificatePassword;
    /**
     * ssh 证书密码 真实值 (解密后)
     */
    @JsonIgnore
    public final String certificatePasswordValue;

    public Https(AtomicBoolean needFixConfig) {

        this.isOpen = getConfigValue("scx.https.is-open", false,
                s -> LogUtils.println("✔ 是否开启 https                       \t -->\t " + (s ? "是" : "否"), Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.https.is-open            \t -->\t 已采用默认值 : " + f, Color.RED);
                }, JsonNode::asBoolean, Boolean::valueOf);

            this.certificatePath = getConfigValue("scx.https.certificate-path", "",
                    s -> LogUtils.println("✔ 证书路径                           \t -->\t " + PackageUtils.getFileByAppRoot(s), Color.GREEN),
                    f -> {
                        needFixConfig.set(true);
                        LogUtils.println("✘ 未检测到 scx.https.certificate-path   \t -->\t 请检查证书路径是否正确", Color.RED);
                    }, JsonNode::asText, a -> a);

            this.certificatePathValue = PackageUtils.getFileByAppRoot(certificatePath);

            this.certificatePassword = getConfigValue("scx.https.certificate-password", "",
                    NoCode::NoCode,
                    f -> {
                        needFixConfig.set(true);
                        LogUtils.println("✘ 未检测到 scx.https.certificate-password     \t -->\t 请检查证书密码是否正确", Color.RED);
                    }, JsonNode::asText, a -> a);

        if (this.isOpen) {
            var tempCertificatePasswordValue = "";
            try {
                tempCertificatePasswordValue = CryptoUtils.decryptText(this.certificatePassword);
            } catch (Exception e) {
                LogUtils.println("✘ 解密 scx.https.certificate-password  出错        \t -->\t 请检查证书密码是否正确", Color.RED);
            }
            this.certificatePasswordValue = tempCertificatePasswordValue;
        }else{
            this.certificatePasswordValue="";
        }

    }
}
