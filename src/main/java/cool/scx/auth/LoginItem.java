package cool.scx.auth;

import cool.scx.enumeration.DeviceType;

import java.io.Serializable;

/**
 * 已登录用户对象
 *
 * @author scx567888
 * @version 1.0.10
 */
public final class LoginItem implements Serializable {

    /**
     * 唯一 ID 用于标识用户
     */
    public final String uniqueID;
    /**
     * 登陆的设备类型
     */
    public final DeviceType loginDevice;
    /**
     * 本质上一个是一个随机字符串
     * <p>
     * 前端 通过此值获取登录用户
     * <p>
     * 来源可以多种 header , cookie ,url 等
     */
    public String token;

    /**
     * 构造函数
     *
     * @param loginDevice {@link #loginDevice}
     * @param token       {@link #token}
     * @param uniqueID    {@link #uniqueID}
     */
    public LoginItem(String token, String uniqueID, DeviceType loginDevice) {
        this.token = token;
        this.uniqueID = uniqueID;
        this.loginDevice = loginDevice;
    }

}
