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

    public Where add(String fieldName, WhereType whereType, Object value) {
        whereBodyList.add(new WhereBody(fieldName, whereType, value));
        return this;
    }

    public Where whereSql(String whereSql) {
        whereSQL.add(whereSql);
        return this;
    }

    public static class WhereBody {

        public String fieldName;

        public WhereType whereType;

        public Object value;

        public WhereBody(String fieldName, WhereType whereType, Object value) {
            this.fieldName = fieldName;
            this.whereType = whereType;
            this.value = value;
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
