package cool.scx.sql;

import cool.scx.annotation.Column;
import cool.scx.bo.TableInfo;
import cool.scx.config.ScxConfig;
import cool.scx.enumeration.FixTableResult;
import cool.scx.util.Ansi;
import cool.scx.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 构建 SQL 的助手(常用方法) 类
 *
 * @author 司昌旭
 * @version 1.1.0
 */
public final class SQLHelper {

    /**
     * tableInfo 缓存为了提高构建表的速度
     */
    private static final Map<String, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>(256);

    /**
     * javaType 到 sqlType 的映射 用于创建建表语句
     */
    private final static HashMap<Class<?>, String> JAVA_TYPE_SQL_TYPE_MAP = initJavaTypeSQLTypeMap();

    /**
     * 数据库实例
     */
    private static final String databaseName = ScxConfig.dataSourceDatabase();

    /**
     * 根据 class 通过反射获取 对应的 TableInfo (表结构)
     *
     * @param clazz class
     * @return a tableInfo
     */
    public static TableInfo getTableInfo(Class<?> clazz) {
        var tempTable = TABLE_INFO_CACHE.get(clazz.getName());
        if (tempTable != null) {
            return tempTable;
        }
        tempTable = new TableInfo(clazz);
        TABLE_INFO_CACHE.put(clazz.getName(), tempTable);
        return tempTable;
    }

