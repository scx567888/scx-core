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
     * 和 token 对应的用户名
     * 根据具体配置情况可能唯一 (唯一性控制 是在 scxContext 中进行控制)
     */
    public String username;

    /**
     * 登陆的设备类型
     */
    public Device loginDevice;

    /**
     * 构造函数
     *
     * @param loginDevice a {@link cool.scx.enumeration.Device} object
     * @param token       a {@link java.lang.String} object
     * @param username    a {@link java.lang.String} object
     */
    public LoginItem(Device loginDevice, String token, String username) {
        this.loginDevice = loginDevice;
        this.token = token;
        this.username = username;
    }
}
