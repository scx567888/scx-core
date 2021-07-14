package cool.scx.util;

import cool.scx.enumeration.MediaType;

import java.util.ArrayList;
import java.util.List;

import static cool.scx.enumeration.MediaType.*;

/**
 * 文件类型工具类
 *
 * @author scx567888
 * @version 1.1.4
 */
public final class FileTypeUtils {

    /**
     * 所有文件类型
     */
    private final static List<FileType> FILE_TYPE_LIST = new ArrayList<>();

    /**
     * 未知类型
     */
    private final static FileType unknownFileType = new FileType(null, null, null, null, "Unknown FileType");

    static {
        initFileTypeList();
    }

    /**
     * 根据文件头信息获取文件的类型 (不受文件名称影响,大多数情况下准确,但是性能较慢 )
     * 可以结合  getFileTypeBySuffix 一起使用
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link cool.scx.util.FileType} object.
     */
    public static FileType byHead(String file) {
        var head = FileUtils.getHead(file);
        return FILE_TYPE_LIST.stream().filter(t -> head.startsWith(t.fileHeader)).findAny().orElse(unknownFileType);
    }

    /**
     * 根据后缀名获取 filetype
     * 注意不需要带 . 如果是文件名请使用 getFileTypeForFilename
     * 正确 txt , png , docx
     * 错误 .txt , a.png , doc.docx
     *
     * @param ext a {@link java.lang.String} object
     * @return a {@link cool.scx.util.FileType} object
     */
    public static FileType byExt(String ext) {
        return FILE_TYPE_LIST.stream().filter(t -> ext.equals(t.fileExtension)).findAny().orElse(unknownFileType);
    }

    /**
     * <p>getFileTypeForFilename.</p>
     *
     * @param filename a {@link java.lang.String} object
     * @return a {@link cool.scx.util.FileType} object
     */
    public static FileType byName(String filename) {
        return byExt(FileUtils.getExt(filename));
    }

    private static void add(String fileExtension, String mimeType, MediaType mediaType, String fileHeader, String description) {
        FILE_TYPE_LIST.add(new FileType(fileExtension, mimeType, mediaType, fileHeader, description));
    }

