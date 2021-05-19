package cool.scx.sql;

import com.zaxxer.hikari.HikariDataSource;
import cool.scx.bo.UpdateResult;
import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.ObjectUtils;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
 * @author 司昌旭
 * @version 1.0.10
 */
public final class SQLRunner {

    /**
     * 数据源
     */
    private static final HikariDataSource dataSource = new HikariDataSource();

    /**
     * 参数过滤正则表达式
     */
    private static final Pattern pattern = Pattern.compile("(:([\\w.]+))");

    static {
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        var d = ScxConfig.dataSource();
        var jdbcUrl = "jdbc:mysql://" + d.host + ":" + d.port + "/" + d.database;
        for (String parameter : d.parameters) {
            var p = parameter.split("=");
            if (p.length == 2) {
                dataSource.addDataSourceProperty(p[0], p[1]);
            }
        }
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setUsername(d.username);
        dataSource.setPassword(d.passwordValue);
    }

    /**
     * 获取 JDBC 连接
     *
     * @return jdbc 连接
     * @throws java.lang.Exception if any.
     */
    public static Connection getConnection() throws Exception {
        return dataSource.getConnection();
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
                        var o = SQLHelper.isSupportedType(filedType) ? rs.getObject(i, filedType) : ObjectUtils.JsonToBean(rs.getString(i), filedType);
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
     * 使用参数值 paramMap，填充 PreparedStatement
     *
     * @param con      连接对象
     * @param sql      未处理的sql 语句
     * @param paramMap 参数 map
     * @return PreparedStatement
     * @throws java.lang.Exception if any.
     */
    private static PreparedStatement getPreparedStatement(Connection con, String sql, Map<String, Object> paramMap) throws Exception {
        var result = pattern.matcher(sql).replaceAll("?");
        var preparedStatement = con.prepareStatement(result, Statement.RETURN_GENERATED_KEYS);
        var index = 1;
        if (paramMap != null) {
            var matcher = pattern.matcher(sql);
            while (matcher.find()) {
                var tempValue = paramMap.get(matcher.group(2));
                if (tempValue != null) {
                    if (SQLHelper.isSupportedType(tempValue.getClass())) {
                        preparedStatement.setObject(index, tempValue);
                    } else {
                        preparedStatement.setString(index, ObjectUtils.beanToJson(tempValue));
                    }
                } else {
                    preparedStatement.setObject(index, null);
                }
                index++;
            }
        }
        if (ScxConfig.showLog()) {
            var s = preparedStatement.toString();
            Ansi.OUT.print(ScxConfig.dateTimeFormatter().format(LocalDateTime.now()) + " " + s.substring(s.indexOf(":"))).ln();
        }
        return preparedStatement;
    }

}
