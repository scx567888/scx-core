package cool.scx.util;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

public class FileTypeUtils {

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

    static {
        IMAGE_FILE_TYPE_LIST = getImageFileTypeList();
        OTHER_FILE_TYPE_LIST = getOtherFileType();
        ALL_FILE_TYPE_LIST = new ArrayList<>();
        ALL_FILE_TYPE_LIST.addAll(IMAGE_FILE_TYPE_LIST);
        ALL_FILE_TYPE_LIST.addAll(OTHER_FILE_TYPE_LIST);
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
     * @param src
     * @return
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
     * @param file
     * @return
     */
    public static FileType getFileTypeBySuffix(File file, List<FileType> FILE_TYPE_LIST) {
        var fileName = file.getName();
        var fileSuffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        return FILE_TYPE_LIST.stream().filter(fileType -> fileType.suffix.equals(fileSuffix)).findAny().orElse(null);
    }


    public static FileType getFileTypeByHead(File file) {
        return getFileTypeByHead(file, ALL_FILE_TYPE_LIST);
    }

    /**
     *
     */
    public static FileType getFileTypeByHead(File file, List<FileType> FILE_TYPE_LIST) {
        try {
            var is = new FileInputStream(file);
            byte[] b = new byte[10];
            is.read(b, 0, b.length);
            var fileCode = bytesToHexString(b);
            return FILE_TYPE_LIST.stream().filter(fileType -> fileType.head.toLowerCase().startsWith(fileCode.toLowerCase().substring(0, 5)) || fileCode.toLowerCase().substring(0, 5).startsWith(fileType.head.toLowerCase())).findAny().orElse(null);
        } catch (Exception e) {
            return new FileType("", "", "", "");
        }
    }

    public static FileType getImageFileType(File file) {
        return getFileTypeBySuffix(file, IMAGE_FILE_TYPE_LIST);
    }

}