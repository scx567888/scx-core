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

//    /**
//     * 文件 下载 公共方法
//     *
//     * @param response response
//     */
    //public static void downloadFile(HttpServletResponse response, HttpServletRequest request, String downloadFilePath) throws UnsupportedEncodingException {
    //    var downloadFile = new File(downloadFilePath);
    //    var context = request.getServletContext();
    //    // 获取文件的 MIME 类型
    //    var mimeType = context.getMimeType(downloadFilePath);
    //    if (mimeType == null) {
    //        // 如果文件的 MIME 类型为空 就设置为 流
    //        mimeType = "application/octet-stream";
    //    }
    //    response.setContentType(mimeType);
    //
    //    response.setHeader("Content-Disposition", "attachment;filename=" + new String(downloadFile.getName().getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
    //    // 代表了该服务器可以接受范围请求
    //    response.setHeader("Accept-Ranges", "bytes");
    //    var downloadSize = downloadFile.length();
    //    var fromPos = 0L;
    //    var toPos = 0L;
    //    var range = request.getHeader("Range");
    //    // 若客户端没有传来Range，说明并没有下载过此文件
    //    if (range != null) {
    //        // 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
    //        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
    //        // 因为请求头是这样的格式 所以再次进行分割提取信息 Range: bytes=12-100
    //        String bytes = range.replaceAll("bytes=", "");
    //        String[] ary = bytes.split("-");
    //        fromPos = Integer.parseInt(ary[0]);
    //        if (ary.length == 2) {
    //            toPos = Integer.parseInt(ary[1]);
    //        }
    //        int size;
    //        if (toPos > fromPos) {
    //            size = (int) (toPos - fromPos);
    //        } else {
    //            size = (int) (downloadSize - fromPos);
    //        }
    //        downloadSize = size;
    //    }
    //    response.setHeader("Content-Length", downloadSize + "");
    //    // 复制文件流 到客户端
    //    RandomAccessFile in = null;
    //    OutputStream out = null;
    //    try {
    //        //已只读方式 获取文件
    //        in = new RandomAccessFile(downloadFile, "r");
    //        // 设置下载起始位置
    //        if (fromPos > 0) {
    //            in.seek(fromPos);
    //        }
    //        // 缓冲区大小 如果文件小于 800 kb 设置为文件大小 否则 设置缓冲为 800 kb
    //        int bufLen = (int) (downloadSize < 819200 ? downloadSize : 819200);
    //        byte[] buffer = new byte[bufLen];
    //        int num;
    //        int count = 0; // 当前写到客户端的大小
    //        out = response.getOutputStream();
    //        while ((num = in.read(buffer)) != -1) {
    //            out.write(buffer, 0, num);
    //            count += num;
    //            //处理最后一段，计算不满缓冲区的大小
    //            if (downloadSize - count < bufLen) {
    //                bufLen = (int) (downloadSize - count);
    //                if (bufLen == 0) {
    //                    break;
    //                }
    //                buffer = new byte[bufLen];
    //            }
    //        }
    //        response.flushBuffer();
    //    } catch (IOException e) {
    //        response.setStatus(HttpServletResponse.SC_NOT_FOUND);
    //    } finally {
    //        if (null != out) {
    //            try {
    //                out.close();
    //            } catch (IOException ignored) {
    //            }
    //        }
    //        if (null != in) {
    //            try {
    //                in.close();
    //            } catch (IOException ignored) {
    //
    //            }
    //        }
    //    }
    //}


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

    //文件全上传完了 将临时文件 重命名 移动至 上传文件夹并 删除临时文件

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

    public static class FileInfo {
        public String id;
        public String parentId;
        public String filePath;
        public String type;
    }

}
