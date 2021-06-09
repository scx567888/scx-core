package cool.scx.sql;

import cool.scx.enumeration.SQLType;
import cool.scx.enumeration.SortType;
import cool.scx.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 简易 sql 语句构造器
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class SQLBuilder {

    /**
     * 当前sql 的类型 有 insert delete update select
     */
    private final SQLType _sqlType;

    /**
     * 表名
     */
    private String _tableName;

    /**
     * 所有查询列 类似 user_name AS userName
     */
    private String[] _selectColumns;

    /**
     * 所有where 条件 类似 id = 1
     */
    private String[] _whereColumns;

    /**
     * 所有列名
     */
    private Set<String> _groupBySet = new HashSet<>();

    /**
     * 所有列名
     */
    private Map<String, SortType> _orderByMap = new HashMap<>();

    /**
     * 起始分页(此值需要进行计算)
     */
    private Integer _page;

    /**
     * 分页条数
     */
    private Integer _limit;

    /**
     * 自定义的查询语句
     */
    private String _whereSql;

    /**
     * 所有列名
     */
    private String[] _columns;

    /**
     * 更新的 列名
     */
    private String[] _updateColumns;

    /**
     * 所有select sql的列名，有带下划线的将其转为aa_bb AS aaBb
     */
    private String[][] _values;

    private SQLBuilder(SQLType sqlType) {
        _sqlType = sqlType;
    }

    /**
     * 获取插入语句构造器
     *
     * @param _tableName 表名
     * @return a {@link SQLBuilder} object.
     */
    public static SQLBuilder Insert(String _tableName) {
        return new SQLBuilder(SQLType.INSERT).Table(_tableName);
    }

    /**
     * 获取更新语句构造器
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link SQLBuilder} object.
     */
    public static SQLBuilder Update(String _tableName) {
        return new SQLBuilder(SQLType.UPDATE).Table(_tableName);
    }

    /**
     * 获取查询语句构造器
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link SQLBuilder} object.
     */
    public static SQLBuilder Select(String _tableName) {
        return new SQLBuilder(SQLType.SELECT).Table(_tableName);
    }

    /**
     * 获取删除语句构造器
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link SQLBuilder} object.
     */
    public static SQLBuilder Delete(String _tableName) {
        return new SQLBuilder(SQLType.DELETE).Table(_tableName);
    }

    /**
     * 设置表名
     *
     * @param tableName a {@link java.lang.String} object.
     * @return a {@link SQLBuilder} object.
     */
    private SQLBuilder Table(String tableName) {
        _tableName = tableName;
        return this;
    }

    /**
     * 设置更新列
     *
     * @param fields an array of {@link java.lang.reflect.Field} objects.
     * @return a {@link SQLBuilder} object.
     */
    public SQLBuilder UpdateColumns(Field[] fields) {
        _updateColumns = Stream.of(fields).map(field -> StringUtils.camelToUnderscore(field.getName()) + " = :" + field.getName()).toArray(String[]::new);
        return this;
    }

    /**
     * 设置列 一般用于插入
     *
     * @param fields an array of {@link java.lang.reflect.Field} objects.
     * @return a {@link SQLBuilder} object.
     */
    public SQLBuilder Columns(Field[] fields) {
        _columns = Stream.of(fields).map(o -> StringUtils.camelToUnderscore(o.getName())).toArray(String[]::new);
        return this;
    }

    /**
     * 设置值 (非批量插入)
     *
     * @param fields an array of {@link java.lang.reflect.Field} objects.
     * @return a {@link SQLBuilder} object.
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
     * @return a {@link SQLBuilder} object.
     */
    public SQLBuilder Values(String[][] values) {
        _values = values;
        return this;
    }

    /**
     * 设置 where 语句
     *
     * @param whereColumns an array of {@link java.lang.String} objects.
     * @return a {@link SQLBuilder} object.
     */
    public SQLBuilder Where(String[] whereColumns) {
        _whereColumns = whereColumns;
        return this;
    }

    /**
     * 设置 GroupBy 语句
     *
     * @param groupBy a {@link java.util.Set} object.
     * @return a {@link SQLBuilder} object.
     */
    public SQLBuilder GroupBy(Set<String> groupBy) {
        _groupBySet = groupBy;
        return this;
    }

    /**
     * 设置 OrderBy 语句
     *
     * @param orderBys a {@link java.util.Map} object.
     * @return a {@link SQLBuilder} object.
     */
    public SQLBuilder OrderBy(Map<String, SortType> orderBys) {
        _orderByMap = orderBys;
        return this;
    }

    /**
     * 设置分页数据
     *
     * @param page  a {@link java.lang.Integer} object.
     * @param limit a {@link java.lang.Integer} object.
     * @return a {@link SQLBuilder} object.
     */
    public SQLBuilder Pagination(Integer page, Integer limit) {
        _page = page;
        _limit = limit;
        return this;
    }

    /**
     * 设置 WhereSql (独立于 Where)
     *
     * @param whereSql a {@link java.lang.String} object.
     * @return a {@link SQLBuilder} object.
     */
    public SQLBuilder WhereSql(String whereSql) {
        _whereSql = whereSql;
        return this;
    }

    private String getWhereSql() {
        var whereSql = "";
        //没有 实体类查询条件 直接用 whereSql
        if (_whereColumns == null || _whereColumns.length == 0) {
            if (!StringUtils.isEmpty(_whereSql)) {
                whereSql = " WHERE " + _whereSql;
            }
        } else {
            //拼接一下
            if (StringUtils.isEmpty(_whereSql)) {
                whereSql = " WHERE " + String.join(" AND ", _whereColumns);
            } else {
                whereSql = " WHERE " + String.join(" AND ", _whereColumns) + " AND " + _whereSql;
            }
        }
        return whereSql;
    }

    /**
     * 设置 查询列
     *
     * @param selectColumns an array of {@link java.lang.String} objects.
     * @return a {@link SQLBuilder} object.
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
        switch (_sqlType) {
            case INSERT:
                if (_values.length == 1) {
                    return " INSERT INTO " + _tableName + " ( " + String.join(",", _columns) + " ) VALUES ( " + String.join(",", _values[0]) + " ) ";
                } else {
                    var valuesStr = Arrays.stream(_values).map(v -> Arrays.stream(v).collect(Collectors.joining(",", "(", ")"))).collect(Collectors.joining(",", "", ""));
                    return " INSERT INTO " + _tableName + " ( " + String.join(",", _columns) + " ) VALUES " + valuesStr;
                }
            case UPDATE:
                return " UPDATE " + _tableName + " SET " + String.join(", ", _updateColumns) + " " + getWhereSql();
            case DELETE:
                return " DELETE FROM " + _tableName + getWhereSql();
            case SELECT: {
                var groupBySql = "";
                if (_groupBySet.size() != 0) {
                    groupBySql = " GROUP BY " + String.join(",", _groupBySet);
                }

                var orderBySql = "";
                if (_orderByMap.size() != 0) {
                    orderBySql = " ORDER BY " + _orderByMap.entrySet().stream().map((entry) -> entry.getKey() + " " + entry.getValue().toString()).collect(Collectors.joining(",", "", ""));
                }

                var limitSql = "";
                if (!StringUtils.isEmpty(_limit) && !StringUtils.isEmpty(_page) && _limit > 0 && _page > 0) {
                    limitSql = " LIMIT " + ((_page - 1) * _limit + "," + _limit);
                }

                return " SELECT " + String.join(", ", _selectColumns) + " FROM " + _tableName + getWhereSql() + groupBySql + orderBySql + limitSql;
            }
            default:
                return "";
        }
    }

}
