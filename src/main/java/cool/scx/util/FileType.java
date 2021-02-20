package cool.scx.util;

public class FileType {
    public final String head;
    public final String suffix;
    public final String contentType;
    public final String description;

    public FileType(String head, String suffix, String contentType, String description) {
        this.head = head;
        this.suffix = suffix;
        this.contentType = contentType;
        this.description = description;
    }
}