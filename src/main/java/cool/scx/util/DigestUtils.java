package cool.scx.util;

import cool.scx.enumeration.DigestType;

import java.io.File;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 摘要算法工具类<br>
 * 只是针对 jdk 中自带的 {@link MessageDigest} 进行的简单封装<br>
 * 注意 : SHA 和 MD5 为单向散列函数,
 * 只适用于防篡改 或单项加密(如密码) 等 .
 * 如有加密后需要解密的需求 , 建议使用 {@link CryptoUtils}
 */
public class DigestUtils {

    /**
     * 缓冲区大小
     */
    private static final int CACHE_LENGTH = 256 * 1024;

    private static final char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String digest(final String data, DigestType digestType) {
        var digest = getDigest(digestType);
        var bytes = digest.digest(getBytes(data));
        return toHex(bytes);
    }

    public static String digest(final byte[] data, DigestType digestType) {
        var digest = getDigest(digestType);
        var bytes = digest.digest(data);
        return toHex(bytes);
    }

    public static String digest(final File data, DigestType digestType) {
        var digest = getDigest(digestType);
        try (var file = new RandomAccessFile(data, "r")) {
            byte[] buffer = new byte[CACHE_LENGTH];
            int read = file.read(buffer, 0, CACHE_LENGTH);
            while (read > -1) {
                digest.update(buffer, 0, read);
                read = file.read(buffer, 0, CACHE_LENGTH);
            }
            var bytes = digest.digest();
            return toHex(bytes);
        } catch (Exception e) {
            return null;
        }
    }

    public static String sha1(final String data) {
        return digest(data, DigestType.SHA_1);
    }

    public static String sha1(final byte[] data) {
        return digest(data, DigestType.SHA_1);
    }

    public static String sha1(final File data) {
        return digest(data, DigestType.SHA_1);
    }

    public static String sha256(final String data) {
        return digest(data, DigestType.SHA_256);
    }

    public static String sha256(final byte[] data) {
        return digest(data, DigestType.SHA_256);
    }

    public static String sha256(final File data) {
        return digest(data, DigestType.SHA_256);
    }

    public static String sha384(final String data) {
        return digest(data, DigestType.SHA_384);
    }

    public static String sha384(final byte[] data) {
        return digest(data, DigestType.SHA_384);
    }

    public static String sha384(final File data) {
        return digest(data, DigestType.SHA_384);
    }

    public static String sha512(final String data) {
        return digest(data, DigestType.SHA_512);
    }

    public static String sha512(final byte[] data) {
        return digest(data, DigestType.SHA_512);
    }

    public static String sha512(final File data) {
        return digest(data, DigestType.SHA_512);
    }

    public static String md5(final String data) {
        return digest(data, DigestType.MD5);
    }

    public static String md5(final byte[] data) {
        return digest(data, DigestType.MD5);
    }

    public static String md5(final File data) {
        return digest(data, DigestType.MD5);
    }

    /**
     * 字节数组转 Hex
     *
     * @param byteArray an array of {@link byte} objects.
     * @return a {@link java.lang.String} object.
     */
    private static String toHex(final byte[] byteArray) {
        char[] resultCharArray = new char[byteArray.length * 2];
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        return new String(resultCharArray);
    }

    private static MessageDigest getDigest(final DigestType digestType) {
        try {
            return MessageDigest.getInstance(digestType.algorithmsName());
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    private static byte[] getBytes(final String string) {
        return string == null ? null : string.getBytes(StandardCharsets.UTF_8);
    }

}
