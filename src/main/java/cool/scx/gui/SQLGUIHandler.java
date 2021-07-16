package cool.scx.gui;

import cool.scx.util.Ansi;

import javax.swing.*;

/**
 * SQLGUIHandler
 *
 * @author scx567888
 * @version 1.0.10
 */
public class SQLGUIHandler extends BaseGUIHandler {

    /**
     * 数据源连接异常
     *
     * @param e a {@link java.lang.Exception} object.
     */
    public static void dataSourceExceptionHandler(Exception e) {
        Ansi.out().red("X 数据源连接失败 !!!").ln();
        var options = new Object[]{"忽略错误", "退出"};
        int result = JOptionPane.showOptionDialog(null, "错误信息:\r\n " + e.getMessage(), "N 数据源连接错误 !!!", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
        if (result == 1) {
            e.printStackTrace();
            System.exit(-1);
        } else {
            Ansi.out().red("N 数据源链接错误,用户已忽略!!!").ln();
            e.printStackTrace();
        }
    }

    /**
     * 向用户确认是否修复数据表
     *
     * @return a 结果
     */
    public static boolean confirmFixTable() {
        var options = new Object[]{"修复数据表", "忽略", "退出"};
        int result = JOptionPane.showOptionDialog(null, "Y 检测到需要修复数据表 , 是否修复?", "Y 是否修复数据表 !!!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (result == 0) {
            return true;
        } else if (result == 1) {
            return false;
        } else if (result == 2) {
            System.exit(-1);
        } else if (result == -1) {
            System.exit(-1);
        }
        return true;
    }

}
