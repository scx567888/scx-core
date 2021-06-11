package cool.scx.auth;

import cool.scx.util.CryptoUtils;
import cool.scx.util.StringUtils;

/**
 * 提供一些有关认证的常用方法
 *
 * @author 司昌旭
 * @version 1.1.13
 */
public final class AuthUtils {

    /**
     * 加密密码 返回加密后的密码和盐值
     * <p>
     * 如需解密通过此方法加密的密码请使用 {@link #verifyPassword(String, String, String)}
     *
     * @param clearTextPassword 需要加密的明文密码
     * @return 密码和盐值的数组  [0] 密码 [1] 盐值
     */
    public static String[] getPasswordAndSalt(String clearTextPassword) {
        var salt = StringUtils.getUUID().replace("-", "").substring(16);
        try {
            var password = CryptoUtils.encryptPassword(clearTextPassword, salt);
            return new String[]{password, salt};
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    /**
     * 校验密码是否正确
     * <p>
     * 使用此方法校验的密码 , 加密时请使用 {@link #getPasswordAndSalt(String)}
     *
     * @param password          加密后的密码
     * @param salt              加密后的盐值
     * @param clearTextPassword 前台传过来的明文密码
     * @return 是否正确
     */
    public static boolean verifyPassword(String password, String salt, String clearTextPassword) {
        try {
            var decryptPassword = CryptoUtils.decryptPassword(password, salt);
            return clearTextPassword.equals(decryptPassword);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
