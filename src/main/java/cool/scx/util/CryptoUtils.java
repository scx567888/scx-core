package cool.scx.util;

import cool.scx.boot.ScxConfig;
import org.jasypt.util.text.AES256TextEncryptor;

public final class CryptoUtils {

    private CryptoUtils() {
    }

    /**
     * 加密密码
     *
     * @param password 需要加密的密码
     * @param salt     盐值
     * @return 加密后的密码
     */
    public static String encryptPassword(String password, String salt) {
        return init(salt + ScxConfig.AppKey).encrypt(password);
    }

    private static AES256TextEncryptor init(String str) {
        var encryptors = new AES256TextEncryptor();
        encryptors.setPassword(str);
        return encryptors;
    }

    public static String decryptPassword(String encryptedPassword, String salt) {
        return init(salt + ScxConfig.AppKey).decrypt(encryptedPassword);
    }

    public static String encryptText(String text) {
        return init(ScxConfig.AppKey).encrypt(text);
    }

    public static String decryptText(String text) {
        return init(ScxConfig.AppKey).decrypt(text);
    }

}
