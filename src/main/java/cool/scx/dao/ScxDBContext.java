package cool.scx.dao;

import com.zaxxer.hikari.HikariDataSource;
import cool.scx.ScxEventBus;
import cool.scx.annotation.ScxModel;
import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.FixTableResult;
import cool.scx.gui.SQLGUIHandler;
import cool.scx.module.ScxModule;
import cool.scx.sql.SQLHelper;
import cool.scx.util.Ansi;
import cool.scx.util.ScxUtils;

import javax.sql.DataSource;
import java.util.List;

/**
 * <p>ScxDBContext class.</p>
 *
 * @author scx567888
 * @version 1.1.5
 */
public final class ScxDBContext {

    /**
     * 数据源
     */
    private static DataSource dataSource;

    static {
        //模块加载时的消费者
        ScxEventBus.consumer(ScxContext.ON_CONTEXT_REGISTER_NAME, o -> {
            var scxModuleList = ScxUtils.cast(o);
            fixTableByScxModel(scxModuleList);
        });
    }

    /**
     * <p>fixTable.</p>
     *
     * @param scxModuleList
     */
    public static void fixTableByScxModel(List<ScxModule> scxModuleList) {
        //如果无法链接数据库 就跳过修复表
        if (!checkDataSource()) {
            return;
        }
        Ansi.out().brightMagenta("检查数据表结构中...").ln();
        //已经显示过修复表的 gui 这里使用 flag 只显示一次
        boolean alreadyShowConfirmFixTable = false;
        //修复成功的表
        var fixSuccess = 0;
        //修复失败的表
        var fixFail = 0;
        //不需要修复的表
        var noNeedToFix = 0;
        for (ScxModule scxModule : scxModuleList) {
            for (Class<?> v : scxModule.classList) {
                //只对 ScxModel 注解标识的了类进行数据表修复
                if (v.isAnnotationPresent(ScxModel.class) && !v.isInterface()) {
                    //判断是否需要修复
                    if (SQLHelper.needFixTable(v)) {
                        //如果已经显示过gui选择界面了就不再显示
                        if (!alreadyShowConfirmFixTable) {
                            //获取用户数据 true 为修复 false 为不修复
                            var cancelFix = !SQLGUIHandler.confirmFixTable();
                            //如果取消修复 直接跳出这个方法
                            if (cancelFix) {
                                Ansi.out().brightMagenta("已取消修复表...").ln();
                                return;
                            }
                            //设置 flag
                            alreadyShowConfirmFixTable = true;
                        }
                        //获取修复表的结果
                        var r = SQLHelper.fixTable(v);
                        if (r == FixTableResult.FIX_SUCCESS) {
                            fixSuccess = fixSuccess + 1;
                        } else if (r == FixTableResult.FIX_FAIL) {
                            fixFail = fixFail + 1;
                        } else if (r == FixTableResult.NO_NEED_TO_FIX) {
                            noNeedToFix = noNeedToFix + 1;
                        }
                    }
                }
            }
        }

        if (fixSuccess != 0) {
            Ansi.out().brightMagenta("修复成功 " + fixSuccess + " 张表...").ln();
        }
        if (fixFail != 0) {
            Ansi.out().brightMagenta("修复失败 " + fixFail + " 张表...").ln();
        }
        if (fixSuccess + fixFail == 0) {
            Ansi.out().brightMagenta("没有表需要修复...").ln();
        }

    }

    /**
     * <p>initDB.</p>
     */
    public static void initDB() {
        Ansi.out().brightMagenta("ScxDBContext 初始化完成...").ln();
    }

    private static boolean checkDataSource() {
        DataSource ds = getDataSourceByConfig();
        try (var conn = ds.getConnection()) {
            var dm = conn.getMetaData();
            Ansi.out().brightMagenta("数据源连接成功 : 类型 [" + dm.getDatabaseProductName() + "]  版本 [" + dm.getDatabaseProductVersion() + "]").ln();
            dataSource = ds;
            return true;
        } catch (Exception e) {
            SQLGUIHandler.dataSourceExceptionHandler(e);
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
