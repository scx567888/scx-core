package cool.scx.util;

import cool.scx.config.ScxConfig;
import org.jasypt.util.text.AES256TextEncryptor;

/**
 * 加密解密工具类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class CryptoUtils {

    private CryptoUtils() {
    }

    /**
     * @param encryptorPassword 加密解密密钥
     * @return 加密解密工具类对象
     */
    private static AES256TextEncryptor getEncryptor(String encryptorPassword) {
        var encryptors = new AES256TextEncryptor();
        encryptors.setPassword(encryptorPassword);
        return encryptors;
    }

    /**
     * 加密密码
     *
     * @param password 需要加密的密码
     * @param salt     盐值
     * @return 加密后的密码
     */
    public static String encryptPassword(String password, String salt) {
        return getEncryptor(salt + ScxConfig.AppKey()).encrypt(password);
    }

    /**
     * 解密密码
     *
     * @param encryptedPassword 加密后的密码
     * @param salt              盐值
     * @return 解密后的密码
     */
    public static String decryptPassword(String encryptedPassword, String salt) {
        return getEncryptor(salt + ScxConfig.AppKey()).decrypt(encryptedPassword);
    }

    /**
     * 加密字符串
     *
     * @param text 待加密的字符串
     * @return a 密文
     */
    public static String encryptText(String text) {
        return getEncryptor(ScxConfig.AppKey()).encrypt(text);
    }

    /**
     * 解密字符串
     *
     * @param text 密文
     * @return a 结果
     */
    public static String decryptText(String text) {
        return getEncryptor(ScxConfig.AppKey()).decrypt(text);
    }

}
