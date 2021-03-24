package cool.scx.exception.handler;

import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.NetUtils;
import cool.scx.web.ScxServer;

import javax.swing.*;

/**
 * <p>ScxServerExceptionHandler class.</p>
 *
 * @author 司昌旭
 * @version 1.0.8
 */
public class ScxServerExceptionHandler {
    static {
        /**
         * 初始化提示框样式
         */
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>bindExceptionHandler.</p>
     */
    public static void bindExceptionHandler() {
        var options = new Object[]{"采用新端口号", "忽略", "退出"};
        int result = JOptionPane.showOptionDialog(null, "✘ 端口号 [ " + (ScxConfig.port()) + " ] 已被占用 !!!   请选择采取措施!!!", "✘ 端口号 [ " + (ScxConfig.port()) + " ] 已被占用 !!!", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        if (result == 0) {
            ScxServer.port = checkPort(ScxServer.port);
            JOptionPane.showMessageDialog(null, "✔ 新端口号 [ " + ScxServer.port + " ] !!!", "✔ 新端口号 [ " + ScxServer.port + " ] !!!", JOptionPane.INFORMATION_MESSAGE);
            ScxServer.startServer();
        } else if (result == 1) {
            Ansi.OUT.red("✘ 端口号被占用,用户已忽略!!! 服务器未启动!!!").ln();
        } else if (result == 2) {
            System.exit(-1);
        } else if (result == -1) {
            System.exit(-1);
        }
    }

    /**
     * 检查端口号是否可以使用
     * 当端口号不可以使用时会 将端口号进行累加 1 直到端口号可以使用
     *
     * @param p 需要检查的端口号
     * @return 可以使用的端口号
     */
    private static int checkPort(int p) {
        while (NetUtils.isLocalePortUsing(p)) {
            p = p + 1;
            Ansi.OUT.red("✘ 端口号 [ " + (p - 1) + " ] 已被占用 !!!         \t -->\t 新端口号 : " + p).ln();
        }
        return p;
    }

}
