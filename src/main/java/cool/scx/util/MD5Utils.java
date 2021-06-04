package cool.scx.util;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {

    public static String md5(String str) {
        var b = str.getBytes(StandardCharsets.UTF_8);
        MessageDigest md5Instance = getMD5Instance();
        md5Instance.update(b);
        return byteArrayToHex(md5Instance.digest());
    }

    /**
     * 根据文件计算 md5
     *
     * @param inputFile 文件路径
     * @return a md5 值
     */
    public static String md5(File inputFile) {
        // 缓冲区大小（这个可以抽出一个参数）
        try (var fileInputStream = new FileInputStream(inputFile); var digestInputStream = new DigestInputStream(fileInputStream, getMD5Instance())) {
            byte[] buffer = new byte[256 * 1024];
            while (digestInputStream.read(buffer) > 0) ;
            var md5Instance = digestInputStream.getMessageDigest();
            return byteArrayToHex(md5Instance.digest());
        } catch (Exception e) {
            return null;
        }
    }

    private static MessageDigest getMD5Instance() {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return messageDigest;
    }

    /**
     * 字节数组转 Hex
     *
     * @param byteArray an array of {@link byte} objects.
     * @return a {@link java.lang.String} object.
     */
    private static String byteArrayToHex(byte[] byteArray) {
        // 首先初始化一个字符数组，用来存放每个16进制字符
        char[] hexDigits = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        // new一个字符数组，这个就是用来组成结果字符串的（解释一下：一个byte是八位二进制，也就是2位十六进制字符（2的8次方等于16的2次方））
        char[] resultCharArray = new char[byteArray.length * 2];
        // 遍历字节数组，通过位运算（位运算效率高），转换成字符放到字符数组中去
        int index = 0;
        for (byte b : byteArray) {
            resultCharArray[index++] = hexDigits[b >>> 4 & 0xf];
            resultCharArray[index++] = hexDigits[b & 0xf];
        }
        // 字符数组组合成字符串返回
        return new String(resultCharArray);
    }

}