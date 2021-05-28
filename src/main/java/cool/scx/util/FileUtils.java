package cool.scx.util;

import cool.scx.module.ScxModule;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * 文件 操作类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class FileUtils {

    /**
     * 文件大小格式化 正则表达式
     */
    public final static Pattern DISPLAY_SIZE_PATTERN = Pattern.compile("^([+\\-]?\\d+)([a-zA-Z]{0,2})$");

    /**
     * 文件大小格式化 映射表 方便计算使用
     */
    private final static HashMap<String, Long> DISPLAY_SIZE_MAP = getDisplaySizeMap();


    /**
     * deleteFilesVisitor
     */
    private final static SimpleFileVisitor<Path> deleteFilesVisitor = getDeleteFilesVisitor();

    /**
     * deleteIfExistsVisitor
     */
    private final static SimpleFileVisitor<Path> deleteIfExistsVisitor = getDeleteIfExistsVisitor();

    private static SimpleFileVisitor<Path> getDeleteFilesVisitor() {
        return new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        };
    }

    private static SimpleFileVisitor<Path> getDeleteIfExistsVisitor() {
        return new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return super.postVisitDirectory(dir, exc);
            }
        };
    }

    private static HashMap<String, Long> getDisplaySizeMap() {
        var tempMap = new HashMap<String, Long>();
        tempMap.put("B", 1L);
        tempMap.put("KB", 1024L);
        tempMap.put("MB", 1048576L);
        tempMap.put("GB", 1073741824L);
        tempMap.put("TB", 1099511627776L);
        return tempMap;
    }

    /**
     * 将 long 类型的文件大小 格式化(转换为人类可以看懂的形式)
     * 如 1024 转换为 1KB
     *
     * @param size a long.
     * @return a {@link java.lang.String} object.
     */
    public static String longToDisplaySize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    /**
     * 将 格式化后的大小转换为 long
     * 如将 1KB 转换为 1024
     *
     * @param str 待转换的值 如 5MB 13.6GB
     * @return a long.
     */
    public static long displaySizeToLong(String str) {
        var matcher = DISPLAY_SIZE_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new RuntimeException(str + " : 无法转换为 long");
        }
        var group = matcher.group(2);
        long amount = Long.parseLong(matcher.group(1));
        var s = StringUtils.isNotEmpty(group) ? DISPLAY_SIZE_MAP.get(group) : DISPLAY_SIZE_MAP.get("B");
        return Math.multiplyExact(amount, s);
    }

    /**
     * 删除文件 若文件无法删除则返回 false
     *
     * @param path a {@link java.nio.file.Path} object.
     * @return a boolean.
     */
    public static boolean deleteFiles(Path path) {
        try {
            Files.walkFileTree(path, deleteFilesVisitor);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 删除文件
     *
     * @param path a {@link java.nio.file.Path} object.
     * @throws java.io.IOException if any.
     */
    public static void deleteIfExists(Path path) throws IOException {
        try {
            Files.deleteIfExists(path);
        } catch (DirectoryNotEmptyException e) {
            Files.walkFileTree(path, deleteIfExistsVisitor);
        }
    }

    /**
     * 字节数组转 Hex
     *
     * @param byteArray an array of {@link byte} objects.
     * @return a {@link java.lang.String} object.
     */
    public static String byteArrayToHex(byte[] byteArray) {
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

    /**
     * 根据文件计算 md5
     *
     * @param inputFile 文件路径
     * @return a md5 值
     */
    public static String fileMD5(String inputFile) {
        // 缓冲区大小（这个可以抽出一个参数）
        int bufferSize = 256 * 1024;
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try (var fileInputStream = new FileInputStream(inputFile); var digestInputStream = new DigestInputStream(fileInputStream, messageDigest)) {
            byte[] buffer = new byte[bufferSize];
            while (digestInputStream.read(buffer) > 0) ;
            messageDigest = digestInputStream.getMessageDigest();
            byte[] resultByteArray = messageDigest.digest();
            return byteArrayToHex(resultByteArray);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将一个文件移动到另一个位置
     *
     * @param moveFrom a {@link java.lang.String} object.
     * @param moveto   a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean fileMove(String moveFrom, String moveto) {
        var moveFromPath = Path.of(moveFrom);
        var moveToPath = Path.of(moveto);
        try {
            Files.createDirectories(moveToPath.getParent());
            Files.move(moveFromPath, moveToPath, StandardCopyOption.REPLACE_EXISTING);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 追加 byte 到另一个文件中
     *
     * @param path  文件根路径
     * @param bytes 追加内容
     * @return a {@link java.lang.Boolean} object.
     */
    public static Boolean fileAppend(String path, byte[] bytes) {
        var tempPath = Path.of(path);
        try {
            Files.createDirectories(tempPath.getParent());
            //实现文件追加写入
            Files.write(tempPath, bytes, StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.WRITE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * <p>getFileByAppRoot.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a {@link java.io.File} object.
     */
    public static File getFileByRootModulePath(String path) {
        return path.startsWith("absPath:") ? new File(path.replaceAll("absPath:", "")) : new File(ScxModule.appRootPath(), path);
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath    文件路径
     * @param fileContent 待写入的内容
     */
    public static void setFileContent(String filePath, String fileContent) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            var channel = fos.getChannel();
            var src = StandardCharsets.UTF_8.encode(fileContent);
            channel.write(src);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
