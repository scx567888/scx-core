package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import cool.scx.enumeration.Color;
import cool.scx.util.CryptoUtils;
import cool.scx.util.LogUtils;

import java.util.concurrent.atomic.AtomicBoolean;

import static cool.scx.config.ScxConfig.getConfigValue;

/**
 * <p>DataSource class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class DataSource {
    /**
     * 数据源 url
     */
    public String url;
    /**
     * 数据源 用户名
     */
    public String username;
    /**
     * 数据源密码 字符串值
     */
    public String password;
    /**
     * 数据源密码 真实值(解密后)
     */
    @JsonIgnore
    public String passwordValue;

    /**
     * <p>Constructor for DataSource.</p>
     *
     * @param needFixConfig a {@link java.util.concurrent.atomic.AtomicBoolean} object.
     * @return a {@link cool.scx.config.example.DataSource} object.
     */
    public static DataSource from(AtomicBoolean needFixConfig) {
        var dataSource = new DataSource();
        dataSource.url = getConfigValue("scx.data-source.url", "jdbc:mysql://127.0.0.1:3306/scx",
                s -> LogUtils.println("✔ 数据库 JDBC Url                       \t -->\t " + s, Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.data-source.url          \t -->\t 已采用默认值 : " + f, Color.RED);
                },
                JsonNode::asText, a -> a);

        dataSource.username = getConfigValue("scx.data-source.username", "root",
                s -> LogUtils.println("✔ 数据库连接用户                          \t -->\t " + s, Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.data-source.username     \t -->\t 已采用默认值 : " + f, Color.RED);
                },
                JsonNode::asText, a -> a);

        dataSource.password = getConfigValue("scx.data-source.password", "",
                s -> LogUtils.println("✔ 数据库连接密码                          \t -->\t " + s, Color.GREEN),
                f -> {
                    needFixConfig.set(true);
                    LogUtils.println("✘ 未检测到 scx.data-source.password     \t -->\t 请检查数据库密码是否正确", Color.RED);
                },
                JsonNode::asText, a -> a);

        if (!"".equals(dataSource.password)) {
            var tempPasswordValue = "";
            try {
                tempPasswordValue = CryptoUtils.decryptText(dataSource.password);
            } catch (Exception e) {
                LogUtils.println("✘ 解密 scx.data-source.password 出错  \t -->\t 请检查数据库密码是否正确", Color.RED);
            }
            dataSource.passwordValue = tempPasswordValue;
        } else {
            dataSource.passwordValue = "";
        }
        return dataSource;
    }
}
