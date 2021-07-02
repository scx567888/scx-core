package cool.scx.vo;

import cool.scx.exception.BadRequestException;
import cool.scx.exception.NotFoundException;
import cool.scx.util.FileType;
import cool.scx.util.FileTypeUtils;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.RoutingContext;

import java.io.File;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 二进制文件 但不需要下载的 vo
 * 比如 pdf 之类
 *
 * @author scx567888
 * @version 0.7.0
 */
public class Binary implements BaseVo {

    /**
     * 正则表达式 用于校验 RANGE 字段
     */
    private static final Pattern RANGE = Pattern.compile("^bytes=(\\d+)-(\\d*)$");

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
     */
    private void sendFile(RoutingContext context) throws BadRequestException {
        var request = context.request();
        var response = context.response();
        //只处理 get 或 head 请求
        if (request.method() != HttpMethod.GET && request.method() != HttpMethod.HEAD) {
            throw new BadRequestException();
        } else {
            //获取文件路径
            var path = file.getPath();
            //获取当前文件的基本信息
            var fileLength = file.length();

            //偏移量
            Long offset = null;
            long end;
            var headers = response.headers();
            //如果留已经关闭
            if (response.closed()) {
                return;
            }
            //检查客户端是否正在进行范围请求
            String range = request.getHeader("Range");
            // end byte is length - 1
            end = fileLength - 1;
            //正在进行范围请求 这里进行偏移量的计算
            if (range != null) {
                Matcher m = RANGE.matcher(range);
                if (m.matches()) {
                    try {
                        String part = m.group(1);
                        // offset cannot be empty
                        offset = Long.parseLong(part);
                        // offset must fall inside the limits of the file
                        if (offset < 0 || offset >= fileLength) {
                            throw new IndexOutOfBoundsException();
                        }
                        // length can be empty
                        part = m.group(2);
                        if (part != null && part.length() > 0) {
                            // ranges are inclusive
                            end = Math.min(end, Long.parseLong(part));
                            // end offset must not be smaller than start offset
                            if (end < offset) {
                                throw new IndexOutOfBoundsException();
                            }
                        }
                    } catch (NumberFormatException | IndexOutOfBoundsException e) {
                        context.response().putHeader("content-range", "bytes */" + fileLength);
                        context.fail(416);
                        return;
                    }
                }
            }

            //通知客户我们支持范围请求
            headers.set("accept-ranges", "bytes");
            //发送内容长度，即使是头部请求
            headers.set("content-length", Long.toString(end + 1 - (offset == null ? 0 : offset)));
            //写入缓存

            String contentType = FileTypeUtils.getMimeTypeForFilename(path);
            if (contentType != null) {
                if (contentType.startsWith("text")) {
                    response.putHeader("content-type", contentType + ";charset=UTF-8");
                } else {
                    response.putHeader("content-type", contentType);
                }
            }
            if (offset != null) {
                // 返回206
                headers.set("content-range", "bytes " + offset + "-" + end + "/" + fileLength);
                response.setStatusCode(206);

                long finalOffset = offset;
                long finalLength = end + 1 - offset;

                response.sendFile(path, finalOffset, finalLength, res2 -> {
                    if (res2.failed()) {
                        context.fail(res2.cause());
                    }
                });
            } else {

                response.sendFile(path, res2 -> {
                    if (res2.failed()) {
                        context.fail(res2.cause());
                    }
                });
            }

        }
    }

    private void sendBytes(RoutingContext context) {
        var response = context.response();
        var mimeType = fileType != null && fileType.mimeType != null ? fileType.mimeType : "application/octet-stream";
        response.putHeader("Content-Type", mimeType);
        response.putHeader("Content-Disposition", "inline");
        //文件的真实大小
        var downloadSize = bytes.length;
        //客户端要求的文件起始位置
        response.putHeader("content-length", String.valueOf(downloadSize));
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
