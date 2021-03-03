package cool.scx.util.file;

/**
 * <p>FileType class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class FileType {
    public final String head;
    public final String suffix;
    public final String contentType;
    public final String description;

    /**
     * <p>Constructor for FileType.</p>
     *
     * @param head        a {@link java.lang.String} object.
     * @param suffix      a {@link java.lang.String} object.
     * @param contentType a {@link java.lang.String} object.
     * @param description a {@link java.lang.String} object.
     */
    public FileType(String head, String suffix, String contentType, String description) {
        this.head = head;
        this.suffix = suffix;
        this.contentType = contentType;
        this.description = description;
    }
}
