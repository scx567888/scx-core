package cool.scx;

import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;

import java.io.*;
import java.util.function.Consumer;

/**
 * 事件总线<br>
 * 针对 vertx 的 eventbus 进行简单封装
 *
 * @author scx567888
 * @version 1.1.9
 */
public final class ScxEventBus {

    /**
     * vertx 的事件总线
     */
    private static final EventBus VERTX_EVENTBUS = Scx.vertx().eventBus();

    /**
     * 注册消费者 这里注册的消费者 前端和本地均可以进行调用
     *
     * @param address a
     * @param handler h
     */
    public static void consumer(String address, Consumer<Object> handler) {
        VERTX_EVENTBUS.localConsumer(address, (Handler<Message<Buffer>>) message -> handler.accept(bufferToObject(message.body())));
    }

    /**
     * 注册消费者 这里注册的消费者 前端和本地均可以进行调用
     *
     * @param address a
     * @param message a {@link java.lang.Object} object
     */
    public static void publish(String address, Object message) {
        VERTX_EVENTBUS.publish(address, objectToBuffer(message));
    }

    /**
     * 初始化默认 handler
     */
    public static void initConsumer() {
        //循环从模块中寻找标记 @ScxEventConsumer 注解的消费者并添加到事件总线中 todo

    }

    /**
     * {@inheritDoc}
     */
    private static Buffer objectToBuffer(Object obj) {
        Buffer buffer = null;
        try (var b = new ByteArrayOutputStream(); var o = new ObjectOutputStream(b)) {
            o.writeObject(obj);
            buffer = Buffer.buffer(b.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * {@inheritDoc}
     */
    private static Object bufferToObject(Buffer buffer) {
        Object msg = null;
        try (var b = new ByteArrayInputStream(buffer.getBytes()); var o = new ObjectInputStream(b)) {
            msg = o.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return msg;
    }

}
