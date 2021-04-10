package cool.scx.sql;

import cool.scx.annotation.Column;
import cool.scx.bo.TableInfo;
import cool.scx.enumeration.FixTableResult;
import cool.scx.util.Ansi;
import cool.scx.util.StringUtils;

import java.lang.reflect.Field;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
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
    private static final Map<String, TableInfo> tableCache = new ConcurrentHashMap<>(256);

    /**
     * <p>getTableInfo.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link cool.scx.bo.TableInfo} object.
     */
    public static TableInfo getTableInfo(Class<?> clazz) {
        var tempTable = tableCache.get(clazz.getName());
        if (tempTable != null) {
            return tempTable;
        }
        tempTable = new TableInfo(clazz);
        tableCache.put(clazz.getName(), tempTable);
        return tempTable;
    }

    /**
     * <p>getSQlColumnTypeByClass.</p>
     *
     * @param clazz a {@link java.lang.Class} object.
     * @return a {@link java.lang.String} object.
     */
    public static String getSQlColumnTypeByClass(Class<?> clazz) {
        var TypeMapping = new HashMap<Class<?>, String>();
        TypeMapping.put(java.lang.Integer.class, "int");
        TypeMapping.put(java.lang.Long.class, "bigint");
        TypeMapping.put(java.lang.Double.class, "double");
        TypeMapping.put(java.lang.Boolean.class, "BIT(1)");
        TypeMapping.put(java.time.LocalDateTime.class, "DATETIME");
        var type = TypeMapping.get(clazz);
        if (type == null) {
            return "varchar(128)";
        }
        return type;
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

                while (nowColumns.next()) {
                    var dataMap = new HashMap<>();
                    ResultSetMetaData rsMeta = nowColumns.getMetaData();
                    int columnCount = rsMeta.getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        dataMap.put(rsMeta.getColumnLabel(i), nowColumns.getObject(i));
                    }
                    System.out.println();
                }

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

    /**
     * 检查字段是否符合要求
     *
     * @return
     */
    private static boolean checkColumn() {
        return false;
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
            type = "".equals(fieldColumn.type()) ? SQLHelper.getSQlColumnTypeByClass(field.getType()) : fieldColumn.type();
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
            type = SQLHelper.getSQlColumnTypeByClass(field.getType());
            notNull = " NULL ";
        }
        return columnName + type + notNull + autoIncrement + defaultValue + onUpdate;
    }

}
