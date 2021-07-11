package cool.scx.vo;

import cool.scx.Scx;
import cool.scx.exception.NotFoundException;
import cool.scx.util.FileTypeUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * 文件下载 vo
 *
 * @author scx567888
 * @version 1.0.10
 */
public class Download implements BaseVo {

    /**
     * 待下载的文件
     */
    private final File file;

    /**
     * 要下载的 byte (一般用于 excel 之类的)
     */
    private final byte[] bytes;

    /**
     * 下载时的文件名称
     */
    private final String downloadName;

    /**
     * 节流桶的大小
     * 不宜过大 会造成内存占用过高 , 也不建议过小会造成请求写入过于频繁
     * 此处设置为 128KB (1MB)
     */
    private final int bucketSize;

    /**
     * 暂停时间 用于节流下载 -1 为不节流
     */
    private final long pauseTime;

    /**
     * 下载文件 不进行节流限速 文件名称为文件原始名称
     *
     * @param file a {@link java.io.File} object.
     */
    public Download(File file) {
        this(file, file.getName());
    }

    /**
     * 下载文件 不进行节流限速 文件名称为自定义名称
     *
     * @param file         a {@link java.io.File} object.
     * @param downloadName a {@link java.lang.String} object.
     */
    public Download(File file, String downloadName) {
        this(file, downloadName, -1);
    }

    /**
     * 下载文件 进行节流限速 文件名称为文件原始名称
     *
     * @param file                a {@link java.io.File} object.
     * @param downloadKBPerSecond a {@link java.lang.String} object.
     */
    public Download(File file, int downloadKBPerSecond) {
        this(file, file.getName(), downloadKBPerSecond);
    }

    /**
     * 下载文件
     *
     * @param file                待下载的文件
     * @param downloadName        下载时的名称
     * @param downloadKBPerSecond 限速每秒多少 单位 kb
     */
    public Download(File file, String downloadName, int downloadKBPerSecond) {
        this(file, null, downloadName, downloadKBPerSecond);
    }

    /**
     * 下载 字节数组
     *
     * @param bytes        字节数组
     * @param downloadName 下载名称 如果是 包含正确文件名的名称 则会 相应的对响应头进行处理
     */
    public Download(byte[] bytes, String downloadName) {
        this(bytes, downloadName, -1);
    }

    /**
     * <p>Constructor for Download.</p>
     *
     * @param bytes               an array of {@link byte} objects.
     * @param downloadName        a {@link java.lang.String} object.
     * @param downloadKBPerSecond a long.
     */
    public Download(byte[] bytes, String downloadName, int downloadKBPerSecond) {
        this(null, bytes, downloadName, downloadKBPerSecond);
    }

    /**
     * 初始化
     *
     * @param bytes               文件
     * @param downloadName        下载的文件名称
     * @param downloadKBPerSecond 每秒多少 kb
     */
    private Download(File file, byte[] bytes, String downloadName, int downloadKBPerSecond) {
        this.file = file;
        this.bytes = bytes;
        this.downloadName = downloadName;
        if (downloadKBPerSecond > 0) {
            // 小于 10MB 就采用 控制 控制缓冲区大小的方法
            if (downloadKBPerSecond < 10000) {
                this.pauseTime = 1000;
                this.bucketSize = downloadKBPerSecond * 1000;
            } else { // 大于 10MB 就采用  暂停时间的方法
                this.bucketSize = 2048 * 1000;
                this.pauseTime = (long) (bucketSize * 1.0 / downloadKBPerSecond);
            }
        } else {
            this.bucketSize = 2048 * 1000;
            this.pauseTime = -1;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void sendToClient(RoutingContext context) throws Exception {
        if (file != null) {
            if (!file.exists()) {
                throw new NotFoundException();
            } else {
                sendFile(context);
            }
        } else {
            sendBytes(context);
        }
    }

    /**
     * @param context c
     * @throws UnsupportedEncodingException c
     */
    private void sendFile(RoutingContext context) throws UnsupportedEncodingException, NotFoundException {
        var request = context.request();
        var response = context.response();
        var mimeType = FileTypeUtils.getMimeTypeForFilename(file.getName());
        //文件的真实大小
        var fileSize = file.length();
        //通知 客户端 服务端支持断点续传
        response.putHeader("Accept-Ranges", "bytes");
        response.putHeader("Content-Range", "bytes */" + fileSize);
        //通知客户端服务器端 文件的类型 如果未知就返回流
        response.putHeader("Content-Type", mimeType != null ? mimeType : "application/octet-stream");
        //通知客户端 类型为下载
        response.putHeader("Content-Disposition", "attachment;filename=" + new String(downloadName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        //客户端要求的文件起始位置
        var fromPos = 0L;
        //客户端要求的文件结束
        var range = request.getHeader("Range");
        //若客户端没有传来Range，说明并没有下载过此文件
        if (range != null) {
            // 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
            response.setStatusCode(206);
            // 因为请求头是这样的格式 所以再次进行分割提取信息 Range: bytes=12-100
            var ary = range.replaceAll("bytes=", "").split("-");
            fromPos = Long.parseLong(ary[0]);
        }
        response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(fileSize));
        // 复制文件流 到客户端
        try {
            var in = new RandomAccessFile(file, "r");
            if (fromPos > 0) {
                in.seek(fromPos);
            }
            var bucketSize0 = (int) Math.min(fileSize, bucketSize);
            writeFile(response, in, bucketSize0);
        } catch (IOException e) {
            throw new NotFoundException();
        }
    }

    private void writeFile(HttpServerResponse response, RandomAccessFile accessFile, int bucketSize) {
        var b = new byte[bucketSize];
        try {
            // 文件读取结束
            if (accessFile.read(b) == -1) {
                accessFile.close();
                response.end();
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        response.write(Buffer.buffer(b), (r) -> {
            if (r.succeeded()) {
                if (pauseTime > 0) {
                    Scx.setTimer(pauseTime, () -> writeFile(response, accessFile, bucketSize));
                } else {
                    writeFile(response, accessFile, bucketSize);
                }
            } else {
                // 请求失败 关闭文件
                try {
                    accessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void sendBytes(RoutingContext context) throws UnsupportedEncodingException {
        var request = context.request();
        var response = context.response();
        var mimeType = FileTypeUtils.getMimeTypeForFilename(downloadName);
        //通知 客户端 服务端支持断点续传
        response.putHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        //通知客户端服务器端 文件的类型 如果未知就返回流
        response.putHeader("Content-Type", mimeType != null ? mimeType : "application/octet-stream");
        //通知客户端 类型为下载
        response.putHeader("Content-Disposition", "attachment;filename=" + new String(downloadName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        //文件的真实大小
        var downloadSize = bytes.length;
        //客户端要求的文件起始位置
        var fromPos = 0;
        var range = request.getHeader("Range");
        //若客户端没有传来Range，说明并没有下载过此文件
        if (range != null) {
            // 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
            response.setStatusCode(206);
            // 因为请求头是这样的格式 所以再次进行分割提取信息 Range: bytes=12-100
            var ary = range.replaceAll("bytes=", "").split("-");
            fromPos = Integer.parseInt(ary[0]);
        }
        response.putHeader("content-length", String.valueOf(downloadSize));
        //分块的大小
        var bucketSize0 = Math.min(downloadSize, bucketSize);
        writeBytes(response, fromPos, bucketSize0);
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
                if (pauseTime > 0) {
                    Scx.setTimer(pauseTime, () -> writeBytes(response, to, length));
                } else {
                    writeBytes(response, to, length);
                }
            }
        });
    }
}
