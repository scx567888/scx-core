package cool.scx.util;

import cool.scx.module.ScxModuleHandler;
import org.jasypt.util.password.BasicPasswordEncryptor;
import org.jasypt.util.text.AES256TextEncryptor;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加密,解密工具类 <br>
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class CryptoUtils {

    private static final BasicPasswordEncryptor passwordEncryptor = new BasicPasswordEncryptor();

    private CryptoUtils() {

    }

    /**
     * @param encryptorPassword 加密解密密钥
     * @return 加密解密工具类对象
     */
    private static AES256TextEncryptor getAES256TextEncryptor(String encryptorPassword) {
        var encryptors = new AES256TextEncryptor();
        encryptors.setPassword(encryptorPassword);
        return encryptors;
    }

    /**
     * 使用自定义的密码 , 加密字符串
     *
     * @param text               待加密的字符串
     * @param encryptorsPassword 自定义的密码
     * @return a 密文
     */
    public static String encryptText(String text, String encryptorsPassword) {
        return getAES256TextEncryptor(encryptorsPassword).encrypt(text);
    }

    /**
     * 使用自定义的密码 , 解密字符串
     *
     * @param text               密文
     * @param encryptorsPassword 自定义的密码
     * @return a 结果
     */
    public static String decryptText(String text, String encryptorsPassword) {
        return getAES256TextEncryptor(encryptorsPassword).decrypt(text);
    }

    /**
     * 使用核心包默认的密码加密字符串 (注意 : 信息敏感数据不建议使用默认密码) {@link #encryptText(String, String)}
     *
     * @param text 待加密的字符串
     * @return a 密文
     */
    public static String encryptText(String text) {
        return getAES256TextEncryptor(ScxModuleHandler.appKey()).encrypt(text);
    }

    /**
     * 使用核心包默认的密码解密字符串 (注意 : 信息敏感数据不建议使用默认密码) {@link #decryptText(String, String)}
     *
     * @param text 加密后的数据
     * @return a 解密后的数据
     */
    public static String decryptText(String text) {
        return getAES256TextEncryptor(ScxModuleHandler.appKey()).decrypt(text);
    }


    /**
     * 根据 字符串 获取(加密) BASE64
     *
     * @param str a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public static String encodeBase64(String str) {
        var encoder = Base64.getEncoder();
        return encoder.encodeToString(str.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 根据 BASE64 获取(解密) 字符串
     *
     * @param base64 a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public static String decodeBase64(String base64) {
        var decoder = Base64.getDecoder();
        return new String(decoder.decode(base64), StandardCharsets.UTF_8);
    }

    /**
     * 加密密码
     *
     * @param password p
     * @return p
     */
    public static String encryptPassword(String password) {
        return passwordEncryptor.encryptPassword(password);
    }

    /**
     * 校验密码
     *
     * @param plainPassword     原密码
     * @param encryptedPassword 加密后的密码
     * @return a
     */
    public static boolean checkPassword(String plainPassword, String encryptedPassword) {
        return passwordEncryptor.checkPassword(plainPassword, encryptedPassword);
    }

}