    /**
     * 初始化一些常用的 文件类型
     */
    private static void initFileTypeList() {

        // application
        add("epub", "application/epub+zip", APPLICATION, "-", "电子书格式");
        add("js", "application/javascript", APPLICATION, "-", "-");
        add("json", "application/json", APPLICATION, "-", "-");
        add("doc", "application/msword", APPLICATION, "d0cf11e0a1b11ae10000", " office 文档文件 (03 版本)");
        add("xls", "application/vnd.ms-excel", APPLICATION, "-", "office 表格文件 (03 版本)");
        add("ppt", "application/vnd.ms-powerpoint", APPLICATION, "-", "office 幻灯片文件 (03 版本)");
        add("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", APPLICATION, "504b0304140006000800", " office 文档文件 (07+ 版本)");
        add("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", APPLICATION, "-", "office 表格文件 (07+ 版本)");
        add("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation", APPLICATION, "-", "office 幻灯片文件 (07+ 版本)");
        add("exe", null, APPLICATION, "4D5A9000030000000400", "可执行文件");
        add("dll", null, APPLICATION, "4D5A9000030000000400", "动态链接库");
        add("com", null, APPLICATION, "-", "-");
        add("bat", null, APPLICATION, "-", "-");
        add("msi", null, APPLICATION, "-", "-");
        add("7z", null, APPLICATION, "-", "压缩包");
        add("rar", "application/vnd.rar", APPLICATION, "-", "压缩包");
        add("zip", "application/zip", APPLICATION, "504B0304", "压缩包");
        add("dmg", "-", APPLICATION, "-", "苹果安装包文件");
        add("sql", "application/sql", APPLICATION, "-", "SQL 脚本");
        add("wasm", "application/wasm", APPLICATION, "-", "wasm");
        add("iso", "-", APPLICATION, "-", "镜像文件");
        add("xhtml", "application/xhtml+xml", APPLICATION, "-", "xhtml");
        add("xml", "application/xml", APPLICATION, "3c3f786d6c2076657273", "xml文件");
        add("pdf", "application/pdf", APPLICATION, "255044462d312e350d0a", "pdf 文件");
        add("dtd", "application/xml-dtd", APPLICATION, "-", "-");
        add("xslt", "application/xslt+xml", APPLICATION, "-", "-");
        add("m3u", "application/vnd.apple.mpegurl", APPLICATION, "-", "-");
        add("m3u8", "application/vnd.apple.mpegurl", APPLICATION, "-", "-");
        add("psd", null, APPLICATION, "-", "-");
        add("rmvb", "application/vnd.rn-realmedia-vbr", APPLICATION, "2e524d46000000120001", "-");

        // font
        add("otf", "font/otf", FONT, "-", "字体文件");
        add("ttf", "font/ttf", FONT, "-", "字体文件");
        add("woff", "font/woff", FONT, "-", "字体文件");
        add("woff2", "font/woff2", FONT, "-", "字体文件");

        // audio
        add("mp3", "audio/mpeg", AUDIO, "49443303000000002176", "mp3 文件");
        add("oga", "audio/ogg", AUDIO, "-", "-");
        add("ogg", "audio/ogg", AUDIO, "-", "-");
        add("spx", "audio/ogg", AUDIO, "-", "-");
        add("m3a", "audio/mpeg", AUDIO, "-", "-");
        add("rip", "audio/vnd.rip", AUDIO, "-", "-");
        add("aac", "audio/aac", AUDIO, "-", "-");
        add("flac", "audio/x-flac", AUDIO, "-", "-");
        add("wma", null, AUDIO, "-", "-");
        add("wav", null, AUDIO, "52494646e27807005741", "Wave (wav)");

        // image
        add("bmp", "image/bmp", IMAGE, "424d228c010000000000", " 16色位图(bmp)");
        add("bmp", "image/bmp", IMAGE, "424d8240090000000000", "24位位图(bmp)");
        add("bmp", "image/bmp", IMAGE, "424d8e1b030000000000", " 256色位图(bmp)");
        add("gif", "image/gif", IMAGE, "474946383961", "GIF");
        add("jpeg", "image/jpeg", IMAGE, "ffd8ffe000104a464946", "JPEG");
        add("jpg", "image/jpeg", IMAGE, "ffd8ffe000104a464946", "JPEG");
        add("png", "image/png", IMAGE, "89504E470D0A1A0A0000", "PNG");
        add("svg", "image/svg+xml", IMAGE, "-", "-");
        add("tiff", "image/tiff", IMAGE, "49492A00", "TIFF (tif)");
        add("tif", "image/tiff", IMAGE, "49492A00", "TIFF (tif)");
        add("webp", "image/webp", IMAGE, "-", "-");
        add("ico", "image/x-icon", IMAGE, "-", "ico 图标文件");

        // text 文本文件不存在文件头
        add("css", "text/css", TEXT, "-", "CSS 样式表");
        add("csv", "text/csv", TEXT, "-", "-");
        add("html", "text/html", TEXT, "-", "HTML");
        add("txt", "text/plain", TEXT, "-", "文本文件");

        // video
        add("mp4", "video/mp4", VIDEO, "00000020667479706d70", " mp4 视频文件");
        add("3gp", "video/3gpp", VIDEO, "-", "-");
        add("3g2", "video/3gpp2", VIDEO, "-", "-");
        add("h261", "video/h261", VIDEO, "-", "-");
        add("h263", "video/h263", VIDEO, "-", "-");
        add("h264", "video/h264", VIDEO, "-", "-");
        add("mpg4", "video/mp4", VIDEO, "-", "-");
        add("mpeg", "video/mpeg", VIDEO, "-", "-");
        add("mpg", "video/mpeg", VIDEO, "000001ba210001000180", "mpg 文件");
        add("mpe", "video/mpeg", VIDEO, "-", "-");
        add("ogv", "video/ogg", VIDEO, "-", "-");
        add("qt", "video/quicktime", VIDEO, "-", "-");
        add("mov", "video/quicktime", VIDEO, "-", "-");
        add("webm", "video/webm", VIDEO, "-", "-");
        add("f4v", "video/x-f4v", VIDEO, "-", "-");
        add("flv", "video/x-flv", VIDEO, "-", "-");
        add("m4a", "audio/x-m4a", VIDEO, "-", "-");
        add("m4v", "video/x-m4v", VIDEO, "-", "-");
        add("mkv", "video/x-matroska", VIDEO, "-", "-");
        add("mk3d", "video/x-matroska", VIDEO, "-", "-");
        add("vob", "video/x-ms-vob", VIDEO, "-", "-");
        add("wmv", "video/x-ms-wmv", VIDEO, "3026b2758e66cf11a6d9", "wmv 文件");
        add("avi", "video/x-msvideo", VIDEO, "52494646d07d60074156", "avi 文件");

    }

}
