package cool.scx.base;

import java.util.List;

class UpdateResult {
    public final Integer affectedLength;
    public final List<Long> generatedKeys;

    UpdateResult(Integer affectedLength, List<Long> generatedKeys) {
        this.affectedLength = affectedLength;
        this.generatedKeys = generatedKeys;
    }
}