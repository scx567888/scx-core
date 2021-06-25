package cool.scx.bo;

import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.base.BaseModel;
import cool.scx.enumeration.WhereType;
import cool.scx.util.Ansi;
import cool.scx.util.ObjectUtils;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * where 查询条件封装类
 * todo 数据校验
 *
 * @author 司昌旭
 * @version 1.2.0
 */
public final class Where {

    /**
     * 存储查询条件
     */
    public final Set<WhereBody> whereBodyList = new HashSet<>();

    /**
     * 自定义的查询语句
     */
    public String whereSQL;

    /**
     * 一个实体类的 class 对象 当不为空时可以对传进来的参数进行数据校验
     */
    private final Class<?> entityClass;

    /**
     * <p>Constructor for Where.</p>
     */
    public Where() {
        this.entityClass = null;
    }

    /**
     * 创建一个 Where 对象 (添加 where 条件时会根据 entityClass 校验数据)
     */
    public Where(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    /**
     * <p>Constructor for Where.</p>
     *
     * @param whereSQL a {@link java.lang.String} object
     */
    public Where(String whereSQL) {
        this.entityClass = null;
        whereSQL(whereSQL);
    }

    /**
     * <p>Constructor for Where.</p>
     *
     * @param fieldName a {@link java.lang.String} object
     * @param whereType a {@link cool.scx.enumeration.WhereType} object
     * @param value1    a {@link java.lang.Object} object
     * @param value2    a {@link java.lang.Object} object
     */
    public Where(String fieldName, WhereType whereType, Object value1, Object value2) {
        this.entityClass = null;
        add(fieldName, whereType, value1, value2);
    }

    /**
     * <p>Constructor for Where.</p>
     *
     * @param fieldName a {@link java.lang.String} object
     * @param whereType a {@link cool.scx.enumeration.WhereType} object
     * @param value     a {@link java.lang.Object} object
     */
    public Where(String fieldName, WhereType whereType, Object value) {
        this.entityClass = null;
        add(fieldName, whereType, value);
    }

    /**
     * <p>Constructor for Where.</p>
     *
     * @param fieldName a {@link java.lang.String} object
     * @param whereType a {@link cool.scx.enumeration.WhereType} object
     */
    public Where(String fieldName, WhereType whereType) {
        this.entityClass = null;
        add(fieldName, whereType);
    }

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
            var whereBody = new WhereBody(fieldName, whereType, value1, value2);
            if (checkWhereBody(whereBody)) {
                whereBodyList.add(whereBody);
            }
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
            var whereBody = new WhereBody(fieldName, whereType, value1, null);
            if (checkWhereBody(whereBody)) {
                whereBodyList.add(whereBody);
            }
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
            var whereBody = new WhereBody(fieldName, whereType, null, null);
            if (checkWhereBody(whereBody)) {
                whereBodyList.add(whereBody);
            }
            return this;
        }
        throw new RuntimeException(" WhereType 类型 : " + whereType + " , 参数数量必须为 " + whereType.paramSize());
    }

    /**
     * 设置 whereSql 适用于 复杂查询的自定义 where 子句<br>
     * 在最终 sql 中会拼接到 where 子句的最后<br>
     * 注意 :  除特殊语法外不需要手动在头部添加 AND
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
     *
     * @return a boolean
     */
    public boolean isEmpty() {
        return whereBodyList.size() == 0 && whereSQL == null;
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
     * like : 根据 SQL 表达式进行判断
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     SQL 表达式
     * @return this 方便链式调用
     */
    public Where likeRegex(String fieldName, String value) {
        return add(fieldName, WhereType.LIKE_REGEX, value);
    }

    /**
     * not like : 根据 SQL 表达式进行判断
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
     * not like : 默认会在首尾添加 %
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
    public Where jsonContains(String fieldName, Object value) {
        return add(fieldName, WhereType.JSON_CONTAINS, value);
    }

    /**
     * 在其中
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where in(String fieldName, Object value) {
        return add(fieldName, WhereType.IN, value);
    }

    /**
     * 不在其中
     *
     * @param fieldName 字段名称 (注意 : 不是数据库名称)
     * @param value     比较值
     * @return this 方便链式调用
     */
    public Where notIn(String fieldName, Object value) {
        return add(fieldName, WhereType.NOT_IN, value);
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

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WhereBody whereBody = (WhereBody) o;
            return Objects.equals(fieldName, whereBody.fieldName) && whereType == whereBody.whereType && Objects.equals(value1, whereBody.value1) && Objects.equals(value2, whereBody.value2);
        }

        @Override
        public int hashCode() {
            return Objects.hash(fieldName, whereType, value1, value2);
        }

    }

    /**
     * 检查 WhereBody 当 entityClass 存在时会先校验 fieldName 是否存在于 实体类中的字段<br>
     * <p>
     * 当 entityClass 不存在时则不会校验
     *
     * @return 检查的结果 只有为 true 时才会向列表中添加
     */
    private boolean checkWhereBody(WhereBody whereBody) {
        //先检查 orderByColumn 是不是存在于类中的
        if (entityClass != null) {
            try {
                entityClass.getField(whereBody.fieldName);
            } catch (NoSuchFieldException e) {
                Ansi.OUT.brightRed(whereBody.fieldName + " 不存在于 " + entityClass + " 的 field 内 , 请检查 Where 字段是否正确 或使用 Where 的无参构造创建对象 以忽略对字段的校验!!!").ln();
                return false;
            }
        }
        return true;
    }

}
