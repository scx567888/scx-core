package cool.scx.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final static List<FileType> ALL_FILE_TYPE_LIST = new ArrayList<>();

    private final static Map<String, FileType> EXTENSION_FILETYPE_MAPPING = new HashMap<>();

    private final static Map<String, FileType> HEAD_FILETYPE_MAPPING = new HashMap<>();

    static {
        initFileTypeList();
        initExtensionFileTypeMapping();
        initHeadFileTypeMapping();
    }

    /**
     * <p>getMimeTypeForExtension.</p>
     *
     * @param ext a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public static String getMimeTypeForExtension(String ext) {
        var fileType = getFileTypeForExtension(ext);
        if (fileType != null) {
            return fileType.mimeType;
        }
        return null;
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
    public static FileType getFileTypeForExtension(String ext) {
        return EXTENSION_FILETYPE_MAPPING.get(ext.toLowerCase());
    }

    /**
     * 根据文件头信息获取文件的类型 (不受文件名称影响,大多数情况下准确,但是性能较慢 )
     * 可以结合  getFileTypeBySuffix 一起使用
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link cool.scx.util.FileType} object.
     */
    public static FileType getFileTypeByHead(File file) {
        try (var is = new FileInputStream(file)) {
            byte[] b = new byte[10];
            is.read(b, 0, b.length);
            var fileCode = bytesToHexString(b);
            return HEAD_FILETYPE_MAPPING.get(fileCode.toLowerCase().substring(0, 5));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据后缀名称获取文件的类型 (可能不准确但是速度快)
     * 可以结合  getFileTypeByHead 一起使用
     *
     * @param file a {@link java.io.File} object.
     * @return a {@link cool.scx.util.FileType} object.
     */
    public static FileType getFileTypeForFile(File file) {
        return getFileTypeForFilename(file.getName().toLowerCase());
    }

    /**
     * <p>getFileTypeForFilename.</p>
     *
     * @param filename a {@link java.lang.String} object
     * @return a {@link cool.scx.util.FileType} object
     */
    public static FileType getFileTypeForFilename(String filename) {
        int li = filename.lastIndexOf('.');
        if (li != -1 && li != filename.length() - 1) {
            String ext = filename.substring(li + 1);
            return getFileTypeForExtension(ext);
        }
        return null;
    }

    /**
     * <p>getMimeTypeForFile.</p>
     *
     * @param file a {@link java.io.File} object
     * @return a {@link java.lang.String} object
     */
    public static String getMimeTypeForFile(File file) {
        return getFileTypeForFile(file).mimeType;
    }

    /**
     * <p>getMimeTypeForFilename.</p>
     *
     * @param filename a {@link java.lang.String} object
     * @return a {@link java.lang.String} object
     */
    public static String getMimeTypeForFilename(String filename) {
        int li = filename.lastIndexOf('.');
        if (li != -1 && li != filename.length() - 1) {
            String ext = filename.substring(li + 1);
            return getMimeTypeForExtension(ext);
        }
        return null;
    }


    private static void add(String fileExtension, String mimeType) {
        add(fileExtension, mimeType, null);
    }

    private static void add(String fileExtension, String mimeType, String fileHeader) {
        add(fileExtension, mimeType, fileHeader, null);
    }

    private static void add(String fileExtension, String mimeType, String fileHeader, String description) {
        add(fileExtension, mimeType, fileHeader, description, false);
    }

    private static void add(String fileExtension, String mimeType, String fileHeader, String description, boolean isImage) {
        ALL_FILE_TYPE_LIST.add(new FileType(fileExtension, mimeType, fileHeader, description, isImage));
    }

    /**
     * <p>initExtensionFileTypeMapping.</p>
     */
    public static void initExtensionFileTypeMapping() {
        for (FileType f : ALL_FILE_TYPE_LIST) {
            EXTENSION_FILETYPE_MAPPING.put(f.fileExtension.toLowerCase(), f);
        }
    }

    /**
     * <p>initHeadFileTypeMapping.</p>
     */
    public static void initHeadFileTypeMapping() {
        for (FileType f : ALL_FILE_TYPE_LIST) {
            if (f.fileHeader != null) {
                HEAD_FILETYPE_MAPPING.put(f.fileHeader.toLowerCase(), f);
            }
        }
    }

    /**
     * 得到文件的文件头
     *
     * @param src an array of {@link byte} objects.
     * @return a {@link java.lang.String} object.
     */
    private static String bytesToHexString(byte[] src) {
        var stringBuilder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return "";
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
     * 初始化一些常用的 文件类型
     */
    private static void initFileTypeList() {
        add("epub", "application/epub+zip");
        add("ink", "application/inkml+xml");
        add("jar", "application/java-archive");
        add("class", "application/java-vm");
        add("js", "application/javascript", "696b2e71623d696b2e71", "js");
        add("json", "application/json");
        add("doc", "application/msword", "d0cf11e0a1b11ae10000", " docx文件 (03 版本)");
        add("bin", "application/octet-stream");
        add("so", "application/octet-stream");
        add("m3u8", "application/vnd.apple.mpegurl");
        add("cab", "application/vnd.ms-cab-compressed");
        add("xls", "application/vnd.ms-excel");
        add("ppt", "application/vnd.ms-powerpoint");
        add("wps", "application/vnd.ms-works");
        add("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        add("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        add("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document", "504b0304140006000800", " docx文件 (07+ 版本)");
        add("rtf", "application/x-rtf", "7b5c727466315c616e73", "Rich Text Format (rtf)");
        add("rm", "application/vnd.rn-realmedia");
        add("rmvb", "application/vnd.rn-realmedia-vbr", "2e524d46000000120001", "rmvb");
        add("wasm", "application/wasm");
        add("7z", "application/x-7z-compressed");
        add("dmg", "application/x-apple-diskimage");
        add("otf", "application/x-font-otf");
        add("ttf", "application/x-font-ttf");
        add("ttc", "application/x-font-ttf");
        add("woff", "application/x-font-woff");
        add("woff2", "font/woff2");
        add("install", "application/x-install-instructions");
        add("iso", "application/x-iso9660-image");
        add("prc", "application/x-mobipocket-ebook");
        add("mobi", "application/x-mobipocket-ebook");
        add("application", "application/x-ms-application");
        add("lnk", "application/x-ms-shortcut");
        add("exe", "application/x-msdownload", "4d5a9000030000000400", "可执行文件");
        add("dll", "application/x-msdownload");
        add("com", "application/x-msdownload");
        add("bat", "application/x-msdownload");
        add("msi", "application/x-msdownload");
        add("sh", "application/x-sh");
        add("swf", "application/x-shockwave-flash");
        add("sql", "application/x-sql");
        add("obj", "application/x-tgif");
        add("yml", "application/x-yaml");
        add("yaml", "application/x-yaml");
        add("xaml", "application/xaml+xml");
        add("xhtml", "application/xhtml+xml");
        add("xht", "application/xhtml+xml");
        add("xml", "application/xml", "3c3f786d6c2076657273", "xml文件");
        add("xsl", "application/xml");
        add("dtd", "application/xml-dtd");
        add("xslt", "application/xslt+xml");
        add("zip", "application/zip");
        add("mp4a", "audio/mp4");
        add("mpga", "audio/mpeg");
        add("mp2", "audio/mpeg");
        add("mp2a", "audio/mpeg");
        add("mp3", "audio/mpeg", "49443303000000002176", "mp3 文件");
        add("m2a", "audio/mpeg");
        add("m3a", "audio/mpeg");
        add("oga", "audio/ogg");
        add("ogg", "audio/ogg");
        add("spx", "audio/ogg");
        add("rip", "audio/vnd.rip");
        add("weba", "audio/webm");
        add("aac", "audio/x-aac");
        add("aif", "audio/x-aiff");
        add("aiff", "audio/x-aiff");
        add("aifc", "audio/x-aiff");
        add("caf", "audio/x-caf");
        add("flac", "audio/x-flac");
        add("mka", "audio/x-matroska");
        add("m3u", "audio/x-mpegurl");
        add("wma", "audio/x-ms-wma");
        add("wav", "audio/x-wav", "52494646e27807005741", "Wave (wav)");
        add("bmp", "image/bmp", "424d228c010000000000", " 16色位图(bmp)", true);
        add("bmp", "image/bmp", "424d8240090000000000", "24位位图(bmp)", true);
        add("bmp", "image/bmp", "424d8e1b030000000000", " 256色位图(bmp)", true);
        add("gif", "image/gif", "47494638396126026f01", " GIF (gif)", true);
        add("jpeg", "image/jpeg", "ffd8ffe000104a464946", "JPEG (jpg)", true);
        add("jpg", "image/jpeg", "ffd8ffe000104a464946", "JPEG (jpg)", true);
        add("jpe", "image/jpeg");
        add("png", "image/png", "89504e470d0a1a0a0000", "PNG (png)", true);
        add("svg", "image/svg+xml");
        add("svgz", "image/svg+xml");
        add("tiff", "image/tiff", "49492a00227105008037", "TIFF (tif)", true);
        add("tif", "image/tiff", "49492a00227105008037", "TIFF (tif)", true);
        add("psd", "image/vnd.adobe.photoshop");
        add("webp", "image/webp");
        add("ico", "image/x-icon", "", "ico 图标文件");
        add("css", "text/css", "48544d4c207b0d0a0942", "css 样式表");
        add("csv", "text/csv");
        add("html", "text/html", "3c21444f435459504520", " HTML (html)");
        add("htm", "text/html", "3c21646f637479706520", "HTM (htm)");
        add("txt", "text/plain");
        add("text", "text/plain");
        add("conf", "text/plain");
        add("log", "text/plain");
        add("uri", "text/uri-list");
        add("uris", "text/uri-list");
        add("urls", "text/uri-list");
        add("s", "text/x-asm");
        add("asm", "text/x-asm");
        add("c", "text/x-c");
        add("cc", "text/x-c");
        add("cxx", "text/x-c");
        add("cpp", "text/x-c");
        add("h", "text/x-c");
        add("hh", "text/x-c");
        add("dic", "text/x-c");
        add("java", "text/x-java-source");
        add("nfo", "text/x-nfo");
        add("sfv", "text/x-sfv");
        add("3gp", "video/3gpp");
        add("3g2", "video/3gpp2");
        add("h261", "video/h261");
        add("h263", "video/h263");
        add("h264", "video/h264");
        add("jpgv", "video/jpeg");
        add("jpm", "video/jpm");
        add("jpgm", "video/jpm");
        add("mj2", "video/mj2");
        add("mjp2", "video/mj2");
        add("mp4", "video/mp4", "00000020667479706d70", " mp4 视频文件");
        add("mp4v", "video/mp4");
        add("mpg4", "video/mp4");
        add("mpeg", "video/mpeg");
        add("mpg", "video/mpeg", "000001ba210001000180", "mpg 文件");
        add("mpe", "video/mpeg");
        add("m1v", "video/mpeg");
        add("m2v", "video/mpeg", "");
        add("ogv", "video/ogg");
        add("qt", "video/quicktime");
        add("mov", "video/quicktime");
        add("webm", "video/webm");
        add("f4v", "video/x-f4v");
        add("flv", "video/x-flv");
        add("m4a", "audio/x-m4a");
        add("m4v", "video/x-m4v");
        add("mkv", "video/x-matroska");
        add("mk3d", "video/x-matroska");
        add("vob", "video/x-ms-vob");
        add("wmv", "video/x-ms-wmv", "3026b2758e66cf11a6d9", "wmv 文件");
        add("avi", "video/x-msvideo", "52494646d07d60074156", "avi 文件");
        add("pdf", "application/pdf", "255044462d312e350d0a", "pdf 文件");
        add("torrent", "application/x-bittorrent", "6431303a637265617465", "种子文件");
    }

}
