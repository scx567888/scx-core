package cool.scx.bo;

import java.util.List;

/**
 * <p>UpdateResult class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class UpdateResult {
    public final Integer affectedLength;
    public final List<Long> generatedKeys;

    /**
     * <p>Constructor for UpdateResult.</p>
     *
     * @param affectedLength a {@link java.lang.Integer} object.
     * @param generatedKeys  a {@link java.util.List} object.
     */
    public UpdateResult(Integer affectedLength, List<Long> generatedKeys) {
        this.affectedLength = affectedLength;
        this.generatedKeys = generatedKeys;
    }
}
