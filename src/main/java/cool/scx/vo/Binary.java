package cool.scx.vo;

import cool.scx.util.FileType;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.util.Arrays;

/**
 * 二进制文件 但不需要下载的 vo
 * 比如 pdf 之类
 *
 * @author 司昌旭
 * @version 0.7.0
 */
public class Binary implements BaseVo {

    /**
     * 文件
     */
    private final File file;

    /**
     * 文件类型 主要用于 数据类型是 byte 时
     */
    private final FileType fileType;

    /**
     * byte (一般用于 excel 之类的)
     */
    private final byte[] bytes;

    /**
     * 下载文件 不进行节流限速 文件名称为文件原始名称
     *
     * @param file a {@link java.io.File} object.
     */
    public Binary(File file) {
        this(file, null, null);
    }

    /**
     * <p>Constructor for Download.</p>
     *
     * @param bytes    an array of {@link byte} objects.
     * @param fileType 文件类型
     */
    public Binary(byte[] bytes, FileType fileType) {
        this(null, bytes, fileType);
    }

    /**
     * 初始化
     *
     * @param file     f
     * @param bytes    b
     * @param fileType 文件类型
     */
    private Binary(File file, byte[] bytes, FileType fileType) {
        this.file = file;
        this.bytes = bytes;
        this.fileType = fileType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendToClient(RoutingContext context) throws Exception {
        if (file != null) {
            if (!file.exists()) {
                context.fail(404);
            } else {
                sendFile(context);
            }
        } else {
            sendBytes(context);
        }
    }

    /**
     * @param context c
     */
    private void sendFile(RoutingContext context) {
        var response = context.response();
        response.sendFile(file.getPath());
    }

    private void sendBytes(RoutingContext context) {
        var response = context.response();
        var mimeType = fileType != null && fileType.mimeType != null ? fileType.mimeType : "application/octet-stream";
        response.putHeader("Content-Type", mimeType);
        response.putHeader("Content-Disposition", "inline");
        //文件的真实大小
        var downloadSize = bytes.length;
        //客户端要求的文件起始位置
        response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(downloadSize));
        //分块的大小 此处为固定值 1024*1000 (1MB)
        var bucketSize0 = Math.min(downloadSize, 1024 * 1000);
        writeBytes(response, 0, bucketSize0);
    }

    private void writeBytes(HttpServerResponse response, int _from, int length) {
        var to = Math.min(bytes.length, _from + length);
        var b = Arrays.copyOfRange(bytes, _from, to);
        if (_from + length >= bytes.length) {
            response.end(Buffer.buffer(b));
            return;
        }
        response.write(Buffer.buffer(b), (r) -> {
            if (r.succeeded()) {
                writeBytes(response, to, length);
            }
        });
    }

}