    /**
     * 根据类修复表
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return 是否修复
     */
    public static FixTableResult fixTable(Class<?> clazz) {
        var table = getTableInfo(clazz);
        try (var connection = SQLRunner.getConnection()) {
            //获取当前连接对象的 MetaData
            var dbMetaData = connection.getMetaData();
            //根据表名称获取表
            var nowTable = dbMetaData.getTables(databaseName, databaseName, table.tableName, new String[]{"TABLE"});
            //获取到表
            if (nowTable.next()) {
                var nowColumns = dbMetaData.getColumns(databaseName, databaseName, nowTable.getString("TABLE_NAME"), null);
                var stringArrayList = new ArrayList<>();
                while (nowColumns.next()) {
                    stringArrayList.add(nowColumns.getString("COLUMN_NAME"));
                }
                var nonExistentFields = Stream.of(table.allFields).filter(field ->
                        !stringArrayList.contains(StringUtils.camelToUnderscore(field.getName()))
                ).collect(Collectors.toList());

                if (nonExistentFields.size() != 0) {
                    var columns = nonExistentFields.stream().map(field -> StringUtils.camelToUnderscore(field.getName())).collect(Collectors.joining(" , ", " [ ", " ] "));
                    Ansi.OUT.brightBlue("未找到表 " + table.tableName + " 中的 " + columns + " 字段 --> 正在自动建立 !!!").ln();
                    var addSql = nonExistentFields.stream().map(field -> " ADD " + getSQLColumn(field)).collect(Collectors.joining(",", "", ""));
                    var alertSql = "ALTER TABLE `" + table.tableName + "` " + addSql;
                    var otherSQLByField = getOtherSQL(nonExistentFields.toArray(Field[]::new));
                    if (otherSQLByField.size() > 0) {
                        alertSql += otherSQLByField.stream().map(str -> " ADD " + str).collect(Collectors.joining(",", ",", ";"));
                    } else {
                        alertSql += ";";
                    }
                    SQLRunner.execute(alertSql, null);
                    return FixTableResult.FIX_SUCCESS;
                } else {
                    return FixTableResult.NO_NEED_TO_FIX;
                }
            } else {
                Ansi.OUT.brightMagenta("未找到表 " + table.tableName + " --> 正在自动建立 !!!").ln();
                var createTableSql = "CREATE TABLE `" + table.tableName + "` ( " + Stream.of(table.allFields).map(field -> getSQLColumn(field) + ",").collect(Collectors.joining("", "", "")) + String.join(",", getOtherSQL(table.allFields)) + ") ;";
                SQLRunner.execute(createTableSql, null);
                return FixTableResult.FIX_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return FixTableResult.FIX_FAIL;
        }
    }

    /**
     * 根据 field 构建 特殊的 SQLColumn
     * 如 是否为唯一键 是否添加索引 是否为主键等
     *
     * @param allFields allFields
     * @return 生成的语句片段
     */
    private static List<String> getOtherSQL(Field... allFields) {
        var list = new ArrayList<String>();
        for (Field field : allFields) {
            var column = field.getAnnotation(Column.class);
            if (column != null) {
                var columnName = StringUtils.camelToUnderscore(field.getName());
                if (column.primaryKey()) {
                    list.add("PRIMARY KEY (`" + columnName + "`)");
                }
                if (column.unique()) {
                    list.add("UNIQUE KEY `unique_" + columnName + "`(`" + columnName + "`)");
                }
                if (column.needIndex()) {
                    list.add("KEY `index_" + columnName + "`(`" + columnName + "`)");
                }
            }
        }
        return list;
    }

    /**
     * 根据 field 构建 基本的 SQLColumn
     *
     * @param field field
     * @return 生成的语句片段
     */
    private static String getSQLColumn(Field field) {
        var columnName = "`" + StringUtils.camelToUnderscore(field.getName()) + "` ";
        var type = "";
        var notNull = "";
        var autoIncrement = "";
        var defaultValue = "";
        var onUpdate = "";
        var fieldColumn = field.getAnnotation(Column.class);
        if (fieldColumn != null) {
            type = "".equals(fieldColumn.type()) ? getSQLType(field.getType()) : fieldColumn.type();
            notNull = fieldColumn.notNull() ? " NOT NULL" : " NULL";
            if (fieldColumn.autoIncrement()) {
                autoIncrement = " AUTO_INCREMENT ";
            }
            if (fieldColumn.primaryKey()) {
                notNull = " NOT NULL ";
            }
            if (!"".equals(fieldColumn.defaultValue())) {
                defaultValue = " DEFAULT " + fieldColumn.defaultValue();
            }
            if (!"".equals(fieldColumn.onUpdateValue())) {
                onUpdate += " ON UPDATE " + fieldColumn.defaultValue();
            }
        } else {
            type = getSQLType(field.getType());
            notNull = " NULL ";
        }
        return columnName + type + notNull + autoIncrement + defaultValue + onUpdate;
    }

    /**
     * 注意 : 此方法只适用于 创建 建表语句
     * 根据 class 获取对应的 SQLType 类型 如果没有则返回 JSON
     * 此处并没有直接返回 {@link com.mysql.cj.MysqlType} 的枚举类型是因为根据当前业务需要对 返回的类型进行一些特殊处理
     *
     * @param javaType 需要获取的类型
     * @return a {@link java.lang.String} object.
     */
    private static String getSQLType(Class<?> javaType) {
        String sqlType = JAVA_TYPE_SQL_TYPE_MAP.get(javaType);
        if (sqlType == null) {
            return "JSON";
        }
        return sqlType;
    }

    /**
     * 判断类型是否可以由 JDBC 进行 SQLType 到 JavaType 的直接转换
     * <p>
     * 例子 :
     * String 可以由 varchar 直接转换 true
     * Integer 可以由 int 直接转换 true
     * User 不可以由 json 直接转换 false
     *
     * @param javaType 需要判断的类型
     * @return 是否可以进行转换
     */
    public static boolean isSupportedType(Class<?> javaType) {
        return JAVA_TYPE_SQL_TYPE_MAP.get(javaType) != null;
    }

    private static HashMap<Class<?>, String> initJavaTypeSQLTypeMap() {
        var m = new HashMap<Class<?>, String>();
        //基本类型 (不包含 char )
        m.put(byte.class, "TINYINT");
        m.put(short.class, "SMALLINT");
        m.put(int.class, "INT");
        m.put(long.class, "BIGINT");
        m.put(float.class, "FLOAT");
        m.put(double.class, "DOUBLE");
        m.put(boolean.class, "TINYINT(1)");
        //以上基本类型对应的包装类型
        m.put(java.lang.Byte.class, "TINYINT");
        m.put(java.lang.Short.class, "SMALLINT");
        m.put(java.lang.Integer.class, "INT");
        m.put(java.lang.Long.class, "BIGINT");
        m.put(java.lang.Float.class, "FLOAT");
        m.put(java.lang.Double.class, "DOUBLE");
        m.put(java.lang.Boolean.class, "TINYINT(1)");
        //其他类型
        m.put(java.lang.String.class, "VARCHAR(128)");
        m.put(java.math.BigInteger.class, "BIGINT");
        m.put(java.math.BigDecimal.class, "DECIMAL");
        m.put(java.time.LocalDateTime.class, "DATETIME");
        //数组类型
        m.put(byte[].class, "LONGBLOB");
        m.put(java.lang.Byte[].class, "LONGBLOB");
        return m;
    }

}
