package cool.scx.boot;

/**
 * <p>ScxListener class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class ScxListener {
    static {
//        try {
//            var fis = new FileInputStream(PackageUtils.getFileByAppRoot("\\session.cache"));
//            ObjectInputStream objectInputStream = new ObjectInputStream(fis);
//            var o = (ConcurrentHashMap<String, String>) objectInputStream.readObject();
//            for (var entry : o.entrySet()) {
//                ScxContext.addUserToSession(entry.getKey(), entry.getValue());
//            }
//            objectInputStream.close();
//        } catch (Exception ignored) {
//
//        }
//        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//            try {
//                var fos = new FileOutputStream(PackageUtils.getFileByAppRoot("\\session.cache"));
//                var objectOutputStream = new ObjectOutputStream(fos);
////                objectOutputStream.writeObject(ScxContext.session);
//                objectOutputStream.close();
//
//            } catch (IOException ignored) {
//
//            }
//        }));
    }

    /**
     * 初始化 事件监听
     */
    public static void initListener() {

    }
}
