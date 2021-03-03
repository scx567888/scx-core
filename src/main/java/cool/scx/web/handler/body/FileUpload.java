package cool.scx.web.handler.body;

import io.vertx.core.buffer.Buffer;

/**
 * <p>FileUpload class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class FileUpload {

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
     * <p>Constructor for FileUpload.</p>
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
