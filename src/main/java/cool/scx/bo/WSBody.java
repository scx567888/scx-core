package cool.scx.bo;

/**
 * 前台和后台发送 websocket 消息的 封装体
 *
 * @author 司昌旭
 * @version 1.2.2
 */
public class WSBody {
    /**
     * 事件名称 事件总线用
     */
    public final String eventName;

    /**
     * 回调 id
     */
    public final String callBackID;

    /**
     * 消息体
     */
    public final Object data;

    /**
     * <p>Constructor for WSBody.</p>
     *
     * @param eventName  a {@link java.lang.String} object
     * @param callBackID a {@link java.lang.String} object
     * @param data       a {@link java.lang.Object} object
     */
    public WSBody(String eventName, String callBackID, Object data) {
        this.eventName = eventName;
        this.callBackID = callBackID;
        this.data = data;
    }

}
