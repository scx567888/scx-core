package cool.scx.util;

import cool.scx.boot.ScxConfig;
import cool.scx.business.system.ScxLogService;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 文件 操作类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class FileUtils {


    //public static boolean uploadFile(MultipartFile file, String fileName, Integer index, Integer chunkTotal) {
    //    String tempFilePath;
    //    if (index == -1) {
    //        //单文件 直接写入磁盘
    //        tempFilePath = ScxContext.uploadFilePath.getPath() + fileName;
    //    } else {
    //        //分片文件 分片写入
    //        tempFilePath = ScxContext.uploadFilePath.getPath() + "TEMP\\" + fileName + "\\" + fileName + ".scxTemp";
    //        changeUploadFileConfig(fileName, index + 1, chunkTotal);
    //    }
    //    try {
    //        fileAppend(tempFilePath, file.getBytes());
    //        return true;
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //        return false;
    //    }
    //}

    //这个方法就是改变配置文件的

    /**
     * 所有文件类型
     */
    public final static List<FileType> ALL_FILE_TYPE_LIST;
    /**
     * 图片文件类型
     */
    public final static List<FileType> IMAGE_FILE_TYPE_LIST;
    /**
     * 其他文件类型
     */
    public final static List<FileType> OTHER_FILE_TYPE_LIST;

    //文件全上传完了 将临时文件 重命名 移动至 上传文件夹并 删除临时文件

    static {
        IMAGE_FILE_TYPE_LIST = getImageFileTypeList();
        OTHER_FILE_TYPE_LIST = getOtherFileType();
        ALL_FILE_TYPE_LIST = new ArrayList<>();
        ALL_FILE_TYPE_LIST.addAll(IMAGE_FILE_TYPE_LIST);
        ALL_FILE_TYPE_LIST.addAll(OTHER_FILE_TYPE_LIST);
    }

    /**
     * <p>changeUploadFileConfig.</p>
     *
     * @param fileName   a {@link java.lang.String} object.
     * @param nowChunk   a {@link java.lang.Integer} object.
     * @param chunkTotal a {@link java.lang.Integer} object.
     */
    public static void changeUploadFileConfig(String fileName, Integer nowChunk, Integer chunkTotal) {
        var configFilePath = ScxConfig.uploadFilePath + "TEMP\\" + fileName + "\\" + ".scxUpload";
        var config = new File(configFilePath);
        var tempFileParent = config.getParentFile();
        if (!tempFileParent.exists()) {
            boolean b = tempFileParent.mkdirs();
            if (!b) {
                ScxLogService.outLog("创建目录失败!!!", true);
            }
        }
        try {
            var fw = new FileWriter(config, false);
            var bw = new BufferedWriter(fw);
            bw.write(nowChunk + "-" + chunkTotal);
            bw.flush();
            bw.close();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>getDateStr.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public static String getDateStr() {
        var cale = Calendar.getInstance();
        String str;
        str = cale.get(Calendar.YEAR) + "/";
        str = str + (cale.get(Calendar.MONTH) + 1) + "/";
        str = str + cale.get(Calendar.DATE) + "/";
        str = str + cale.get(Calendar.HOUR_OF_DAY) + "/";
        str = str + new Date().getTime() + "";
        return str;
    }

    /**
     * <p>deleteFileByPath.</p>
     *
     * @param path a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean deleteFileByPath(String path) {
        var file = new File(path);
        if (file.isDirectory()) {
            //文件夹下已经没有文件了
            if (Objects.requireNonNull(file.list()).length == 0) {
                if (!file.delete()) {
                    return false;
                }
                deleteFileByPath(file.getParent());
            }
        } else {
            if (file.exists()) {
                if (!file.delete()) {
                    return false;
                }
                deleteFileByPath(file.getParent());
            }
        }
        return true;

    }

    /**
     * <p>validateFile.</p>
     *
     * @param fileName      a {@link java.lang.String} object.
     * @param fileWritePath a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean validateFile(String fileName, String fileWritePath) {
        var moveFrom = FileSystems.getDefault().getPath(ScxConfig.uploadFilePath.getPath() + "temp\\" + fileName + "\\" + fileName + ".scxTemp");
        var moveto = FileSystems.getDefault().getPath(ScxConfig.uploadFilePath.getPath() + fileWritePath);
        try {
            Files.createDirectories(moveto.getParent());
            Files.move(moveFrom, moveto, StandardCopyOption.REPLACE_EXISTING);
            Files.walk(Paths.get(ScxConfig.uploadFilePath.getPath() + "temp\\" + fileName + "\\"))
                    .sorted(Comparator.reverseOrder()).map(Path::toFile)
                    .forEach(file -> System.err.println(file.delete()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 追加 byte 到另一个文件中
     *
     * @param path  文件根路径
     * @param bytes 追加内容
     */
    public static void fileAppend(String path, byte[] bytes) {
        var tempPath = Paths.get(path);
        try {
            Files.createDirectories(tempPath.getParent());
            //实现文件追加写入
            Files.write(tempPath, bytes, StandardOpenOption.APPEND, StandardOpenOption.CREATE, StandardOpenOption.SYNC, StandardOpenOption.WRITE);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
     * @return a {@link cool.scx.util.FileType} object.
     */
    public static FileType getFileTypeBySuffix(File file, List<FileType> FILE_TYPE_LIST) {
        var fileName = file.getName();
        var fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return FILE_TYPE_LIST.stream().filter(fileType -> fileType.suffix.equals(fileSuffix)).findAny().orElse(null);
    }

    public static FileType getFileTypeBySuffix(File file) {
        return getFileTypeBySuffix(file, ALL_FILE_TYPE_LIST);
    }


    /**
     * <p>getFileTypeByHead.</p>
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link cool.scx.util.FileType} object.
     */
    public static FileType getFileTypeByHead(File file) {
        return getFileTypeByHead(file, ALL_FILE_TYPE_LIST);
    }

    /**
     * <p>getFileTypeByHead.</p>
     *
     * @param file           a {@link java.io.File} object.
     * @param FILE_TYPE_LIST a {@link java.util.List} object.
     * @return a {@link cool.scx.util.FileType} object.
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
     * @return a {@link cool.scx.util.FileType} object.
     */
    public static FileType getImageFileType(File file) {
        return getFileTypeBySuffix(file, IMAGE_FILE_TYPE_LIST);
    }

}
