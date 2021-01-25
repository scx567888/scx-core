package cool.scx.base;

import com.zaxxer.hikari.HikariDataSource;
import cool.scx.boot.ScxConfig;
import cool.scx.util.ObjectUtils;
import cool.scx.util.StringUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public final class SQLRunner {

    private static final HikariDataSource dataSource;
    private static final DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Pattern pattern = Pattern.compile("(:([\\w.]+))");
    private static boolean nextSqlPrintColor = false;

    static {
        dataSource = new HikariDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setJdbcUrl(ScxConfig.datasourceUrl);
        dataSource.setUsername(ScxConfig.datasourceUsername);
        dataSource.setPassword(ScxConfig.datasourcePassword);
    }

    public static boolean testConnection() {
        try (var conn = getConnection()) {
            var dm = conn.getMetaData();
            StringUtils.println("数据源连接成功 : 类型 [" + dm.getDatabaseProductName() + "]  版本 [" + dm.getDatabaseProductVersion() + "]", StringUtils.Color.MAGENTA);
            return true;
        } catch (Exception e) {
            StringUtils.println("数据源连接失败                       \t -->\t " + ScxConfig.datasourceUrl, StringUtils.Color.RED);
            if (ScxConfig.showLog) {
                e.printStackTrace();
            }
            return false;
        }
    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    public static <T> ArrayList<T> query(Class<T> clazz, String sql, Map<String, Object> param) {
        var list = new ArrayList<T>();
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, param); var resultSet = preparedStatement.executeQuery()) {
            var resultSetMetaData = resultSet.getMetaData();
            //从rs中取出数据，并且封装到ArrayList中
            while (resultSet.next()) {
                var s = new HashMap<String, Object>();
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    s.put(resultSetMetaData.getColumnLabel(i), resultSet.getObject(i));
                }
                list.add(ObjectUtils.mapToBean(s, clazz));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 使用参数值 paramMap，填充 PreparedStatement
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
            StringUtils.println(format.format(LocalDateTime.now()) + " " + s.substring(s.indexOf(":")), nextSqlPrintColor ? StringUtils.Color.BLUE : StringUtils.Color.GREEN);
            nextSqlPrintColor = !nextSqlPrintColor;
        }
        return preparedStatement;
    }

    public static List<Map<String, Object>> query(String sql, Map<String, Object> param) {
        var list = new ArrayList<Map<String, Object>>();
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, param); var resultSet = preparedStatement.executeQuery()) {
            var resultSetMetaData = resultSet.getMetaData();
            //从rs中取出数据，并且封装到ArrayList中
            while (resultSet.next()) {
                var s = new HashMap<String, Object>();
                for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                    s.put(resultSetMetaData.getColumnLabel(i), resultSet.getObject(i));
                }
                list.add(s);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static boolean execute(String sql) {
        try (var con = getConnection(); var preparedStatement = getPreparedStatement(con, sql, new HashMap<>())) {
            preparedStatement.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

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
