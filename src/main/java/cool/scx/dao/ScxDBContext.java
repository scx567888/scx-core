package cool.scx.dao;

import com.zaxxer.hikari.HikariDataSource;
import cool.scx.annotation.ScxModel;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.FixTableResult;
import cool.scx.exception.handler.SQLRunnerExceptionHandler;
import cool.scx.util.Ansi;

import javax.sql.DataSource;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>ScxDBContext class.</p>
 *
 * @author 司昌旭
 * @version 1.1.5
 */
public class ScxDBContext {

    /**
     * 数据源
     */
    private static DataSource dataSource;

    /**
     * <p>fixTable.</p>
     */
    public static void fixTable() {
        Ansi.OUT.brightMagenta("修复数据表中...").ln();
        var noNeedFix = new AtomicBoolean(true);
        ScxContext.scxBeanClassNameMapping().forEach((k, v) -> {
            if (v.isAnnotationPresent(ScxModel.class) && !v.isInterface()) {
                try {
                    if (SQLHelper.fixTable(v) != FixTableResult.NO_NEED_TO_FIX) {
                        noNeedFix.set(false);
                    }
                } catch (Exception ignored) {

                }
            }
        });
        if (noNeedFix.get()) {
            Ansi.OUT.brightMagenta("没有表需要修复...").ln();
        }
    }

    /**
     * <p>initDB.</p>
     */
    public static void initDB() {
        Ansi.OUT.brightMagenta("ScxDBContext 初始化中...").ln();
        var dataSourceCanUse = checkDataSource();
        if (dataSourceCanUse && ScxConfig.fixTable()) {
            fixTable();
        }
        Ansi.OUT.brightMagenta("ScxDBContext 初始化完成...").ln();
    }

    private static boolean checkDataSource() {
        DataSource ds = getDataSourceByConfig();
        try (var conn = ds.getConnection()) {
            var dm = conn.getMetaData();
            Ansi.OUT.brightMagenta("数据源连接成功 : 类型 [" + dm.getDatabaseProductName() + "]  版本 [" + dm.getDatabaseProductVersion() + "]").ln();
            dataSource = ds;
            return true;
        } catch (Exception e) {
            SQLRunnerExceptionHandler.sqlExceptionHandler(e);
            return false;
        }
    }

    private static DataSource getDataSourceByConfig() {
        var ds = new HikariDataSource();
        ds.setDriverClassName("com.mysql.cj.jdbc.Driver");
        var jdbcUrl = "jdbc:mysql://" + ScxConfig.dataSourceHost() + ":" + ScxConfig.dataSourcePort() + "/" + ScxConfig.dataSourceDatabase();
        for (String parameter : ScxConfig.dataSourceParameters()) {
            var p = parameter.split("=");
            if (p.length == 2) {
                ds.addDataSourceProperty(p[0], p[1]);
            }
        }
        ds.setJdbcUrl(jdbcUrl);
        ds.setUsername(ScxConfig.dataSourceUsername());
        ds.setPassword(ScxConfig.dataSourcePassword());
        return ds;
    }

    /**
     * <p>dataSource.</p>
     *
     * @return a DataSource object
     */
    public static DataSource dataSource() {
        return dataSource;
    }
}
