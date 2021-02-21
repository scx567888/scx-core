package cool.scx.vo;

import cool.scx.base.BaseVo;
import cool.scx.util.FileUtils;
import io.vertx.core.buffer.Buffer;
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
    public File file;
    /**
     * 下载时的文件名称
     */
    public String downloadName;
    /**
     * 是否支持断点续传
     */
    public Boolean resume;
    /**
     * 节流大小 (byte) 以秒为单位
     * -1 为不节流
     */
    public Long throttle;

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
     * @param file a {@link java.io.File} object.
     * @param downloadName a {@link java.lang.String} object.
     */
    public Download(File file, String downloadName) {
        this(file, downloadName, true);
    }

    /**
     * <p>Constructor for Download.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param downloadName a {@link java.lang.String} object.
     * @param resume a {@link java.lang.Boolean} object.
     */
    public Download(File file, String downloadName, Boolean resume) {
        this(file, downloadName, resume, 512000L);
    }

    /**
     * <p>Constructor for Download.</p>
     *
     * @param file a {@link java.io.File} object.
     * @param downloadName a {@link java.lang.String} object.
     * @param resume a {@link java.lang.Boolean} object.
     * @param throttle a {@link java.lang.Long} object.
     */
    public Download(File file, String downloadName, Boolean resume, Long throttle) {
        this.file = file;
        this.downloadName = downloadName;
        this.resume = resume;
        this.throttle = throttle;
    }

    /** {@inheritDoc} */
    @Override
    public void sendToClient(RoutingContext context) throws Exception {
        var request = context.request();
        var response = context.response();
        var fileType = FileUtils.getFileTypeByHead(file);
        //    // 获取文件的 MIME 类型
        response.putHeader("Content-Type", fileType != null ? fileType.contentType : "application/octet-stream");
        response.putHeader("Content-Disposition", "attachment;filename=" + new String(downloadName.getBytes(StandardCharsets.UTF_8), "ISO8859-1"));
        //    // 代表了该服务器可以接受范围请求
        response.putHeader("Accept-Ranges", "bytes");
        var downloadSize = file.length();
        var fromPos = 0L;
        var toPos = 0L;
        var range = request.getHeader("Range");
        //    // 若客户端没有传来Range，说明并没有下载过此文件
        if (range != null) {
            // 若客户端传来Range，说明之前下载了一部分，设置206状态(SC_PARTIAL_CONTENT)
            response.setStatusCode(206);
            // 因为请求头是这样的格式 所以再次进行分割提取信息 Range: bytes=12-100
            String bytes = range.replaceAll("bytes=", "");
            String[] ary = bytes.split("-");
            fromPos = Integer.parseInt(ary[0]);
            if (ary.length == 2) {
                toPos = Integer.parseInt(ary[1]);
            }
            int size;
            if (toPos > fromPos) {
                size = (int) (toPos - fromPos);
            } else {
                size = (int) (downloadSize - fromPos);
            }
            downloadSize = size;
        }
        response.putHeader("Content-Length", downloadSize + "");
        // 复制文件流 到客户端
        try (RandomAccessFile in = new RandomAccessFile(file, "r")) {
            //已只读方式 获取文件
            // 设置下载起始位置
            if (fromPos > 0) {
                in.seek(fromPos);
            }
            //不节流
            if (throttle == -1) {
                // 缓冲区大小 如果文件小于 800 kb 设置为文件大小 否则 设置缓冲为 800 kb
                int bufLen = (int) (downloadSize < 819200 ? downloadSize : 819200);
                byte[] buffer = new byte[bufLen];
                int num;
                int count = 0; // 当前写到客户端的大小
                while ((num = in.read(buffer)) != -1) {
                    response.write(Buffer.buffer(buffer));
                    count += num;
                    //处理最后一段，计算不满缓冲区的大小
                    if (downloadSize - count < bufLen) {
                        bufLen = (int) (downloadSize - count);
                        if (bufLen == 0) {
                            break;
                        }
                        buffer = new byte[bufLen];
                    }
                }
            }
            //节流
            else {
                //减少桶的大小使客户端减少卡顿
                throttle = throttle / 2;
                // 缓冲区大小 如果文件小于 800 kb 设置为文件大小 否则 设置缓冲为 800 kb
                int bufLen = (int) (downloadSize < throttle ? downloadSize : throttle);
                byte[] buffer = new byte[bufLen];
                int num;
                int count = 0; // 当前写到客户端的大小
                while ((num = in.read(buffer)) != -1) {
                    response.write(Buffer.buffer(buffer));
                    Thread.sleep(500);
                    count += num;
                    //处理最后一段，计算不满缓冲区的大小
                    if (downloadSize - count < bufLen) {
                        bufLen = (int) (downloadSize - count);
                        if (bufLen == 0) {
                            break;
                        }
                        buffer = new byte[bufLen];
                    }
                }
            }
            response.end();
        } catch (IOException e) {
            response.setStatusCode(404);
        }
    }
}
