package cool.scx.auth;


import cool.scx.enumeration.Device;

/**
 * 已登录用户对象
 * 此对象会在 scxContext 中以类似 map 的形式存储
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class LoginItem {
    /**
     * token 本质上一个是一个随机字符串
     * 前端 通过此值获取登录用户
     * 来源可以多种 header , cookie ,url  等
     */
    public String token;

    /**
     * 唯一 ID 用于标识用户
     */
    public String uniqueID;

    /**
     * 登陆的设备类型
     */
    public Device loginDevice;

    /**
     * 构造函数
     *
     * @param loginDevice a {@link cool.scx.enumeration.Device} object
     * @param token       a {@link java.lang.String} object
     * @param uniqueID    a {@link java.lang.String} object
     */
    public LoginItem(Device loginDevice, String token, String uniqueID) {
        this.loginDevice = loginDevice;
        this.token = token;
        this.uniqueID = uniqueID;
    }
}
