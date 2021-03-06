package cool.scx.util;

import cool.scx.context.ScxContext;
import io.vertx.core.http.HttpServerRequest;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * 基本网络操作工具类
 *
 * @author scx567888
 * @version 0.3.6
 */
public final class NetUtils {

    /**
     * 端口号是否被占用
     *
     * @param port a int.
     * @return a boolean.
     */
    public static boolean isLocalePortUsing(int port) {
        var flag = true;
        try {
            flag = isPortUsing("127.0.0.1", port);
        } catch (Exception ignored) {
        }
        return flag;
    }

    /**
     * 端口号是否被占用
     *
     * @param host a {@link java.lang.String} object.
     * @param port a int.
     * @return a boolean.
     * @throws java.net.UnknownHostException if any.
     */
    public static boolean isPortUsing(String host, int port) throws UnknownHostException {
        boolean flag = false;
        var theAddress = InetAddress.getByName(host);
        try {
            var socket = new Socket(theAddress, port);
            flag = true;
            socket.close();
        } catch (IOException ignored) {

        }
        return flag;
    }

    /**
     * 获取访问者IP
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request .getRemoteAddr()。
     *
     * @return IP
     */
    public static String getIpAddr() {
        var context = ScxContext.routingContext();
        if (context == null) {
            return "";
        }
        HttpServerRequest request = context.request();
        var ip = request.getHeader("X-Real-IP");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            if (ip.contains("../") || ip.contains("..\\")) {
                return "";
            }
            return ip;
        }
        ip = request.getHeader("X-Forwarded-For");
        if (StringUtils.isNotEmpty(ip) && !"unknown".equalsIgnoreCase(ip)) {
            // 多次反向代理后会有多个IP值，第一个为真实IP。
            int index = ip.indexOf(',');
            if (index != -1) {
                ip = ip.substring(0, index);
            }
            if (ip.contains("../") || ip.contains("..\\")) {
                return "";
            }
        } else {
            ip = request.remoteAddress().host();
            if (ip.contains("../") || ip.contains("..\\")) {
                return "";
            }
            if (ip.equals("0:0:0:0:0:0:0:1")) {
                ip = "127.0.0.1";
            }
        }
        return ip;
    }

    /**
     * 获取本机的 ip 地址
     *
     * @return ip
     */
    public static String getLocalAddress() {
        try {
            Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
            ArrayList<String> ipv4Result = new ArrayList<>();
            ArrayList<String> ipv6Result = new ArrayList<>();
            while (enumeration.hasMoreElements()) {
                final NetworkInterface networkInterface = enumeration.nextElement();
                final Enumeration<InetAddress> en = networkInterface.getInetAddresses();
                while (en.hasMoreElements()) {
                    final InetAddress address = en.nextElement();
                    if (!address.isLoopbackAddress()) {
                        if (address instanceof Inet6Address) {
                            ipv6Result.add(normalizeHostAddress(address));
                        } else {
                            ipv4Result.add(normalizeHostAddress(address));
                        }
                    }
                }
            }
            if (!ipv4Result.isEmpty()) {
                for (String ip : ipv4Result) {
                    if (ip.startsWith("127.0") || ip.startsWith("192.168")) {
                        continue;
                    }
                    return ip;
                }
                return ipv4Result.get(ipv4Result.size() - 1);
            } else if (!ipv6Result.isEmpty()) {
                return ipv6Result.get(0);
            }
            final InetAddress localHost = InetAddress.getLocalHost();
            return normalizeHostAddress(localHost);
        } catch (SocketException | UnknownHostException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * <p>normalizeHostAddress.</p>
     *
     * @param localHost a {@link java.net.InetAddress} object.
     * @return a {@link java.lang.String} object.
     */
    public static String normalizeHostAddress(final InetAddress localHost) {
        if (localHost instanceof Inet6Address) {
            return "[" + localHost.getHostAddress() + "]";
        } else {
            return localHost.getHostAddress();
        }
    }
}
