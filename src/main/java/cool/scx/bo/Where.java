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

/**
 * where 查询条件封装类
 */
public final class Where {

    /**
     * 存储查询条件
     */
    public List<WhereBody> whereBodyList = new ArrayList<>();

    /**
     * 自定义的查询语句
     */
    public String whereSQL;

    /**
     * 添加一个查询条件 (注意 : 此处添加的所有条件都会以 and 拼接 , 如需使用 or 请考虑使用 {@link #whereSQL(String)} })
     *
     * @param fieldName 字段名称 (注意不是数据库名称)
     * @param whereType where 类型
     * @param value1    参数1
     * @param value2    参数2
     * @return 本身 , 方便链式调用
     */
    public Where add(String fieldName, WhereType whereType, Object value1, Object value2) {
        if (whereType.paramSize() == 2) {
            whereBodyList.add(new WhereBody(fieldName, whereType, value1, value2));
            return this;
        }
        throw new RuntimeException(" WhereType 类型 : " + whereType + " , 参数数量必须为 " + whereType.paramSize());
    }

    /**
     * 添加一个查询条件 (注意 : 此处添加的所有条件都会以 and 拼接 , 如需使用 or 请考虑使用 {@link #whereSQL(String)} })
     *
     * @param fieldName 字段名称 (注意不是数据库名称)
     * @param whereType where 类型
     * @param value1    参数1
     * @return 本身 , 方便链式调用
     */
    public Where add(String fieldName, WhereType whereType, Object value1) {
        if (whereType.paramSize() == 1) {
            whereBodyList.add(new WhereBody(fieldName, whereType, value1, null));
            return this;
        }
        throw new RuntimeException(" WhereType 类型 : " + whereType + " , 参数数量必须为 " + whereType.paramSize());
    }

    /**
     * 添加一个查询条件 (注意 : 此处添加的所有条件都会以 and 拼接 , 如需使用 or 请考虑使用 {@link #whereSQL(String)} })
     *
     * @param fieldName 字段名称 (注意不是数据库名称)
     * @param whereType where 类型
     * @return 本身 , 方便链式调用
     */
    public Where add(String fieldName, WhereType whereType) {
        if (whereType.paramSize() == 0) {
            whereBodyList.add(new WhereBody(fieldName, whereType, null, null));
            return this;
        }
        throw new RuntimeException(" WhereType 类型 : " + whereType + " , 参数数量必须为 " + whereType.paramSize());
    }

    /**
     * 添加一个 whereSql 适用于 复杂查询的自定义 where 子句
     *
     * @param whereSQL sql 语句
     * @return 本身 , 方便链式调用
     */
    public Where whereSQL(String whereSQL) {
        this.whereSQL = whereSQL;
        return this;
    }

    /**
     * 查询条件是否为空
     */
    public boolean isEmpty() {
        return whereBodyList.size() == 0 && whereSQL == null;
    }

    public Where() {

    }

    public Where(String whereSQL) {
        whereSQL(whereSQL);
    }

    public Where(String fieldName, WhereType whereType, Object value1, Object value2) {
        add(fieldName, whereType, value1, value2);
    }

    public Where(String fieldName, WhereType whereType, Object value) {
        add(fieldName, whereType, value);
    }

    public Where(String fieldName, WhereType whereType) {
        add(fieldName, whereType);
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

    /**
     * 为空
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @return this 方便链式调用
     */
    public Where isNull(String fieldName) {
        return add(fieldName, WhereType.IS_NULL);
    }

    /**
     * 不为空
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @return this 方便链式调用
     */
    public Where isNotNull(String fieldName) {
        return add(fieldName, WhereType.IS_NOT_NULL);
    }

    /**
     * 相等
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where equal(String fieldName, Object value) {
        return add(fieldName, WhereType.EQUAL, value);
    }

    /**
     * 不相等
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where notEqual(String fieldName, Object value) {
        return add(fieldName, WhereType.NOT_EQUAL, value);
    }

    /**
     * 大于
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where greaterThan(String fieldName, Object value) {
        return add(fieldName, WhereType.GREATER_THAN, value);
    }

    /**
     * 大于等于
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where greaterThanOrEqual(String fieldName, Object value) {
        return add(fieldName, WhereType.GREATER_THAN_OR_EQUAL, value);
    }

    /**
     * 小于
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where lessThan(String fieldName, Object value) {
        return add(fieldName, WhereType.LESS_THAN, value);
    }

    /**
     * 小于等于
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where lessThanOrEqual(String fieldName, Object value) {
        return add(fieldName, WhereType.LESS_THAN_OR_EQUAL, value);
    }

    /**
     * 两者之间
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value1    比较值1
     * @param value2    比较值2
     * @return this 方便链式调用
     */
    public Where between(String fieldName, Object value1, Object value2) {
        return add(fieldName, WhereType.BETWEEN, value1, value2);
    }

    /**
     * 不处于两者之间
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value1    比较值1
     * @param value2    比较值2
     * @return this 方便链式调用
     */
    public Where notBetween(String fieldName, Object value1, Object value2) {
        return add(fieldName, WhereType.NOT_BETWEEN, value1, value2);
    }


    /**
     * like : 根据 表达式进行判断
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     SQL 表达式
     * @return this 方便链式调用
     */
    public Where likeRegex(String fieldName, String value) {
        return add(fieldName, WhereType.LIKE_REGEX, value);
    }

    /**
     * 不 like : 根据 表达式进行判断
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     SQL 表达式
     * @return this 方便链式调用
     */
    public Where notLikeRegex(String fieldName, String value) {
        return add(fieldName, WhereType.NOT_LIKE_REGEX, value);
    }

    /**
     * like : 默认会在首尾添加 %
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     参数 默认会在首尾添加 %
     * @return this 方便链式调用
     */
    public Where like(String fieldName, Object value) {
        return add(fieldName, WhereType.LIKE, value);
    }

    /**
     * 不 like : 默认会在首尾添加 %
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     默认会在首尾添加 %
     * @return this 方便链式调用
     */
    public Where notLike(String fieldName, Object value) {
        return add(fieldName, WhereType.NOT_LIKE, value);
    }

    /**
     * 包含  : 一般用于 JSON 格式字段 区别于 in
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where contain(String fieldName, Object value) {
        return add(fieldName, WhereType.CONTAIN, value);
    }


    /**
     * 包含
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where in(String fieldName, Object value) {
        return add(fieldName, WhereType.IN, value);
    }

    /**
     * 不包含
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where notIn(String fieldName, Object value) {
        return add(fieldName, WhereType.NOT_IN, value);
    }

    /**
     * 不等于
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where not(String fieldName, Object value) {
        return add(fieldName, WhereType.NOT, value);
    }

    /**
     * 存在
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where exists(String fieldName, Object value) {
        return add(fieldName, WhereType.NOT, value);
    }

    /**
     * 不存在
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where notExists(String fieldName, Object value) {
        return add(fieldName, WhereType.NOT_EXISTS, value);
    }

    /**
     * where 封装体
     */
    public static class WhereBody {

        /**
         * 字段名称 (注意不是数据库名称)
         */
        public final String fieldName;

        /**
         * 类型
         */
        public final WhereType whereType;

        /**
         * 因为参数不固定 所以这里用两个参数
         * 参数1
         */
        public final Object value1;

        /**
         * 参数2
         */
        public final Object value2;

        public WhereBody(String fieldName, WhereType whereType, Object value1, Object value2) {
            this.fieldName = fieldName;
            this.whereType = whereType;
            this.value1 = value1;
            this.value2 = value2;
        }

    }

}
