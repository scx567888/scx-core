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

    public UpdateResult(Integer affectedLength, List<Long> generatedKeys) {
        this.affectedLength = affectedLength;
        this.generatedKeys = generatedKeys;
    }
}
