package cool.scx.vo;

import cool.scx.base.BaseVo;
import cool.scx.util.FileType;
import cool.scx.util.FileUtils;
import cool.scx.util.StringUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

/**
 * <p>Binary class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class Binary implements BaseVo {
    private File file;
    private byte[] bytes;
    private Boolean download = false;
    private String downloadName = "";
    private FileType binaryType;

    /**
     * <p>Constructor for Binary.</p>
     *
     * @param filePath a {@link java.lang.String} object.
     */
    public Binary(String filePath) {
        try {
            file = new File(filePath);
        } catch (Exception ignored) {

        }
    }

    /**
     * <p>Constructor for Binary.</p>
     *
     * @param _file a {@link java.io.File} object.
     */
    public Binary(File _file) {
        file = _file;
    }

    /**
     * <p>Constructor for Binary.</p>
     *
     * @param _bytes an array of {@link byte} objects.
     */
    public Binary(byte[] _bytes) {
        bytes = _bytes;
    }

    /**
     * <p>Constructor for Binary.</p>
     *
     * @param filePath    a {@link java.lang.String} object.
     * @param _binaryType a {@link cool.scx.util.FileType} object.
     */
    public Binary(String filePath, FileType _binaryType) {
        file = new File(filePath);
        binaryType = _binaryType;
    }

    /**
     * <p>Constructor for Binary.</p>
     *
     * @param _file       a {@link java.io.File} object.
     * @param _binaryType a {@link cool.scx.util.FileType} object.
     */
    public Binary(File _file, FileType _binaryType) {
        file = _file;
        binaryType = _binaryType;
    }

    /**
     * {@inheritDoc}
     *
     * @return a {@link io.vertx.core.buffer.Buffer} object.
     */
    public Buffer getBuffer() {
        try {
            if (file == null) {
                return Buffer.buffer(bytes);
            } else {
                var input = new FileInputStream(file);
                byte[] byt = new byte[input.available()];
                int read = input.read(byt);
                input.close();
                return Buffer.buffer(byt);
            }

        } catch (Exception e) {
            return Buffer.buffer();
        }
    }

    /**
     * {@inheritDoc}
     *
     * @return a {@link java.lang.String} object.
     */
    public String getContentType() {
        return FileUtils.getFileTypeByHead(file).contentType;
    }

    /**
     * {@inheritDoc}
     *
     * @return a {@link java.lang.String} object.
     */
    public String getContentDisposition() {
        if (download) {
            if (StringUtils.isNotEmpty(downloadName)) {
                try {
                    return "attachment;filename=" + new String(downloadName.getBytes(StandardCharsets.UTF_8), "ISO8859-1");
                } catch (Exception ignored) {
                }
            }
            return "attachment;";
        }
        return null;
    }


    /**
     * <p>aaa.</p>
     *
     * @param response a {@link io.vertx.ext.web.RoutingContext} object.
     */
    public void aaa(RoutingContext response) {
//        response.putHeader("Content-Type", baseVo.getContentType());
//        String contentDisposition = baseVo.getContentDisposition();
//        if (StringUtils.isNotEmpty(contentDisposition)) {
//            response.putHeader("Content-Disposition", contentDisposition);
//        }
//        response.end(baseVo.getBuffer());
    }

    /**
     * <p>Setter for the field <code>download</code>.</p>
     *
     * @param b a boolean.
     * @return a {@link cool.scx.vo.Binary} object.
     */
    public Binary setDownload(boolean b) {
        download = b;
        return this;
    }

    /**
     * <p>Setter for the field <code>download</code>.</p>
     *
     * @param b a {@link java.lang.String} object.
     * @return a {@link cool.scx.vo.Binary} object.
     */
    public Binary setDownload(String b) {
        download = true;
        downloadName = b;
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public void sendToClient(RoutingContext context) {

    }

    /**
     * <p>isDownload.</p>
     *
     * @return a {@link cool.scx.vo.Binary} object.
     */
    public Binary isDownload() {
        return null;
    }

    /**
     * <p>Setter for the field <code>downloadName</code>.</p>
     */
    public void setDownloadName() {

    }
}
