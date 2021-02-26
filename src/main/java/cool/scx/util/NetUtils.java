package cool.scx.util;

import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * <p>NetUtils class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public final class NetUtils {

    /**
     * <p>isLocalePortUsing.</p>
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
     * <p>isPortUsing.</p>
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
     * <p>text.</p>
     */
    public static void text() {
        String url = "https://www.baidu.com";
        String s = sendHttpRequest(url, "GET");
        StringUtils.println(s);
    }

    /**
     * <p>sendHttpRequest.</p>
     *
     * @param urlParam      url
     * @param requestMethod 请求方式
     * @return 返回String类型的字符串 ，如果请求失败，返回null
     */
    public static String sendHttpRequest(String urlParam, String requestMethod) {
        HttpURLConnection con = null;
        BufferedReader buffer = null;
        StringBuffer resultBuffer = null;
        InputStream ins = null;
        try {
            URL url = new URL(urlParam);
            //得到连接对象
            con = (HttpURLConnection) url.openConnection();

            //设置请求类型


            con.setRequestMethod(requestMethod);

            //据说post请求必须设置这两条,暂时不知道
            con.setDoOutput(true);  //允许写出
            con.setDoInput(true);   //允许读入

            con.setUseCaches(false);    //不使用缓存

            //得到响应码
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //请求成功，得到响应流
                ins = con.getInputStream();
                //转化为字符串
                resultBuffer = new StringBuffer();
                String line;
                //注意响应体的编码格式，可能会有乱码
                buffer = new BufferedReader(new InputStreamReader(ins, "GBK"));

                while ((line = buffer.readLine()) != null) {
                    resultBuffer.append(line);
                }
                //将resultBuffer返回即可
                return resultBuffer.toString();
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    /**
     * 获取访问者IP
     * 在一般情况下使用Request.getRemoteAddr()即可，但是经过nginx等反向代理软件后，这个方法会失效。
     * 本方法先从Header中获取X-Real-IP，如果不存在再从X-Forwarded-For获得第一个IP(用,分割)，
     * 如果还不存在则调用Request .getRemoteAddr()。
     *
     * @param context a {@link io.vertx.ext.web.RoutingContext} object.
     * @return IP
     */
    public static String getIpAddr(RoutingContext context) {
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
