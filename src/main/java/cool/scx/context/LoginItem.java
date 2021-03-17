package cool.scx.context;


import cool.scx.enumeration.Device;

/**
 * 已登录用户对象
 * 此对象会在 scxContext 中以类似 map 的形式存储
 *
 * @author scx56
 * @version $Id: $Id
 */
public class LoginItem {
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
     *
     */
    public Device device;

    /**
     * 构造函数
     *
     * @param _token    _token
     * @param _username _username
     * @param _device a {@link cool.scx.enumeration.Device} object.
     */
    public LoginItem(Device _device,String _token, String _username) {
        this.device = _device;
        this.token = _token;
        this.username = _username;
    }
}
