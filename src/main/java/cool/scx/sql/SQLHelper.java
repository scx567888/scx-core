package cool.scx.sql;

import cool.scx.annotation.Column;
import cool.scx.bo.TableInfo;
import cool.scx.enumeration.FixTableResult;
import cool.scx.util.Ansi;
import cool.scx.util.StringUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>SQLHelper class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class SQLHelper {

    /**
     * tableInfo 缓存
     */
    private static final Map<String, TableInfo> TABLE_INFO_CACHE = new ConcurrentHashMap<>(256);


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
     * 根据 class 获取类型
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getColumnTypeByType(Class<?> clazz) {
        if (clazz == java.lang.String.class) {
            return "VARCHAR(128)";
        } else if (clazz == java.lang.Long.class) {
            return "BIGINT";
        } else if (clazz == java.lang.Double.class) {
            return "DOUBLE";
        } else if (clazz == java.lang.Byte.class) {
            return "TINYINT";
        } else if (clazz == java.lang.Integer.class) {
            return "INTEGER";
        } else if (clazz == java.lang.Boolean.class) {
            return "TINYINT(1)";
        } else if (clazz == java.time.LocalDateTime.class) {
            return "DATETIME";
        } else {
            return "VARCHAR(128)";
        }
    }

    /**
     * <p>fixTable.</p>
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
            var nowTable = dbMetaData.getTables(null, null, table.tableName, new String[]{"TABLE"});
            //获取到表
            if (nowTable.next()) {
                var nowColumns = dbMetaData.getColumns(null, null, nowTable.getString("TABLE_NAME"), null);
                var stringArrayList = new ArrayList<>();
                while (nowColumns.next()) {
                    stringArrayList.add(nowColumns.getString("COLUMN_NAME"));
                }
                var nonExistentFields = Stream.of(table.allFields).filter(field ->
                        !stringArrayList.contains(StringUtils.camel2Underscore(field.getName()))
                ).collect(Collectors.toList());

                if (nonExistentFields.size() != 0) {
                    var columns = nonExistentFields.stream().map(field -> StringUtils.camel2Underscore(field.getName())).collect(Collectors.joining(" , ", " [ ", " ] "));
                    Ansi.OUT.brightBlue("未找到表 " + table.tableName + " 中的 " + columns + " 字段 --> 正在自动建立 !!!").ln();
                    var addSql = nonExistentFields.stream().map(field -> " ADD " + getSQLColumnByField(field)).collect(Collectors.joining(",", "", ""));
                    var alertSql = "ALTER TABLE `" + table.tableName + "` " + addSql;
                    var otherSQLByField = getOtherSQLByField(nonExistentFields.toArray(Field[]::new));
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
                var createTableSql = "CREATE TABLE `" + table.tableName + "` ( " + Stream.of(table.allFields).map(field -> getSQLColumnByField(field) + ",").collect(Collectors.joining("", "", "")) + String.join(",", getOtherSQLByField(table.allFields)) + ") ;";
                SQLRunner.execute(createTableSql, null);
                return FixTableResult.FIX_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return FixTableResult.FIX_FAIL;
        }
    }

    private static List<String> getOtherSQLByField(Field... allFields) {
        var list = new ArrayList<String>();
        for (Field field : allFields) {
            var column = field.getAnnotation(Column.class);
            if (column != null) {
                var columnName = StringUtils.camel2Underscore(field.getName());
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


    private static String getSQLColumnByField(Field field) {
        var columnName = "`" + StringUtils.camel2Underscore(field.getName()) + "` ";
        var type = "";
        var notNull = "";
        var autoIncrement = "";
        var defaultValue = "";
        var onUpdate = "";
        var fieldColumn = field.getAnnotation(Column.class);
        if (fieldColumn != null) {
            type = "".equals(fieldColumn.type()) ? SQLHelper.getColumnTypeByType(field.getType()) : fieldColumn.type();
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
            type = SQLHelper.getColumnTypeByType(field.getType());
            notNull = " NULL ";
        }
        return columnName + type + notNull + autoIncrement + defaultValue + onUpdate;
    }

}
