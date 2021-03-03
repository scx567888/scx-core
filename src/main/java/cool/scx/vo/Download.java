package cool.scx.vo;

import cool.scx.base.BaseVo;
import cool.scx.util.file.FileUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpHeaders;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;

/**
 * <p>Download class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class Download implements BaseVo {
    /**
     * 待下载的文件
     */
    public final File file;
    /**
     * 下载时的文件名称
     */
    public final String downloadName;
    /**
     * 节流大小 (也作为内部桶的大小) 以 byte/秒 为单位
     * 小于 0 为不节流
     */
    public final long throttle;

    /**
     * 内部标识 是否节流
     */
    private final boolean openThrottle;

    /**
     * <p>Constructor for Download.</p>
     *
     * @param file a {@link java.io.File} object.
     */
    public Download(File file) {
        this(file, file.getName());
    }

    /**
     * <p>Constructor for Download.</p>
     *
     * @param file         a {@link java.io.File} object.
     * @param downloadName a {@link java.lang.String} object.
     */
    public Download(File file, String downloadName) {
        this(file, downloadName, 512000L);
    }

    /**
     * <p>Constructor for Download.</p>
     *
     * @param _file         a {@link java.io.File} object.
     * @param _downloadName a {@link java.lang.String} object.
     * @param _throttle     a long.
     */
    public Download(File _file, String _downloadName, long _throttle) {
        this.file = _file;
        this.downloadName = _downloadName;
        if (_throttle > 0) {
            this.throttle = _throttle;
            //节流
            openThrottle = true;
        } else {
            //不节流
            openThrottle = false;
            this.throttle = 512000L;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * todo 现在的大文件过度占用内存 此处需要优化
     */
    @Override
    public void sendToClient(RoutingContext context) throws Exception {
        var request = context.request();
        var response = context.response();
        var fileType = FileUtils.getFileTypeByHead(file);
        //通知 客户端 服务端支持断点续传
        response.putHeader(HttpHeaders.ACCEPT_RANGES, "bytes");
        //通知客户端服务器端 文件的类型 如果未知就返回流
        response.putHeader("Content-Type", fileType != null ? fileType.contentType : "application/octet-stream");
        //通知客户端 类型为下载
        response.putHeader("Content-Disposition", "attachment;filename=" + new String(downloadName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        //文件的真实大小
        var downloadSize = file.length();
        //客户端要求的文件起始位置
        var fromPos = 0L;
        //客户端要求的文件结束
        var toPos = 0L;
        var range = request.getHeader("Range");
        //若客户端没有传来Range，说明并没有下载过此文件
        if (range != null) {
            // 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
            response.setStatusCode(206);
            // 因为请求头是这样的格式 所以再次进行分割提取信息 Range: bytes=12-100
            var ary = range.replaceAll("bytes=", "").split("-");
            fromPos = Long.parseLong(ary[0]);
            if (ary.length == 2) {
                toPos = Long.parseLong(ary[1]);
            }
            if (toPos > fromPos) {
                downloadSize = toPos - fromPos;
            } else {
                downloadSize = downloadSize - fromPos;
            }
        }
        response.putHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(downloadSize));
        // 复制文件流 到客户端
        try (var in = new RandomAccessFile(file, "r")) {
            //已只读方式 获取文件
            //设置下载起始位置
            if (fromPos > 0) {
                in.seek(fromPos);
            }
            var bucketSize = (int) Math.min(downloadSize, openThrottle ? throttle / 2 : throttle);

            var bucket = new byte[bucketSize];
            // 桶(缓冲区)
            while (in.read(bucket) != -1) {
                response.write(Buffer.buffer(bucket));
                if (openThrottle) {
                    Thread.sleep(500);
                }
            }
            response.end();
        } catch (IOException e) {
            response.setStatusCode(404);
        }
    }
}
