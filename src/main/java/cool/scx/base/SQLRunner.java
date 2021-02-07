package cool.scx.base;

import com.zaxxer.hikari.HikariDataSource;
import cool.scx.boot.ScxConfig;
import cool.scx.enumeration.Color;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;

/**
 * <p>SQLRunner class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class SQLRunner {

    private static final HikariDataSource dataSource;
    private static final Pattern pattern = Pattern.compile("(:([\\w.]+))");

    static {
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(ScxConfig.dataSourceUrl);
        dataSource.setUsername(ScxConfig.dataSourceUsername);
        dataSource.setPassword(ScxConfig.dataSourcePassword);
    }

    /**
     * <p>testConnection.</p>
     *
     * @return a boolean.
     */
    public static boolean testConnection() {
        try (var conn = getConnection()) {
            var dm = conn.getMetaData();
            StringUtils.println("数据源连接成功 : 类型 [" + dm.getDatabaseProductName() + "]  版本 [" + dm.getDatabaseProductVersion() + "]", Color.MAGENTA);
            return true;
        } catch (Exception e) {
            StringUtils.println("数据源连接失败                       \t -->\t " + ScxConfig.dataSourceUrl, Color.RED);
            if (ScxConfig.showLog) {
                e.printStackTrace();
            }
            return false;
        }
    }

    /**
     * <p>getConnection.</p>
     *
     * @return a Connection object.
     * @throws java.sql.SQLException if any.
     */
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    private static <T> ArrayList<T> query(String sql, Map<String, Object> param, Function<Map<String, Object>, T> convertFun) {
        var list = new ArrayList<T>();
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, param); var resultSet = preparedStatement.executeQuery()) {
            var resultSetMetaData = resultSet.getMetaData();
            var count = resultSetMetaData.getColumnCount();
            //从rs中取出数据，并且封装到ArrayList中
            while (resultSet.next()) {
                var s = new HashMap<String, Object>();
                for (int i = 1; i <= count; i++) {
                    s.put(resultSetMetaData.getColumnLabel(i), resultSet.getObject(i));
                }
                list.add(convertFun.apply(s));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * <p>query.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @param param a {@link java.util.Map} object.
     * @param clazz a {@link java.lang.Class} object.
     * @param <T> a T object.
     * @return a {@link java.util.List} object.
     */
    public static <T> List<T> query(String sql, Map<String, Object> param, Class<T> clazz) {
        return query(sql, param, c -> ObjectUtils.mapToBean(c, clazz));
    }

    /**
     * <p>query.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @param param a {@link java.util.Map} object.
     * @return a {@link java.util.List} object.
     */
    public static List<Map<String, Object>> query(String sql, Map<String, Object> param) {
        return query(sql, param, c -> c);
    }


    /**
     * 使用参数值 paramMap，填充 PreparedStatement
     *
     * @param con      连接对象
     * @param sql      未处理的sql 语句
     * @param paramMap 参数 map
     * @return PreparedStatement
     * @throws java.sql.SQLException if any.
     */
    public static PreparedStatement getPreparedStatement(Connection con, String sql, Map<String, Object> paramMap) throws SQLException {
        var matcher = pattern.matcher(sql);
        var result = pattern.matcher(sql).replaceAll("?");
        var preparedStatement = con.prepareStatement(result, Statement.RETURN_GENERATED_KEYS);
        int index = 1;
        while (matcher.find()) {
            preparedStatement.setObject(index, paramMap.get(matcher.group(2)));
            index++;
        }
        if (ScxConfig.showLog) {
            var s = preparedStatement.toString();
            StringUtils.printlnAutoColor(ScxConfig.dateTimeFormatter.format(LocalDateTime.now()) + " " + s.substring(s.indexOf(":")));
        }
        return preparedStatement;
    }

    /**
     * <p>execute.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @return a boolean.
     */
    public static boolean execute(String sql) {
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, new HashMap<>())) {
            preparedStatement.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * <p>update.</p>
     *
     * @param sql a {@link java.lang.String} object.
     * @param param a {@link java.util.Map} object.
     * @return a {@link cool.scx.base.SQLRunner.UpdateResult} object.
     */
    public static UpdateResult update(String sql, Map<String, Object> param) {
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, param)) {
            var affectedLength = preparedStatement.executeUpdate();
            var resultSet = preparedStatement.getGeneratedKeys();
            var ids = new ArrayList<Long>();
            while (resultSet.next()) {
                ids.add(resultSet.getLong(1));
            }
            if (ids.size() == 0) {
                ids.add(-1L);
            }
            return new UpdateResult(affectedLength, ids);
        } catch (Exception e) {
            e.printStackTrace();
            return new UpdateResult(-1, new ArrayList<>());
        }
    }

    public static class UpdateResult {
        public final Integer affectedLength;
        public final List<Long> generatedKeys;

        UpdateResult(Integer affectedLength, List<Long> generatedKeys) {
            this.affectedLength = affectedLength;
            this.generatedKeys = generatedKeys;
        }
    }
}
