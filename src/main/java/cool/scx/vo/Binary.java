package cool.scx.vo;

import cool.scx.base.BaseVo;
import cool.scx.enumeration.BinaryType;
import cool.scx.util.FileTypeUtils;
import cool.scx.util.StringUtils;
import io.vertx.core.buffer.Buffer;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.StandardCharsets;

public class Binary implements BaseVo {
    private File file;
    private byte[] bytes;
    private Boolean download = false;
    private String downloadName = "";
    private BinaryType binaryType;

    public Binary(String filePath) {
        try {
            file = new File(filePath);
        } catch (Exception e) {

        }
    }

    public Binary(File _file) {
        file = _file;
    }

    public Binary(byte[] _bytes) {
        bytes = _bytes;
    }

    public Binary(String filePath, BinaryType _binaryType) {
        file = new File(filePath);
        binaryType = _binaryType;
    }

    public Binary(File _file, BinaryType _binaryType) {
        file = _file;
        binaryType = _binaryType;
    }

    @Override
    public Buffer getBuffer() {
        try {
            if (file == null) {
                return Buffer.buffer(bytes);
            } else {
                var input = new FileInputStream(file);
                byte[] byt = new byte[input.available()];
                int read = input.read(byt);
                return Buffer.buffer(byt);
            }

        } catch (Exception e) {
            return Buffer.buffer();
        }
    }

    @Override
    public String getContentType() {
        return FileTypeUtils.getFileTypeByHead(file).contentType;
    }

    @Override
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

    public Binary setDownload(boolean b) {
        download = b;
        return this;
    }

    public Binary setDownload(String b) {
        download = true;
        downloadName = b;
        return this;
    }
}
