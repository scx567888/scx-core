package cool.scx.exception.handler;

import cool.scx.base.BaseExceptionHandler;
import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;

import javax.swing.*;

/**
 * <p>SQLRunnerExceptionHandler class.</p>
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class SQLRunnerExceptionHandler extends BaseExceptionHandler {

    /**
     * <p>sqlExceptionHandler.</p>
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public static void sqlExceptionHandler(Exception e) {
        Ansi.OUT.red("X 数据源连接失败 !!!").ln();
        var options = new Object[]{"忽略错误", "退出"};
        int result = JOptionPane.showOptionDialog(null, "错误信息:\r\n " + e.getMessage(), "N 数据源连接错误 !!!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        if (result == 1) {
            e.printStackTrace();
            System.exit(-1);
        } else {
            Ansi.OUT.red("N 数据源链接错误,用户已忽略!!!").ln();
            e.printStackTrace();
        }
    }
}
