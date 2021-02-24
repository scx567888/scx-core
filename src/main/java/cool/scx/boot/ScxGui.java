package cool.scx.boot;


import cool.scx.server.ScxServer;
import cool.scx.util.MPrintStream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * <p>ScxGui class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxGui {
    static {
        if (ScxConfig.showGui) {
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
     * <p>getGui.</p>
     */
    public static void getGui() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        scxFrame.setBounds(600, 300, 500, 400);
        scxFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // 面板1
        JPanel panel = new JPanel();
        scxFrame.getContentPane().add(panel, BorderLayout.NORTH);
        JButton button = new JButton("停止服务器");
        // 监听button的选择路径
        button.addActionListener(e -> {
            ScxServer.stopServer();
            System.out.println(123);
            System.out.println("打印这句话");
            System.out.println(123);
            System.out.println(123);
        });
        panel.add(button);
        JButton button1 = new JButton("启动服务器");
        // 监听button的选择路径
        button1.addActionListener(e -> {
            ScxServer.init();
            System.out.println(123);
            System.out.println("打印这句话");
            System.out.println(123);
            System.out.println(123);
        });
        panel.add(button1);

        // 可滚动面板
        JScrollPane scrollPane = new JScrollPane();
        scxFrame.getContentPane().add(scrollPane, BorderLayout.CENTER);

        scrollPane.setViewportView(textArea);
        //这个最好放在最后，否则会出现视图问题。
        scxFrame.setVisible(true);
        MPrintStream mPrintStream = new MPrintStream(System.out, textArea);
        System.setOut(mPrintStream);


    }
}
