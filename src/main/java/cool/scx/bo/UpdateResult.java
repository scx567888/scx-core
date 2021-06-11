package cool.scx.bo;

import java.util.List;

/**
 * 数据库更新结果
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class UpdateResult {

    /**
     * 受影响的行数
     */
    public final Integer affectedLength;

    /**
     * 主键 id 集合
     */
    public final List<Long> generatedKeys;

    /**
     * c
     *
     * @param affectedLength a {@link java.lang.Integer} object.
     * @param generatedKeys  a {@link java.util.List} object.
     */
    public UpdateResult(Integer affectedLength, List<Long> generatedKeys) {
        this.affectedLength = affectedLength;
        this.generatedKeys = generatedKeys;
    }

}
