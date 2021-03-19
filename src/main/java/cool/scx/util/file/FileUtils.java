package cool.scx.util.file;

import cool.scx.util.StringUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.*;
import java.util.regex.Pattern;

/**
 * 文件 操作类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class FileUtils {


    /**
     * 所有文件类型
     */
    public final static List<FileType> ALL_FILE_TYPE_LIST;

    //这个方法就是改变配置文件的
    /**
     * 图片文件类型
     */
    public final static List<FileType> IMAGE_FILE_TYPE_LIST;
    /**
     * 其他文件类型
     */
    public final static List<FileType> OTHER_FILE_TYPE_LIST;
    /**
     * Constant <code>DISPLAY_SIZE_MAP</code>
     */
    public final static HashMap<String, Long> DISPLAY_SIZE_MAP = new HashMap<>();
    /**
     * Constant <code>DISPLAY_SIZE_PATTERN</code>
     */
    public final static Pattern DISPLAY_SIZE_PATTERN = Pattern.compile("^([+\\-]?\\d+)([a-zA-Z]{0,2})$");

    static {
        IMAGE_FILE_TYPE_LIST = getImageFileTypeList();
        OTHER_FILE_TYPE_LIST = getOtherFileType();
        ALL_FILE_TYPE_LIST = new ArrayList<>();
        ALL_FILE_TYPE_LIST.addAll(IMAGE_FILE_TYPE_LIST);
        ALL_FILE_TYPE_LIST.addAll(OTHER_FILE_TYPE_LIST);
        DISPLAY_SIZE_MAP.put("B", 1L);
        DISPLAY_SIZE_MAP.put("KB", 1024L);
        DISPLAY_SIZE_MAP.put("MB", 1048576L);
        DISPLAY_SIZE_MAP.put("GB", 1073741824L);
        DISPLAY_SIZE_MAP.put("TB", 1099511627776L);
    }

    /**
     * <p>getLastUploadChunk.</p>
     *
     * @param uploadConfigFile a {@link java.io.File} object.
     * @param chunkLength      a {@link java.lang.Integer} object.
     * @return a {@link java.lang.Integer} object.
     */
    public static Integer getLastUploadChunk(File uploadConfigFile, Integer chunkLength) {
        try (var fr = new FileReader(uploadConfigFile); var br = new BufferedReader(fr)) {
            return Integer.parseInt(br.readLine().split("-")[0]);
        } catch (Exception e) {
            changeLastUploadChunk(uploadConfigFile, 0, chunkLength);
            return 0;
        }
    }

    /**
     * <p>changeLastUploadChunk.</p>
     *
     * @param uploadConfigFile a {@link java.io.File} object.
     * @param nowChunkIndex    a {@link java.lang.Integer} object.
     * @param chunkLength      a {@link java.lang.Integer} object.
     */
    public static void changeLastUploadChunk(File uploadConfigFile, Integer nowChunkIndex, Integer chunkLength) {
        try {
            Files.createDirectories(Path.of(uploadConfigFile.getParent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        try (var fw = new FileWriter(uploadConfigFile, false); var bw = new BufferedWriter(fw)) {
            bw.write(nowChunkIndex + "-" + chunkLength);
            bw.flush();
        } catch (Exception e) {
            e.printStackTrace();
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
     * <p>deleteFiles.</p>
     *
     * @param filePath a {@link java.nio.file.Path} object.
     */
    public static void deleteFiles(Path filePath) {
        try {
            Files.walk(filePath)
                    .sorted(Comparator.reverseOrder()).map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>deleteFiles.</p>
     *
     * @param filePath a {@link java.lang.String} object.
     */
    public static void deleteFiles(String filePath) {
        deleteFiles(Path.of(filePath));
    }

    /**
     * 获取文件夹下的文件列表
     *
     * @param filePath 文件路径
     * @return 文件列表
     * @throws java.io.IOException if any.
     */
    public static List<FileInfo> getFileList(String filePath) throws IOException {
        var fileList = new LinkedList<FileInfo>();
        var path = Paths.get(filePath);
        Files.walkFileTree(path, new FileVisitor<>() {
            //访问文件夹之前自动调用此方法
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                var fileInfo = new FileInfo();
                fileInfo.type = "Directory";
                return getFileVisitResult(dir, fileInfo, path, fileList);
            }

            //访问文件时自动调用此方法
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                var fileInfo = new FileInfo();
                fileInfo.type = "File";
                return getFileVisitResult(file, fileInfo, path, fileList);
            }

            //访问文件失败时自动调用此方法
            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) {
                return FileVisitResult.CONTINUE;
            }

            //访问文件夹之后自动调用此方法
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                return FileVisitResult.CONTINUE;
            }
        });

        return fileList;

    }

    private static FileVisitResult getFileVisitResult(Path file, FileInfo fileInfo, Path path, LinkedList<FileInfo> fileList) {
        fileInfo.id = file.getFileName().toString();
        fileInfo.parentId = file.getParent().toFile().getPath();
        if (path.toString().equals(fileInfo.parentId)) {
            fileInfo.parentId = "0";
        } else {
            fileInfo.parentId = file.getParent().getFileName().toString();
        }
        fileInfo.filePath = file.toFile().getPath();
        fileList.add(fileInfo);
        return FileVisitResult.CONTINUE;
    }

    /**
     * 将字符串写入文件
     *
     * @param filePath    1
     * @param fileContent 1
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

    /**
     * <p>deleteIfExists.</p>
     *
     * @param dir a {@link java.nio.file.Path} object.
     * @throws java.io.IOException if any.
     */
    public static void deleteIfExists(Path dir) throws IOException {
        try {
            Files.deleteIfExists(dir);
        } catch (DirectoryNotEmptyException e) {
            Files.walkFileTree(dir, new SimpleFileVisitor<>() {
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
            });
        }
    }

    private static ArrayList<FileType> getImageFileTypeList() {
        var f = new ArrayList<FileType>();
        f.add(new FileType("ffd8ffe000104a464946", "jpg", "image/jpeg", "JPEG (jpg)"));
        f.add(new FileType("ffd8ffe000104a464946", "jpeg", "image/jpeg", "JPEG (jpg)"));
        f.add(new FileType("89504e470d0a1a0a0000", "png", "image/png", "PNG (png)"));
        f.add(new FileType("47494638396126026f01", "gif", "image/gif", " GIF (gif)"));
        f.add(new FileType("49492a00227105008037", "tif", "image/tiff", "TIFF (tif)"));
        f.add(new FileType("49492a00227105008037", "tiff", "image/tiff", "TIFF (tif)"));
        f.add(new FileType("424d228c010000000000", "bmp", "image/bmp", " 16色位图(bmp)"));
        f.add(new FileType("424d8240090000000000", "bmp", "image/bmp", "24位位图(bmp)"));
        f.add(new FileType("424d8e1b030000000000", "bmp", "image/bmp", " 256色位图(bmp)"));
        f.add(new FileType("", "ico", "image/x-icon", "ico 图标文件"));
        return f;
    }

    private static ArrayList<FileType> getOtherFileType() {
        var f = new ArrayList<FileType>();
        f.add(new FileType("3c21444f435459504520", "html", "text/html", " HTML (html)"));
        f.add(new FileType("3c21646f637479706520", "htm", "text/html", "HTM (htm)"));
        f.add(new FileType("48544d4c207b0d0a0942", "css", "text/css", "css 样式表"));
        f.add(new FileType("696b2e71623d696b2e71", "js", "application/x-javascript", "js"));
        f.add(new FileType("7b5c727466315c616e73", "rtf", "application/x-rtf", "Rich Text Format (rtf)"));
        f.add(new FileType("255044462d312e350d0a", "pdf", "application/pdf", "pdf 文件")); // Adobe Acrobat (pdf)
        f.add(new FileType("2e524d46000000120001", "rmvb", "application/vnd.rn-realmedia-vbr", "rmvb"));
        f.add(new FileType("00000020667479706d70", "mp4", "video/mpeg4", " mp4 视频文件"));
        f.add(new FileType("49443303000000002176", "mp3", "audio/mp3", "mp3 文件"));
        f.add(new FileType("000001ba210001000180", "mpg", "video/mpg", "mpg 文件")); //
        f.add(new FileType("3026b2758e66cf11a6d9", "wmv", "audio/wav", "wmv 文件"));
        f.add(new FileType("52494646e27807005741", "wav", "audio/wav", "Wave (wav)"));
        f.add(new FileType("52494646d07d60074156", "avi", "video/avi", "avi 文件"));
        f.add(new FileType("4d5a9000030000000400", "exe", "application/x-msdownload", "可执行文件"));
        f.add(new FileType("3c3f786d6c2076657273", "xml", "text/xml", "xml文件"));
        f.add(new FileType("d0cf11e0a1b11ae10000", "doc", "application/msword", " docx文件 (03 版本)")); // MS Excel 注意：word、msi 和 excel的文件头一样
        f.add(new FileType("504b0304140006000800", "docx", "application/msword", " docx文件 (07+ 版本)"));
        f.add(new FileType("6431303a637265617465", "torrent", "application/x-bittorrent", "种子文件"));
        return f;
    }

    /**
     * 得到文件的文件头
     *
     * @param src an array of {@link byte} objects.
     * @return a {@link java.lang.String} object.
     */
    public static String bytesToHexString(byte[] src) {
        var stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte b : src) {
            int v = b & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }


    /**
     * <p>getFileTypeBySuffix.</p>
     *
     * @param file           a {@link java.io.File} object.
     * @param FILE_TYPE_LIST a {@link java.util.List} object.
     * @return a {@link cool.scx.util.file.FileType} object.
     */
    public static FileType getFileTypeBySuffix(File file, List<FileType> FILE_TYPE_LIST) {
        var fileName = file.getName();
        var fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return FILE_TYPE_LIST.stream().filter(fileType -> fileType.suffix.equals(fileSuffix)).findAny().orElse(null);
    }

    /**
     * <p>getFileTypeBySuffix.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link cool.scx.util.file.FileType} object.
     */
    public static FileType getFileTypeBySuffix(File file) {
        return getFileTypeBySuffix(file, ALL_FILE_TYPE_LIST);
    }


    /**
     * <p>getFileTypeByHead.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link cool.scx.util.file.FileType} object.
     */
    public static FileType getFileTypeByHead(File file) {
        return getFileTypeByHead(file, ALL_FILE_TYPE_LIST);
    }

    /**
     * <p>getFileTypeByHead.</p>
     *
     * @param file           a {@link java.io.File} object.
     * @param FILE_TYPE_LIST a {@link java.util.List} object.
     * @return a {@link cool.scx.util.file.FileType} object.
     */
    public static FileType getFileTypeByHead(File file, List<FileType> FILE_TYPE_LIST) {
        try (var is = new FileInputStream(file)) {
            byte[] b = new byte[10];
            is.read(b, 0, b.length);
            var fileCode = bytesToHexString(b);
            return FILE_TYPE_LIST.stream().filter(fileType -> fileType.head.toLowerCase().startsWith(fileCode.toLowerCase().substring(0, 5)) || fileCode.toLowerCase().substring(0, 5).startsWith(fileType.head.toLowerCase())).findAny().orElse(null);
        } catch (Exception e) {
            return new FileType("", "", "", "");
        }
    }

    /**
     * <p>getImageFileType.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link cool.scx.util.file.FileType} object.
     */
    public static FileType getImageFileType(File file) {
        return getFileTypeBySuffix(file, IMAGE_FILE_TYPE_LIST);
    }

    /**
     * 将 long 类型的文件大小 转换为人类可以看懂的形式
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
     * <p>displaySizeToLong.</p>
     *
     * @param str a {@link java.lang.String} object.
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
}
