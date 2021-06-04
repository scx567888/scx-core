package cool.scx.bo;

import io.vertx.core.buffer.Buffer;

/**
 * 文件上传后台接受容器类
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class FileUpload {

    /**
     * 表单中的名称
     */
    public String name;

    /**
     * 上传的文件的真实名称
     */
    public String fileName;

    /**
     * 文件大小
     */
    public long fileSize;

    /**
     * 文件内容
     */
    public Buffer buffer;

    /**
     * 构造函数
     *
     * @param name     a {@link java.lang.String} object.
     * @param fileName a {@link java.lang.String} object.
     * @param fileSize a {@link java.lang.Long} object.
     * @param buffer   a {@link io.vertx.core.buffer.Buffer} object.
     */
    public FileUpload(String name, String fileName, Long fileSize, Buffer buffer) {
        this.name = name;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.buffer = buffer;
    }
}
