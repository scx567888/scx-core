package cool.scx.config.example;

import com.fasterxml.jackson.annotation.JsonIgnore;
import cool.scx.config.ScxConfig;
import cool.scx.util.Ansi;
import cool.scx.util.CryptoUtils;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 数据源配置文件实体类
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public class DataSource {

    /**
     * 数据源地址
     */
    public String host;

    /**
     * 数据源端口
     */
    public Integer port;

    /**
     * 数据源端口
     */
    public String database;

    /**
     * 其他连接参数
     */
    public Set<String> parameters;

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


    private DataSource() {
    }

    /**
     * <p>Constructor for DataSource.</p>
     *
     * @param needFixConfig a {@link java.util.concurrent.atomic.AtomicBoolean} object.
     * @return a {@link cool.scx.config.example.DataSource} object.
     */
    public static DataSource from(AtomicBoolean needFixConfig) {
        var dataSource = new DataSource();

        dataSource.host = ScxConfig.value("scx.data-source.host", "127.0.0.1",
                s -> Ansi.OUT.green("Y 数据源 Host                          \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.data-source.host        \t -->\t 已采用默认值 : " + f).ln();
                });

        dataSource.port = ScxConfig.value("scx.data-source.port", 3306,
                s -> Ansi.OUT.green("Y 数据源 端口号                        \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.data-source.port        \t -->\t 已采用默认值 : " + f).ln();
                });

        dataSource.database = ScxConfig.value("scx.data-source.database", "scx",
                s -> Ansi.OUT.green("Y 数据源 数据库名称                    \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.data-source.database    \t -->\t 已采用默认值 : " + f).ln();
                });

        dataSource.username = ScxConfig.value("scx.data-source.username", "root",
                s -> Ansi.OUT.green("Y 数据源 用户名                        \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.data-source.username    \t -->\t 已采用默认值 : " + f).ln();
                });

        dataSource.password = ScxConfig.value("scx.data-source.password", "",
                s -> Ansi.OUT.green("Y 数据源 连接密码                      \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.data-source.password    \t -->\t 请检查数据库密码是否正确").ln();
                });

        dataSource.parameters = ScxConfig.value("scx.data-source.parameters", new HashSet<>(),
                s -> Ansi.OUT.green("Y 数据源 连接参数                      \t -->\t " + s).ln(),
                f -> {
                    needFixConfig.set(true);
                    Ansi.OUT.red("N 未检测到 scx.data-source.parameters  \t -->\t 已采用默认值 : " + f).ln();
                });

        try {
            dataSource.passwordValue = CryptoUtils.decryptText(dataSource.password);
        } catch (Exception e) {
            Ansi.OUT.red("N 解密 scx.data-source.password 出错  \t -->\t 请检查数据库密码是否正确").ln();
        }

        return dataSource;
    }

}
