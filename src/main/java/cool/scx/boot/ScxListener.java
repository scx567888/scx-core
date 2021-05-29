package cool.scx.boot;

import cool.scx.auth.LoginItem;
import cool.scx.auth.ScxAuth;
import cool.scx.module.ScxModule;
import cool.scx.util.Ansi;
import cool.scx.util.FileUtils;

import java.io.*;
import java.util.List;

/**
 * <p>ScxListener class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxListener {
    static {
        addListener();
    }

    /**
     * 初始化 事件监听
     */
    public static void initListener() {

    }

    /**
     * <p>addListener.</p>
     */
    @SuppressWarnings("unchecked")
    public static void addListener() {
        try {
            var fis = new FileInputStream(FileUtils.getFileByRootModulePath("scx-session.cache"));
            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
            var o = (List<LoginItem>) objectInputStream.readObject();
//            for (var entry : o.entrySet()) {
////                ScxContext.addLoginItem(entry.getKey(), entry.getValue());
//            }
            objectInputStream.close();
        } catch (Exception ignored) {

        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                // 初始化 模块的 start 生命周期
                ScxModule.stopModules();
                Ansi.OUT.red("项目停止!!!").ln();
                Ansi.OUT.red("保存 session 中!!!").ln();
                var fos = new FileOutputStream(FileUtils.getFileByRootModulePath("scx-session.cache"));
                var objectOutputStream = new ObjectOutputStream(fos);
                objectOutputStream.writeObject(ScxAuth.getAllLoginItem());
                objectOutputStream.close();

            } catch (IOException ignored) {

            }
        }));
    }
}
