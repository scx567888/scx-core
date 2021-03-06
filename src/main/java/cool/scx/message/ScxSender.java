package cool.scx.message;

import cool.scx.context.ScxContext;
import cool.scx.module.ScxModuleHandler;
import cool.scx.util.Ansi;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>ScxSender class.</p>
 *
 * @author scx567888
 * @version 1.1.9
 */
public class ScxSender {

    private static final Map<String, BaseSender<?, ?, ?>> NameMapping = new HashMap<>();

    private static final Map<Class<?>, BaseSender<?, ?, ?>> ClassMapping = new HashMap<>();

    /**
     * <p>initSender.</p>
     */
    public static void initSender() {
        Ansi.OUT.brightCyan("ScxSender 初始化中 ...").ln();
        addSender(new EmailSender());
        ScxModuleHandler.iterateClass(c -> {
            if (!c.isInterface() && BaseSender.class.isAssignableFrom(c)) {
                var tempSender = (BaseSender<?, ?, ?>) ScxContext.getBean(c);
                addSender(tempSender);
                Ansi.OUT.brightCyan("已加载自定义 Sender , 名称 [" + tempSender.senderName() + "] Class -> " + c.getName()).ln();
            }
            return true;
        });
        Ansi.OUT.brightCyan("共加载 " + NameMapping.size() + " 个 Sender ...").ln();
        Ansi.OUT.brightCyan("ScxSender 初始化完成 ...").ln();
    }

    /**
     * <p>getSender.</p>
     *
     * @param clazz a {@link java.lang.Class} object
     * @param <T>   a T class
     * @return a T object
     */
    @SuppressWarnings("unchecked")
    public static <T extends BaseSender<?, ?, ?>> T getSender(Class<T> clazz) {
        return (T) ClassMapping.get(clazz);
    }

    /**
     * <p>getSender.</p>
     *
     * @param name a {@link java.lang.String} object
     * @return a {@link cool.scx.message.BaseSender} object
     */
    public static BaseSender<?, ?, ?> getSender(String name) {
        return NameMapping.get(name);
    }

    private static void addSender(BaseSender<?, ?, ?> sender) {
        NameMapping.put(sender.senderName(), sender);
        ClassMapping.put(sender.getClass(), sender);
    }

}
