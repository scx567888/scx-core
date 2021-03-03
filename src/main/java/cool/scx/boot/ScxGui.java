package cool.scx.boot;


import cool.scx.config.ScxConfig;
import cool.scx.context.ScxContext;
import cool.scx.util.MPrintStream;
import cool.scx.util.PackageUtils;
import cool.scx.web.ScxServer;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.MalformedURLException;

/**
 * <p>ScxGui class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxGui {
    static {
        if (ScxConfig.showGui()) {
            Runnable runnable = ScxGui::getGui;
            runnable.run();
        }
    }

    /**
     * <p>init.</p>
     */
    public static void init() {

    }

    /**
     * 初始化样式
     */
    public static void initLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * <p>getGui.</p>
     */
    public static void getGui() {


        initLookAndFeel();

        JFrame scxFrame = new JFrame();
        JTextArea textArea = new JTextArea();
        textArea.setEditable(false);

        textArea.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent mouseEvent) {
                textArea.setCursor(new Cursor(Cursor.TEXT_CURSOR));   //鼠标进入Text区后变为文本输入指针
            }

            public void mouseExited(MouseEvent mouseEvent) {
                textArea.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));   //鼠标离开Text区后恢复默认形态
            }
        });
        textArea.getCaret().addChangeListener(e -> {
            textArea.getCaret().setVisible(true);   //使Text区的文本光标显示
        });
        // 窗口框架
        scxFrame.setTitle("Scx Gui 管理界面");
        try {
            scxFrame.setIconImage(Toolkit.getDefaultToolkit().getImage(PackageUtils.getFileByAppRoot("/META-INF/scx-logo.png").toURI().toURL()));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        scxFrame.setBounds(600, 300, 500, 400);
        scxFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 面板1
        JPanel panel = new JPanel();
        scxFrame.getContentPane().add(panel, BorderLayout.NORTH);
        JButton button = new JButton("停止服务器");
        JButton button1 = new JButton("启动服务器");
        JButton button2 = new JButton("清空控制台");
        JButton button3 = new JButton("重新加载配置文件");

        button2.addActionListener(e -> {
            textArea.setText("");
        });
        // 监听button的选择路径
        button.addActionListener(e -> ScxServer.stopServer());
        // 监听button的选择路径
        button1.addActionListener(e -> {

            ScxServer.startServer();
        });
        // 监听button的选择路径
        button3.addActionListener(e -> ScxConfig.reloadConfig());

        ScxContext.eventBus().consumer("startVertxServer", c -> {
            button.setEnabled(true);
            button1.setEnabled(false);
        });

        ScxContext.eventBus().consumer("stopVertxServer", c -> {
            button.setEnabled(false);
            button1.setEnabled(true);
        });

        if (ScxServer.isServerRunning()) {
            button.setEnabled(true);
            button1.setEnabled(false);
        } else {
            button.setEnabled(false);
            button1.setEnabled(true);
        }

        panel.add(button);
        panel.add(button1);
        panel.add(button2);
        panel.add(button3);
        // 可滚动面板
        JScrollPane scrollPane = new JScrollPane();
        scxFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        scrollPane.setViewportView(textArea);
        //这个最好放在最后，否则会出现视图问题。
        scxFrame.setVisible(true);
        MPrintStream mPrintStream = new MPrintStream(System.err, textArea);
        System.setOut(mPrintStream);
        System.setErr(mPrintStream);
    }
}
