package cool.scx.bo;

import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.base.BaseModel;
import cool.scx.enumeration.WhereType;
import cool.scx.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public final class Where {

    public List<WhereBody> whereBodyList = new ArrayList<>();

    public List<String> whereSQL = new ArrayList<>();

    /**
     * where 连接的类型 有 and (0) 和 or (1) 两种
     */
    private int connectType=0;

    public Where add(String fieldName, WhereType whereType, Object value) {
        whereBodyList.add(new WhereBody(fieldName, whereType, value, connectType));
        return this;
    }

    public Where add(String whereSql) {
        whereSQL.add(whereSql);
        return this;
    }

    public Where and(){

    }

    public Where or(){

    }

    public static class WhereBody {

        public final String fieldName;

        public final  WhereType whereType;

        public final Object value;

        public final int connectType;

        public WhereBody(String fieldName, WhereType whereType, Object value, int connectType) {
            this.fieldName = fieldName;
            this.whereType = whereType;
            this.value = value;
            this.connectType = connectType;
        }
    }

    public boolean isEmpty() {
        return whereBodyList.size() + whereSQL.size() == 0;
    }

    public Where() {

    }

    /**
     * 直接根据实体类生成 Where 条件 (注意此处的所有 whereType 都是 EQUAL 或 LIKE (使用首尾全匹配) ) <br>
     * 如需更细粒度的控制请使用 add 方法
     *
     * @param entity   e
     * @param <Entity> e
     * @return 返回自己 方便链式调用
     */
    public <Entity extends BaseModel> Where addByObject(Entity entity) {
        return addByObject(entity, false);
    }

    /**
     * 直接根据实体类生成 Where 条件 (注意此处的所有 whereType 都是 EQUAL 或 LIKE ) <br>
     * 如需更细粒度的控制请使用 add 方法
     *
     * @param entity     e
     * @param ignoreLike 是否忽略 实体类上的 like 注解
     * @param <Entity>   e
     * @return 返回自己 方便链式调用
     */
    public <Entity extends BaseModel> Where addByObject(Entity entity, boolean ignoreLike) {
        var allFields = Stream.of(entity.getClass().getFields()).filter(field -> !field.isAnnotationPresent(NoColumn.class)).toArray(Field[]::new);
        if (ignoreLike) {
            for (Field field : allFields) {
                Object v = ObjectUtils.getFieldValue(field, entity);
                if (v != null) {
                    this.add(field.getName(), WhereType.EQUAL, v);
                }
            }
        } else {
            for (Field field : allFields) {
                Object v = ObjectUtils.getFieldValue(field, entity);
                if (v != null) {
                    var column = field.getAnnotation(Column.class);
                    if (column != null && column.useLike()) {
                        this.add(field.getName(), WhereType.LIKE, v);
                    } else {
                        this.add(field.getName(), WhereType.EQUAL, v);
                    }
                }
            }
        }
        return this;
    }

}
