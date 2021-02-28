package cool.scx.server.http.handler.body;

import io.vertx.core.buffer.Buffer;

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

    public FileUpload(String name, String fileName, Long fileSize, Buffer buffer) {
        this.name = name;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.buffer = buffer;
    }
}
