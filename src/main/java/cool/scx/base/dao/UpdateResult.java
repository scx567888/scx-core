package cool.scx.base.dao;

import java.util.List;

public class UpdateResult {
    public final Integer affectedLength;
    public final List<Long> generatedKeys;

    UpdateResult(Integer affectedLength, List<Long> generatedKeys) {
        this.affectedLength = affectedLength;
        this.generatedKeys = generatedKeys;
    }
}