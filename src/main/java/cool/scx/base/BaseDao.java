package cool.scx.base;

import cool.scx.annotation.Column;
import cool.scx.annotation.NoColumn;
import cool.scx.annotation.ScxModel;
import cool.scx.boot.ScxConfig;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class BaseDao<Entity extends BaseModel> {

    private static final Map<String, TableInfo> tableCache = new ConcurrentHashMap<>(256);
    private final TableInfo table;
    private final Class<Entity> entityClass;

    public BaseDao(Class<Entity> clazz) {
        table = getTableInfo(clazz);
        entityClass = clazz;
    }

    public static void fixTable(Class<?> clazz) {
        var table = getTableInfo(clazz);
        try (var connection = SQLRunner.getConnection()) {
            //第一步 先检查表是否存在 如果不存在 创建表
            var dbMetaData = Objects.requireNonNull(connection).getMetaData();
            var types = new String[]{"TABLE"};
            var tabs = dbMetaData.getTables(null, null, table.tableName, types);
            if (tabs.next()) {
                var tableName = tabs.getString("TABLE_NAME");
                var resultSet = dbMetaData.getColumns(null, null, tableName, null);
                var stringArrayList = new ArrayList<>();
                while (resultSet.next()) {
                    stringArrayList.add(resultSet.getString("COLUMN_NAME"));
                }
                var nonExistentFields = Stream.of(table.allFields).filter(field -> !stringArrayList.contains(StringUtils.camel2Underscore(field.getName()))).collect(Collectors.toList());
                if (nonExistentFields.size() != 0) {
                    var columns = nonExistentFields.stream().map(field -> StringUtils.camel2Underscore(field.getName())).collect(Collectors.joining(" , ", " [ ", " ] "));
                    StringUtils.println("未找到表 " + table.tableName + " 中的 " + columns + " 字段 --> 正在自动建立 !!!", StringUtils.Color.BRIGHT_BLUE);
                    var alertSql = nonExistentFields.stream().map(field -> " ADD " + getSQLColumnByField(field)).collect(Collectors.joining(",", "", ""));
                    var s = "ALTER TABLE `" + table.tableName + "` " + alertSql + " ; ALTER TABLE `" + table.tableName + "` " + getOtherSQLByField(nonExistentFields.toArray(Field[]::new)).stream().map(str -> " ADD " + str).collect(Collectors.joining(",", "", "")) + ";";
                    SQLRunner.execute(s);
                }
            } else {
                StringUtils.println("未找到表 " + table.tableName + " --> 正在自动建立 !!!", StringUtils.Color.BRIGHT_MAGENTA);
                var createTableSql = "CREATE TABLE `" + table.tableName + "` ( " + Stream.of(table.allFields).map(field -> getSQLColumnByField(field) + ",").collect(Collectors.joining("", "", "")) + String.join(",", getOtherSQLByField(table.allFields)) + ") ;";
                SQLRunner.execute(createTableSql);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

    public static TableInfo getTableInfo(Class<?> clazz) {
        var tempTable = tableCache.get(clazz.getName());
        if (tempTable != null) {
            return tempTable;
        }
        var fields = getColumnFields(clazz);
        tempTable = new TableInfo(fields, getTableName(clazz), getSelectColumns(fields));
        tableCache.put(clazz.getName(), tempTable);
        return tempTable;
    }

    private static Field[] getColumnFields(Class<?> clazz) {
        return Stream.of(clazz.getFields())
                .filter(field -> !field.isAnnotationPresent(NoColumn.class) && (ScxConfig.realDelete || !"isDeleted".equals(field.getName())))
                .toArray(Field[]::new);
    }

    private static String getTableName(Class<?> clazz) {
        var scxModel = clazz.getAnnotation(ScxModel.class);
        if (scxModel != null && StringUtils.isNotEmpty(scxModel.tableName())) {
            return scxModel.tableName();
        }
        if (scxModel != null && StringUtils.isNotEmpty(scxModel.tablePrefix())) {
            return scxModel.tablePrefix() + "_" + StringUtils.camel2Underscore(clazz.getSimpleName());
        }
        return "scx_" + StringUtils.camel2Underscore(clazz.getSimpleName());
    }

    private static String[] getSelectColumns(Field[] fields) {
        return Stream.of(fields).map(field -> {
            var camel = StringUtils.camel2Underscore(field.getName());
            return camel.contains("_") ? camel + " AS " + field.getName() : camel;
        }).toArray(String[]::new);
    }

    private static String getSQlColumnTypeByClass(Class<?> clazz) {
        var TypeMapping = new HashMap<Class<?>, String>();
        TypeMapping.put(java.lang.Integer.class, "int");
        TypeMapping.put(java.lang.Long.class, "bigint");
        TypeMapping.put(java.lang.Double.class, "double");
        TypeMapping.put(java.lang.Boolean.class, "tinyint(1)");
        TypeMapping.put(java.time.LocalDateTime.class, "datetime");
        var type = TypeMapping.get(clazz);
        if (type == null) {
            return " varchar(128) ";
        }
        return type;
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
            type = "".equals(fieldColumn.type()) ? getSQlColumnTypeByClass(field.getType()) : fieldColumn.type();
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
            type = getSQlColumnTypeByClass(field.getType());
            notNull = " NULL ";
        }
        return columnName + type + notNull + autoIncrement + defaultValue + onUpdate;
    }

    public Long save(Entity entity) {
        var c = Stream.of(table.canInsertFields).filter(field -> ObjectUtils.getFieldValue(field, entity) != null).toArray(Field[]::new);
        var sql = SQLBuilder.Insert(table.tableName).Columns(c).Values(c).GetSQL();
        return SQLRunner.update(sql, ObjectUtils.beanToMap(entity)).generatedKeys.get(0);
    }

    public List<Long> saveList(List<Entity> entityList) {
        var splitSize = 5000;
        var size = entityList.size();
        if (size > splitSize) {
            StringUtils.println("批量插入数据量过大 , 达到" + size + "条 !!! 已按照" + splitSize + "条进行切分 !!!", StringUtils.Color.BRIGHT_RED);
            var generatedKeys = new ArrayList<Long>(splitSize);
            int number = size / splitSize;
            for (int i = 0; i < number; i++) {
                generatedKeys.addAll(saveList(entityList.subList(i * splitSize, (Math.min((i + 1) * splitSize, size)))));
            }
            return generatedKeys;
        }
        var values = new String[entityList.size()][table.canInsertFields.length];
        var map = new LinkedHashMap<String, Object>();

        for (int i = 0; i < entityList.size(); i++) {
            for (int j = 0; j < table.canInsertFields.length; j++) {
                values[i][j] = ":list" + i + "." + table.canInsertFields[j].getName();
            }
            //将 list 集合降级为 一维 map 结构 key 为  list{index}.{field} index 为索引 field 为字段名称
            map.putAll(ObjectUtils.beanToMapWithIndex(i, entityList.get(i)));
        }

        var sql = SQLBuilder.Insert().Table(table.tableName).Columns(table.canInsertFields).Values(values).GetSQL();

        return SQLRunner.update(sql, map).generatedKeys;
    }

    public List<Entity> list(Param<Entity> param, boolean ignoreLike) {
        var s = table.selectColumns;
        System.out.println();
        var sql = SQLBuilder.Select().SelectColumns(table.selectColumns).Table(table.tableName)
                .Where(getWhereColumns(param.queryObject, ignoreLike))
                .WhereSql(param.whereSql)
                .GroupBy(param.groupBy)
                .OrderBy(param.orderBy)
                .Pagination(param.getPage(), param.getLimit())
                .GetSQL();
        return SQLRunner.query(sql, ObjectUtils.beanToMap(param.queryObject), entityClass);
    }

    public List<Map<String, Object>> listMap(Param<Entity> param, boolean ignoreLike) {
        var sql = SQLBuilder.Select().SelectColumns(table.selectColumns).Table(table.tableName)
                .Where(getWhereColumns(param.queryObject, ignoreLike))
                .WhereSql(param.whereSql)
                .GroupBy(param.groupBy)
                .OrderBy(param.orderBy)
                .Pagination(param.getPage(), param.getLimit())
                .GetSQL();
        return SQLRunner.query(sql, ObjectUtils.beanToMap(param.queryObject));
    }

    public Integer count(Param<Entity> param, boolean ignoreLike) {
        var sql = SQLBuilder.Select(table.tableName).SelectColumns(new String[]{"COUNT(*)"})
                .Where(getWhereColumns(param.queryObject, ignoreLike))
                .WhereSql(param.whereSql)
                .GroupBy(param.groupBy)
                .GetSQL();
        return Integer.parseInt(SQLRunner.query(sql, ObjectUtils.beanToMap(param.queryObject)).get(0).get("COUNT(*)").toString());
    }

    public SQLRunner.UpdateResult update(Param<Entity> param, boolean includeNull) {
        var beanMap = ObjectUtils.beanToMap(param.queryObject);
        Long id = param.queryObject.id;
        var sql = SQLBuilder.Update(table.tableName);
        if (id != null) {
            var setColumns = Stream.of(table.canUpdateFields)
                    .filter(field -> (!includeNull && ObjectUtils.getFieldValue(field, param.queryObject) != null))
                    .toArray(Field[]::new);
            sql.UpdateColumns(setColumns).WhereSql(" id = :id ");
        } else if (!StringUtils.isEmpty(param.whereSql)) {
            var setColumns = Stream.of(table.canUpdateFields)
                    .filter(field -> (!includeNull && ObjectUtils.getFieldValue(field, param.queryObject) != null))
                    .toArray(Field[]::new);
            sql.UpdateColumns(setColumns).WhereSql(param.whereSql);
        } else {
            throw new RuntimeException("更新数据时必须指定 id 或 自定义的 where 语句 !!!");
        }
        return SQLRunner.update(sql.GetSQL(), beanMap);
    }

    public Integer delete(Param<Entity> param) {
        //将 对象转换为 map 方便处理
        var entityMap = ObjectUtils.beanToMap(param.queryObject);

        var sql = SQLBuilder.Delete().Where(getWhereColumns(param.queryObject, false))
                .WhereSql(param.whereSql).Table(table.tableName).GetSQL();
        return SQLRunner.update(sql, entityMap).affectedLength;
    }

    public List<Map<String, Object>> getFieldList(String fieldName) {
        if (Arrays.stream(table.allFields).filter(field -> field.getName().equals(fieldName)).count() == 1) {
            var sql = SQLBuilder.Select(table.tableName).SelectColumns(new String[]{StringUtils.camel2Underscore(fieldName) + " As value "})
                    .WhereSql(ScxConfig.realDelete ? "" : "WHERE is_deleted = FALSE").GroupBy(new HashSet<>() {{
                        add("value");
                    }}).GetSQL();
            return SQLRunner.query(sql, new HashMap<>());
        } else {
            return new ArrayList<>();
        }
    }

    private String[] getWhereColumns(Entity entity, boolean ignoreLike) {
        return Stream.of(table.allFields).filter(field -> ObjectUtils.getFieldValue(field, entity) != null)
                .map(field -> StringUtils.camel2Underscore(field.getName()) +
                        (((field.getAnnotation(Column.class) != null && field.getAnnotation(Column.class).useLike()) && ignoreLike) ? " LIKE  CONCAT('%',:" + field.getName() + ",'%')" : " = :" + field.getName()))
                .toArray(String[]::new);
    }

    private static class TableInfo {
        private final Field[] canUpdateFields;//实体类型不含@NoColunm 和@NoUpdate 注解的field

        private final Field[] canInsertFields;//实体类型不含@NoColunm 和@NoInsert 注解的field

        private final Field[] allFields;//实体类型不含@NoColunm 注解的field

        private final String tableName;//表名

        private final String[] selectColumns;//所有select sql的列名，有带下划线的将其转为aa_bb AS aaBb

        private TableInfo(Field[] _fields, String _tableName, String[] _selectColumns) {
            canInsertFields = Arrays.stream(_fields).filter(ta -> {
                var c = ta.getAnnotation(Column.class);
                if (c != null) {
                    return !c.noInsert();
                } else {
                    return true;
                }
            }).toArray(Field[]::new);
            canUpdateFields = Arrays.stream(_fields).filter(ta -> {
                var c = ta.getAnnotation(Column.class);
                if (c == null) {
                    return true;
                } else {
                    return !c.noUpdate();
                }
            }).toArray(Field[]::new);
            allFields = _fields;
            tableName = _tableName;
            selectColumns = _selectColumns;
        }
    }
}
