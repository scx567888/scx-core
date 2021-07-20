package cool.scx.sql;

import com.mysql.cj.jdbc.ClientPreparedStatement;
import cool.scx.ScxDao;
import cool.scx.bo.UpdateResult;
import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.ObjectUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * SQLRunner 执行 sql 语句
 *
 * @author scx567888
 * @version 1.0.10
 */
public final class SQLRunner {


    /**
     * 参数过滤正则表达式
     */
    private static final Pattern pattern = Pattern.compile("(:([\\w.]+))");


    /**
     * 获取 JDBC 连接
     *
     * @return jdbc 连接
     * @throws java.lang.Exception if any.
     */
    public static Connection getConnection() throws Exception {
        return ScxDao.dataSource().getConnection();
    }

    /**
     * 查询 返回值为 map集合
     *
     * @param sql   a {@link java.lang.String} object.
     * @param param a {@link java.util.Map} object.
     * @return a map 集合
     */
    public static List<Map<String, Object>> query(String sql, Map<String, Object> param) {
        var list = new ArrayList<Map<String, Object>>();
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, param); var resultSet = preparedStatement.executeQuery()) {
            var rsm = resultSet.getMetaData();
            var count = rsm.getColumnCount();
            while (resultSet.next()) {
                var s = new HashMap<String, Object>();
                for (int i = 1; i <= count; i++) {
                    s.put(rsm.getColumnLabel(i), resultSet.getObject(i));
                }
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 查询 返回值  对象集合
     *
     * @param sql   sql
     * @param param 参数
     * @param clazz 待转换的对象
     * @param <T>   泛型
     * @return list T
     */
    public static <T> List<T> query(String sql, Map<String, Object> param, Class<T> clazz) {
        var list = new ArrayList<T>();
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, param); var rs = preparedStatement.executeQuery()) {
            var rsm = rs.getMetaData();
            var count = rsm.getColumnCount();
            var allField = new Field[count + 1];
            for (int i = 1; i <= count; i++) {
                try {
                    allField[i] = clazz.getField(rsm.getColumnLabel(i));
                    allField[i].setAccessible(true);
                } catch (Exception e) {
                    allField[i] = null;
                }
            }
            //从rs中取出数据，并且封装到ArrayList中
            while (rs.next()) {
                T t = clazz.getDeclaredConstructor().newInstance();
                for (int i = 1; i <= count; i++) {
                    var field = allField[i];
                    if (field != null) {
                        var filedType = field.getType();
                        var o = SQLHelper.isSupportedType(filedType) ?
                                rs.getObject(i, filedType) :
                                ObjectUtils.JsonToBean(rs.getString(i), field.getGenericType());
                        field.set(t, o);
                    }
                }
                list.add(t);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 执行 sql 语句
     *
     * @param sql   a {@link java.lang.String} object.
     * @param param a {@link java.util.Map} object.
     * @return a 执行结果
     */
    public static boolean execute(String sql, Map<String, Object> param) {
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, param)) {
            preparedStatement.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 执行更新语句
     *
     * @param sql   a {@link java.lang.String} object.
     * @param param a {@link java.util.Map} object.
     * @return a {@link cool.scx.bo.UpdateResult} object.
     */
    public static UpdateResult update(String sql, Map<String, Object> param) {
        var ids = new ArrayList<Long>();
        var affectedLength = -1;
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, param)) {
            affectedLength = preparedStatement.executeUpdate();
            var resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                ids.add(resultSet.getLong(1));
            }
            if (ids.size() == 0) {
                ids.add(-1L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UpdateResult(affectedLength, ids);
    }

    /**
     * 批量执行更新语句
     *
     * @param sql          sql
     * @param paramMapList p
     * @return r
     */
    public static UpdateResult updateBatch(String sql, List<Map<String, Object>> paramMapList) {
        var ids = new ArrayList<Long>();
        var affectedLength = -1;
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, paramMapList)) {
            affectedLength = preparedStatement.executeBatch().length;
            var resultSet = preparedStatement.getGeneratedKeys();
            while (resultSet.next()) {
                ids.add(resultSet.getLong(1));
            }
            if (ids.size() == 0) {
                ids.add(-1L);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new UpdateResult(affectedLength, ids);
    }

    private static PreparedStatement getPreparedStatement(Connection con, String sql, Map<String, Object> paramMap) throws Exception {
        var indexNameMapping = getIndexNameMapping(sql);
        var preparedStatement = getPreparedStatement0(con, sql);
        //循环加入
        if (paramMap != null) {
            setObject(indexNameMapping, preparedStatement, paramMap);
        }
        if (ScxConfig.showLog()) {
            var realSQL = preparedStatement.unwrap(ClientPreparedStatement.class).asSql();
            Ansi.out().color(ScxConfig.DATETIME_FORMATTER.format(LocalDateTime.now()) + " " + realSQL).println();
        }
        return preparedStatement;
    }

    /**
     * 获取 PreparedStatement (带填充数据)
     *
     * @param con          连接对象
     * @param sql          包含具名参数的 sql 语句
     * @param paramMapList 参数列表
     * @return p
     * @throws Exception 异常
     */
    private static PreparedStatement getPreparedStatement(Connection con, String sql, List<Map<String, Object>> paramMapList) throws Exception {
        var indexNameMapping = getIndexNameMapping(sql);
        var preparedStatement = getPreparedStatement0(con, sql);
        //循环加入
        for (var paramMap : paramMapList) {
            if (paramMap != null) {
                setObject(indexNameMapping, preparedStatement, paramMap);
                preparedStatement.addBatch();
            }
        }
        if (ScxConfig.showLog()) {
            var realSQL = preparedStatement.unwrap(ClientPreparedStatement.class).asSql();
            Ansi.out().color(ScxConfig.DATETIME_FORMATTER.format(LocalDateTime.now()) + " " + realSQL + "... 额外的 " + (paramMapList.size() - 1) + " 项").println();
        }
        return preparedStatement;
    }

    /**
     * 向 preparedStatement 填充数据
     *
     * @param indexNameMapping  索引名称映射表
     * @param preparedStatement p
     * @param paramMap          参数列表
     * @throws SQLException e
     */
    private static void setObject(Map<Integer, String> indexNameMapping, PreparedStatement preparedStatement, Map<String, Object> paramMap) throws SQLException {
        for (var key : indexNameMapping.keySet()) {
            var name = indexNameMapping.get(key);
            //获取 参数
            var tempValue = paramMap.get(name);
            if (tempValue != null) {
                //判断是否为数据库(MySQL)直接支持的数据类型
                if (SQLHelper.isSupportedType(tempValue.getClass())) {
                    preparedStatement.setObject(key, tempValue);
                } else {//不是则转换为 json 存入
                    preparedStatement.setString(key, ObjectUtils.beanToJson(tempValue));
                }
            } else {
                preparedStatement.setObject(key, null);
            }
        }
    }

    /**
     * 根据 sql 获取 索引及名称映射表
     *
     * @param sql sql
     * @return r
     */
    private static Map<Integer, String> getIndexNameMapping(String sql) {
        var matcher = pattern.matcher(sql);
        //索引和名称 mapping
        var indexNameMapping = new HashMap<Integer, String>();
        var nameIndex = 1;
        while (matcher.find()) {
            var name = matcher.group(2);
            indexNameMapping.put(nameIndex, name);
            nameIndex++;
        }
        return indexNameMapping;
    }

    /**
     * 获取 PreparedStatement (注意!!! 此时数据并未填充)
     *
     * @param con con
     * @param sql sql
     * @return p
     * @throws SQLException e
     */
    private static PreparedStatement getPreparedStatement0(Connection con, String sql) throws SQLException {
        var matcher = pattern.matcher(sql);
        String realSQL = matcher.replaceAll("?");
        return con.prepareStatement(realSQL, Statement.RETURN_GENERATED_KEYS);
    }

}
