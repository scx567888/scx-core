package cool.scx.sql;

import cool.scx.bo.GroupBy;
import cool.scx.bo.OrderBy;
import cool.scx.bo.Pagination;
import cool.scx.bo.Where;
import cool.scx.enumeration.SQLBuilderType;
import cool.scx.util.CaseUtils;
import cool.scx.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * sql 语句构造器
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class SQLBuilder {

    /**
     * 当前sql 的类型 有 insert delete update select
     */
    private final SQLBuilderType _sqlBuilderType;

    /**
     * 表名
     */
    private final String _tableName;

    /**
     * 所有查询列 类似 user_name AS userName <br>
     * 注意 : 只有 _sqlBuilderType 为 select 时生效
     */
    private String[] _selectColumns;


    /**
     * 插入数据时的列名 <br>
     * 注意 : 只有 _sqlBuilderType 为 insert 时生效
     */
    private String[] _insertColumns;

    /**
     * 更新的 列名 <br>
     * 注意 : 只有 _sqlBuilderType 为 update 时生效
     */
    private String[] _updateColumns;

    /**
     * 所有 where 条件字符串 由 where 生成  类似 [ id = :id , age >= :age ]
     */
    private String[] _whereColumns;

    /**
     * whereSQL 由 where 生成
     */
    private String _whereSQL;

    /**
     * 所有列名
     */
    private GroupBy _groupBy;

    /**
     * 所有列名
     */
    private OrderBy _orderBy;

    /**
     * 起始分页(此值需要进行计算)
     */
    private Pagination _pagination;

    /**
     * 根据 where 条件对象生成的 map 防止 sql 注入
     */
    private final Map<String, Object> _whereParamMap = new HashMap<>();

    /**
     * 所有select sql的列名，有带下划线的将其转为aa_bb AS aaBb
     */
    private String[][] _values;


    private SQLBuilder(SQLBuilderType sqlBuilderType, String tableName) {
        _sqlBuilderType = sqlBuilderType;
        _tableName = tableName;
    }

    /**
     * 获取插入语句构造器
     *
     * @param _tableName 表名
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public static SQLBuilder Insert(String _tableName) {
        return new SQLBuilder(SQLBuilderType.INSERT, _tableName);
    }

    /**
     * 获取更新语句构造器
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public static SQLBuilder Update(String _tableName) {
        return new SQLBuilder(SQLBuilderType.UPDATE, _tableName);
    }

    /**
     * 获取查询语句构造器
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public static SQLBuilder Select(String _tableName) {
        return new SQLBuilder(SQLBuilderType.SELECT, _tableName);
    }

    /**
     * 获取删除语句构造器
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public static SQLBuilder Delete(String _tableName) {
        return new SQLBuilder(SQLBuilderType.DELETE, _tableName);
    }

    /**
     * 设置更新列
     *
     * @param fields an array of {@link java.lang.reflect.Field} objects.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public SQLBuilder UpdateColumns(Field[] fields) {
        _updateColumns = Stream.of(fields).map(field -> CaseUtils.toSnake(field.getName()) + " = :" + field.getName()).toArray(String[]::new);
        return this;
    }

    /**
     * 设置列 一般用于插入
     *
     * @param fields an array of {@link java.lang.reflect.Field} objects.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public SQLBuilder InsertColumns(Field[] fields) {
        _insertColumns = Stream.of(fields).map(o -> CaseUtils.toSnake(o.getName())).toArray(String[]::new);
        return this;
    }

    /**
     * 设置值 (非批量插入)
     *
     * @param fields an array of {@link java.lang.reflect.Field} objects.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public SQLBuilder Values(Field[] fields) {
        _values = new String[1][fields.length];
        _values[0] = Stream.of(fields).map(o -> ":" + o.getName()).toArray(String[]::new);
        return this;
    }

    /**
     * 设置值 (批量插入)
     *
     * @param values an array of {@link java.lang.String} objects.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public SQLBuilder Values(String[][] values) {
        _values = values;
        return this;
    }

    /**
     * 设置 where 语句 <br>
     * 内部会自行处理
     *
     * @param where an array of {@link java.lang.String} objects.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public SQLBuilder Where(Where where) {
        //如果 where 为空 不做处理
        if (where == null || where.isEmpty()) {
            return this;
        }
        var tempWhereColumnsList = new ArrayList<String>();
        this._whereSQL = where.whereSQL;
        for (var whereBody : where.whereBodyList) {
            var columnName = CaseUtils.toSnake(whereBody.fieldName);
            switch (whereBody.whereType) {
                case IS_NULL: {
                    var str = columnName + " IS NULL";
                    tempWhereColumnsList.add(str);
                    break;
                }
                case IS_NOT_NULL: {
                    var str = columnName + " IS NOT NULL";
                    tempWhereColumnsList.add(str);
                    break;
                }
                case EQUAL: {
                    var placeholder = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " = :" + placeholder;
                    _whereParamMap.put(placeholder, whereBody.value1);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case NOT_EQUAL: {
                    var placeholder = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " <> :" + placeholder;
                    _whereParamMap.put(placeholder, whereBody.value1);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case LESS_THAN: {
                    var placeholder = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " < :" + placeholder;
                    _whereParamMap.put(placeholder, whereBody.value1);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case LESS_THAN_OR_EQUAL: {
                    var placeholder = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " <= :" + placeholder;
                    _whereParamMap.put(placeholder, whereBody.value1);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case GREATER_THAN: {
                    var placeholder = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " > :" + placeholder;
                    _whereParamMap.put(placeholder, whereBody.value1);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case GREATER_THAN_OR_EQUAL: {
                    var placeholder = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " >= :" + placeholder;
                    _whereParamMap.put(placeholder, whereBody.value1);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case CONTAIN: {
                    //todo 可能有 json 的情况
//                        var placeholder = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
//                        var str = columnName + " >= :" + placeholder;
//                        _whereParamMap.put(placeholder, whereBody.value1);
//                        tempWhereColumnsList.add(str);
                    break;
                }
                case LIKE: {
                    var placeholder = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " LIKE CONCAT('%',:" + placeholder + ",'%')";
                    _whereParamMap.put(placeholder, whereBody.value1);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case NOT_LIKE: {
                    var placeholder = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " NOT LIKE CONCAT('%',:" + placeholder + ",'%')";
                    _whereParamMap.put(placeholder, whereBody.value1);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case LIKE_REGEX: {
                    var str = columnName + " LIKE " + whereBody.value1;
                    tempWhereColumnsList.add(str);
                    break;
                }
                case NOT_LIKE_REGEX: {
                    var str = columnName + " NOT LIKE " + whereBody.value1;
                    tempWhereColumnsList.add(str);
                    break;
                }
                case IN: {
                    //todo 需要拼接
//                        whereColumnAndPlaceholder[0] = columnName + " IN " + whereBody.value1;
//                        return whereColumnAndPlaceholder;
                    System.out.println("in");
                    break;
                }
                case NOT_IN: {
                    //todo 需要拼接 并处理格式
//                        whereColumnAndPlaceholder[0] = columnName + " NOT IN " + whereBody.value1;
//                        return whereColumnAndPlaceholder;
                    System.out.println("out");
                    break;
                }
                case NOT: {
                    //todo 需要拼接 并处理格式
//                        whereColumnAndPlaceholder[0] = columnName + " NOT " + whereBody.value1;
//                        return whereColumnAndPlaceholder;
                    System.out.println("not");
                    break;
                }
                case BETWEEN: {
                    var placeholder1 = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var placeholder2 = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " BETWEEN :" + placeholder1 + " AND :" + placeholder2;
                    _whereParamMap.put(placeholder1, whereBody.value1);
                    _whereParamMap.put(placeholder2, whereBody.value2);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case NOT_BETWEEN: {
                    var placeholder1 = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var placeholder2 = whereBody.fieldName + "_" + StringUtils.getRandomCode(6, true);
                    var str = columnName + " NOT BETWEEN :" + placeholder1 + " AND :" + placeholder2;
                    _whereParamMap.put(placeholder1, whereBody.value1);
                    _whereParamMap.put(placeholder2, whereBody.value2);
                    tempWhereColumnsList.add(str);
                    break;
                }
                case EXISTS: {
                    //todo 待处理
                    System.out.println("EXISTS");
                    break;
                }
                case NOT_EXISTS: {
                    //todo 待处理
                    System.out.println("NOT_EXISTS");
                    break;
                }

            }
        }
        _whereColumns = tempWhereColumnsList.toArray(new String[0]);
        return this;
    }

    /**
     * 设置 GroupBy 语句
     *
     * @param groupBy a {@link java.util.Set} object.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public SQLBuilder GroupBy(GroupBy groupBy) {
        this._groupBy = groupBy;
        return this;
    }

    /**
     * 设置 OrderBy 语句
     *
     * @param orderBy a {@link java.util.Map} object.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public SQLBuilder OrderBy(OrderBy orderBy) {
        this._orderBy = orderBy;
        return this;
    }

    /**
     * 设置分页数据
     *
     * @param pagination 分页对象
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public SQLBuilder Pagination(Pagination pagination) {
        this._pagination = pagination;
        return this;
    }

    /**
     * 设置 查询列
     *
     * @param selectColumns an array of {@link java.lang.String} objects.
     * @return a {@link cool.scx.sql.SQLBuilder} object.
     */
    public SQLBuilder SelectColumns(String[] selectColumns) {
        _selectColumns = selectColumns;
        return this;
    }

    /**
     * 获取 sql
     *
     * @return a {@link java.lang.String} object.
     */
    public String GetSQL() {
        switch (_sqlBuilderType) {
            case INSERT:
                return GetInsertSQL();
            case UPDATE:
                return GetUpdateSQL();
            case DELETE:
                return GetDeleteSQL();
            case SELECT:
                return GetSelectSQL();
            default:
                return "";
        }
    }

    public Map<String, Object> GetWhereParamMap() {
        return _whereParamMap;
    }

    private String GetInsertSQL() {
        if (_values.length == 1) {
            return " INSERT INTO " + _tableName + " ( " + String.join(",", _insertColumns) + " ) VALUES ( " + String.join(",", _values[0]) + " ) ";
        } else {
            var valuesStr = Arrays.stream(_values).map(v -> Arrays.stream(v).collect(Collectors.joining(",", "(", ")"))).collect(Collectors.joining(",", "", ""));
            return " INSERT INTO " + _tableName + " ( " + String.join(",", _insertColumns) + " ) VALUES " + valuesStr;
        }
    }

    private String GetUpdateSQL() {
        return " UPDATE " + _tableName + " SET " + String.join(", ", _updateColumns) + " " + getWhereSql();
    }

    private String GetDeleteSQL() {
        return " DELETE FROM " + _tableName + getWhereSql();
    }

    private String GetSelectSQL() {
        var groupBySql = "";
        if (_groupBy != null && _groupBy.groupByList.size() != 0) {
            groupBySql = " GROUP BY " + String.join(",", _groupBy.groupByList);
        }

        var orderBySql = "";
        if (_orderBy != null && _orderBy.orderByList.size() != 0) {
            orderBySql = " ORDER BY " + _orderBy.orderByList.entrySet().stream().map((entry) -> entry.getKey() + " " + entry.getValue().toString()).collect(Collectors.joining(",", "", ""));
        }

        var limitSql = "";
        if (_pagination != null && _pagination.canUse()) {
            limitSql = " LIMIT " + ((_pagination.page() - 1) * _pagination.limit() + "," + _pagination.limit());
        }

        return " SELECT " + String.join(", ", _selectColumns) + " FROM " + _tableName + getWhereSql() + groupBySql + orderBySql + limitSql;
    }


    private String getWhereSql() {
        var whereSql = "";
        //没有 where 查询条件 直接用 whereSQL
        if (_whereColumns == null || _whereColumns.length == 0) {
            if (!StringUtils.isEmpty(_whereSQL)) {
                whereSql = " WHERE " + _whereSQL;
            }
        } else {
            //拼接一下
            if (StringUtils.isEmpty(_whereSQL)) {
                whereSql = " WHERE " + String.join(" AND ", _whereColumns);
            } else {
                whereSql = " WHERE " + String.join(" AND ", _whereColumns) + " AND " + _whereSQL;
            }
        }
        return whereSql;
    }

}
