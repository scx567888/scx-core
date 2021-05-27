package cool.scx.auth;

import cool.scx.base.BaseUser;
import cool.scx.context.ScxContext;
import cool.scx.enumeration.Device;
import cool.scx.util.Ansi;
import io.vertx.ext.web.RoutingContext;

import java.util.ArrayList;
import java.util.List;

public class ScxAuth {

    public static final String TOKEN_KEY = "S-Token";
    public static final String DEVICE_KEY = "S-Device";

    /**
     * userService 实例 主要用来 获取登录用户的 权限等信息
     */
    private static final AuthHandler AUTH_HANDLER;
    /**
     * 存储所有 已登录 的用户信息
     * todo 需要在 scxConfig中 添加 一个配置项 标识用户多端登录 处理方式
     * todo 比如 允许用户同时登录多个 不同的 客户端(来源一致) 或者只允许用户 在同一时间登录 (无论已经在哪里登录了)
     * todo 或者 不对登录做限制 同时允许 任意客户端(来源可以不一致) 登录任意数量的 同一用户
     */
    private static final List<LoginItem> LOGIN_ITEMS = new ArrayList<>();

    static {
        AUTH_HANDLER = ScxContext.getBean(AuthHandler.class);
    }

    /**
     * <p>authHandler.</p>
     *
     * @return a {@link cool.scx.auth.AuthHandler} object
     */
    public static AuthHandler authHandler() {
        return AUTH_HANDLER;
    }

    /**
     * <p>logoutUser.</p>
     *
     * @return a boolean.
     */
    public static boolean removeLoginUser() {
        if (ScxContext.device() == Device.WEBSITE) {
            var token = ScxContext.routingContext().getCookie(TOKEN_KEY).getValue();
            boolean b = LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
            Ansi.OUT.print("当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
            return b;
        }
        if (ScxContext.device() == Device.ADMIN) {
            var token = ScxContext.routingContext().request().getHeader(TOKEN_KEY);
            boolean b = LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
            Ansi.OUT.print("当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
            return b;
        }
        if (ScxContext.device() == Device.APPLE) {
            var token = ScxContext.routingContext().request().getHeader(TOKEN_KEY);
            boolean b = LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
            Ansi.OUT.print("当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
            return b;
        }
        if (ScxContext.device() == Device.ANDROID) {
            var token = ScxContext.routingContext().request().getHeader(TOKEN_KEY);
            boolean b = LOGIN_ITEMS.removeIf(i -> i.token.equals(token));
            Ansi.OUT.print("当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
            return b;
        }
        return false;

    }


    /**
     * <p>addUserToSession.</p>
     *
     * @param token    a {@link java.lang.String} object.
     * @param username a {@link java.lang.String} object.
     * @param device   a {@link cool.scx.enumeration.Device} object.
     */
    public static void addLoginItem(Device device, String token, String username) {
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> u.username.equals(username) && device == u.device).findAny().orElse(null);
        if (sessionItem == null) {
            LOGIN_ITEMS.add(new LoginItem(device, token, username));
        } else {
            sessionItem.token = token;
            sessionItem.device = device;
        }
        Ansi.OUT.print(username + " 登录了 , 登录设备 [" + device.toString() + "] , 当前总登录用户数量 : " + LOGIN_ITEMS.size() + " 个").ln();
    }


    /**
     * <p>getUserFromSessionByToken.</p>
     *
     * @param token  a {@link java.lang.String} object.
     * @param device a {@link cool.scx.enumeration.Device} object.
     * @return a {@link BaseUser} object.
     */
    public static BaseUser getLoginUserByToken(Device device, String token) {
        var sessionItem = LOGIN_ITEMS.stream().filter(u -> u.token.equals(token) && u.device == device).findAny().orElse(null);
        if (sessionItem == null) {
            return null;
        }
        //每次都从数据库中获取用户 保证 权限设置的及时性 但是为了 性能 此处应该做缓存 todo
        return AUTH_HANDLER.findByUsername(sessionItem.username);
    }


    /**
     * <p>getLoginUserByHeader.</p>
     *
     * @return a {@link BaseUser} object.
     */
    public static BaseUser getLoginUser() {
        if (ScxContext.device() == Device.WEBSITE) {
            String token = ScxContext.routingContext().getCookie(ScxAuth.TOKEN_KEY).getValue();
            return getLoginUserByToken(ScxContext.device(), token);
        }
        if (ScxContext.device() == Device.ADMIN) {
            String token = ScxContext.routingContext().request().getHeader(ScxAuth.TOKEN_KEY);
            return getLoginUserByToken(ScxContext.device(), token);
        }
        if (ScxContext.device() == Device.APPLE) {
            String token = ScxContext.routingContext().request().getHeader(ScxAuth.TOKEN_KEY);
            return getLoginUserByToken(ScxContext.device(), token);
        }
        if (ScxContext.device() == Device.ANDROID) {
            String token = ScxContext.routingContext().request().getHeader(ScxAuth.TOKEN_KEY);
            return getLoginUserByToken(ScxContext.device(), token);
        }
        return null;
    }

    private static Device getDevice(RoutingContext routingContext) {
        String device = routingContext.request().getHeader(DEVICE_KEY);
        if (device == null || device.equalsIgnoreCase("WEBSITE")) {
            return Device.WEBSITE;
        }
        if (device.equalsIgnoreCase("APPLE")) {
            return Device.APPLE;
        }
        if (device.equalsIgnoreCase("ADMIN")) {
            return Device.ADMIN;
        }
        if (device.equalsIgnoreCase("ANDROID")) {
            return Device.ANDROID;
        } else {
            return Device.UNKNOWN;
        }
    }

    public static String getTokenByCookie() {
        return ScxContext.routingContext().getCookie(ScxAuth.TOKEN_KEY).getValue();
    }

    /**
     * <p>getAllLoginItem.</p>
     *
     * @return a {@link java.util.List} object
     */
    public static List<LoginItem> getAllLoginItem() {
        return LOGIN_ITEMS;
    }


}
