package cool.scx.dao;

import cool.scx.enumeration.SQLType;
import cool.scx.enumeration.SortType;
import cool.scx.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>SQLBuilder class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class SQLBuilder {
    private final SQLType _sqlType; //当前sql 的类型 有 insert delete update select
    private String _tableName; //表名
    private String[] _selectColumns; //所有查询列 类似 user_name AS userName
    private String[] _whereColumns; //所有where 条件 类似 id = 1
    private Set<String> _groupBySet = new HashSet<>(); //所有列名
    private Map<String, SortType> _orderByMap = new HashMap<>(); //所有列名
    private Integer _page; //起始分页(此值需要进行计算)
    private Integer _limit; //分页条数
    private String _whereSql; //所有列名
    private String[] _columns; //所有列名
    private String[] _updateColumns; //所有列名
    private String[][] _values; //所有select sql的列名，有带下划线的将其转为aa_bb AS aaBb

    private SQLBuilder() {
        _sqlType = SQLType.SELECT;
    }

    private SQLBuilder(SQLType sqlType) {
        _sqlType = sqlType;
    }

    /**
     * <p>Insert.</p>
     *
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public static SQLBuilder Insert() {
        return new SQLBuilder(SQLType.INSERT);
    }

    /**
     * <p>Update.</p>
     *
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public static SQLBuilder Update() {
        return new SQLBuilder(SQLType.UPDATE);
    }

    /**
     * <p>Select.</p>
     *
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public static SQLBuilder Select() {
        return new SQLBuilder(SQLType.SELECT);
    }

    /**
     * <p>Delete.</p>
     *
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public static SQLBuilder Delete() {
        return new SQLBuilder(SQLType.DELETE);
    }

    /**
     * <p>Insert.</p>
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public static SQLBuilder Insert(String _tableName) {
        return new SQLBuilder(SQLType.INSERT).Table(_tableName);
    }

    /**
     * <p>Update.</p>
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public static SQLBuilder Update(String _tableName) {
        return new SQLBuilder(SQLType.UPDATE).Table(_tableName);
    }

    /**
     * <p>Select.</p>
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public static SQLBuilder Select(String _tableName) {
        return new SQLBuilder(SQLType.SELECT).Table(_tableName);
    }

    /**
     * <p>Delete.</p>
     *
     * @param _tableName a {@link java.lang.String} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public static SQLBuilder Delete(String _tableName) {
        return new SQLBuilder(SQLType.DELETE).Table(_tableName);
    }

    /**
     * <p>Table.</p>
     *
     * @param tableName a {@link java.lang.String} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder Table(String tableName) {
        _tableName = tableName;
        return this;
    }

    /**
     * <p>UpdateColumns.</p>
     *
     * @param fields an array of {@link java.lang.reflect.Field} objects.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder UpdateColumns(Field[] fields) {
        _updateColumns = Stream.of(fields).map(field -> StringUtils.camel2Underscore(field.getName()) + " = :" + field.getName()).toArray(String[]::new);
        return this;
    }

    /**
     * <p>Columns.</p>
     *
     * @param fields an array of {@link java.lang.reflect.Field} objects.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder Columns(Field[] fields) {
        _columns = Stream.of(fields).map(o -> StringUtils.camel2Underscore(o.getName())).toArray(String[]::new);
        return this;
    }

    /**
     * <p>Columns.</p>
     *
     * @param columns an array of {@link java.lang.String} objects.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder Columns(String[] columns) {
        _columns = columns;
        return this;
    }

    /**
     * <p>Values.</p>
     *
     * @param fields an array of {@link java.lang.reflect.Field} objects.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder Values(Field[] fields) {
        _values = new String[1][fields.length];
        _values[0] = Stream.of(fields).map(o -> ":" + o.getName()).toArray(String[]::new);
        return this;
    }

    /**
     * <p>Values.</p>
     *
     * @param values an array of {@link java.lang.String} objects.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder Values(String[][] values) {
        _values = values;
        return this;
    }

    /**
     * <p>Where.</p>
     *
     * @param whereColumns an array of {@link java.lang.String} objects.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder Where(String[] whereColumns) {
        _whereColumns = whereColumns;
        return this;
    }

    /**
     * <p>GroupBy.</p>
     *
     * @param groupBy a {@link java.util.Set} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder GroupBy(Set<String> groupBy) {
        _groupBySet = groupBy;
        return this;
    }

    /**
     * <p>OrderBy.</p>
     *
     * @param orderBys a {@link java.util.Map} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder OrderBy(Map<String, SortType> orderBys) {
        _orderByMap = orderBys;
        return this;
    }

    /**
     * <p>Pagination.</p>
     *
     * @param page  a {@link java.lang.Integer} object.
     * @param limit a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder Pagination(Integer page, Integer limit) {
        _page = page;
        _limit = limit;
        return this;
    }

    /**
     * <p>Pagination.</p>
     *
     * @param limit a {@link java.lang.Integer} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder Pagination(Integer limit) {
        _page = 1;
        _limit = limit;
        return this;
    }

    /**
     * <p>WhereSql.</p>
     *
     * @param whereSql a {@link java.lang.String} object.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder WhereSql(String whereSql) {
        _whereSql = whereSql;
        return this;
    }

    /**
     * <p>GetSQL.</p>
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
     * <p>SelectColumns.</p>
     *
     * @param selectColumns an array of {@link java.lang.String} objects.
     * @return a {@link cool.scx.dao.SQLBuilder} object.
     */
    public SQLBuilder SelectColumns(String[] selectColumns) {
        _selectColumns = selectColumns;
        return this;
    }


}
