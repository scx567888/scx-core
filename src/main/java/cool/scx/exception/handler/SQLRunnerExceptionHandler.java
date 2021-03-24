package cool.scx.exception.handler;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;

import javax.swing.*;

public class SQLRunnerExceptionHandler {
    static {
        /*
          初始化提示框样式
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void sqlExceptionHandler(Exception e) {
        Ansi.OUT.red("✘ 数据源连接失败                       \t -->\t " + ScxConfig.dataSourceUrl()).ln();
        var options = new Object[]{"忽略错误", "退出"};
        int result = JOptionPane.showOptionDialog(null, "错误信息:\r\n " + e.getMessage(), "✘ 数据源连接错误 !!!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        if (result == 1) {
            e.printStackTrace();
            System.exit(-1);
        } else {
            Ansi.OUT.red("✘ 数据源链接错误,用户已忽略!!!").ln();
            e.printStackTrace();
        }
    }
}
