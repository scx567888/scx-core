package cool.scx._ext.organization;

import cool.scx.annotation.FromBody;
import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.Method;
import cool.scx.exception.UnauthorizedException;
import cool.scx.vo.Json;

/**
 * 默认认证 api 推荐使用
 * 也可以不用此 api 但需要将 自定义 AuthHandler 的实现中的方法清空
 *
 * @author scx567888
 * @version 0.3.6
 */
@ScxMapping("/api/auth")
public class OrganizationAuthController {

    /**
     * handler
     */
    private final OrganizationAuthHandler organizationAuthHandler;

    /**
     * a
     *
     * @param organizationAuthHandler a
     */
    public OrganizationAuthController(OrganizationAuthHandler organizationAuthHandler) {
        this.organizationAuthHandler = organizationAuthHandler;
    }

    /**
     * 登录方法
     *
     * @param username 用户名
     * @param password 密码
     * @return json
     */
    @ScxMapping(method = Method.POST)
    public Json login(@FromBody String username, @FromBody String password) {
        return organizationAuthHandler.login(username, password);
    }

    /**
     * 注册方法
     *
     * @param username 前台发送的用户名
     * @param password 前台发送的密码
     * @return a {@link cool.scx.vo.Json} object.
     */
    @ScxMapping(method = Method.POST)
    public Json signup(String username, String password) {
        return organizationAuthHandler.signup(username, password);
    }

    /**
     * 退出登录方法 同时清空 session 里的登录数据
     *
     * @return 是否成功退出
     */
    @ScxMapping(method = Method.POST)
    public Json logout() {
        return organizationAuthHandler.logout();
    }

    /**
     * 拉取当前登录用户的信息 (包括权限)
     *
     * @return Json
     * @throws cool.scx.exception.UnauthorizedException if any.
     */
    @ScxMapping(method = Method.GET)
    public Json info() throws UnauthorizedException {
        return organizationAuthHandler.info();
    }

    /**
     * 用户自己更新的信息 (不包括权限)
     *
     * @param user 用户信息
     * @return Json
     */
    @ScxMapping(method = Method.PUT)
    public Json infoUpdate(User user) {
        return organizationAuthHandler.infoUpdate(user);
    }

}
