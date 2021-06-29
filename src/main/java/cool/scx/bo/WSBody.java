package cool.scx.bo;

/**
 * 前台和后台发送 websocket 消息的 封装体
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

    public WSBody(String eventName, String callBackID, Object data) {
        this.eventName = eventName;
        this.callBackID = callBackID;
        this.data = data;
    }

}
