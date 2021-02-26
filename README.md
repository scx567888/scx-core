<p align="center">
    <img src="https://scx.cool/img/scx-logo.svg" width="300px"  alt="scx-logo"/>
</p>
<p align="center">
    <a target="_blank" href="https://github.com/scx567888/scx">
        <img src="https://img.shields.io/badge/version-0.9.3-ff69b4" alt="version"/>
    </a> 
    <a target="_blank" href="https://github.com/scx567888/scx">
        <img src="https://img.shields.io/github/languages/code-size/scx567888/scx?color=orange" alt="code-size"/>
    </a>
    <a target="_blank" href="https://github.com/scx567888/scx/issues">
        <img src="https://img.shields.io/github/issues/scx567888/scx" alt="issues"/>
    </a> 
    <a target="_blank" href="https://github.com/scx567888/scx/blob/master/LICENSE">
        <img src="https://img.shields.io/github/license/scx567888/scx" alt="license"/>
    </a>
</p>
<p align="center">
   <a target="_blank" href="https://github.com/eclipse-vertx/vert.x">
        <img src="https://img.shields.io/badge/Vert.x-4.0.2-blue" alt="Vert.x"/>
    </a>
    <a target="_blank" href="https://github.com/apache/freemarker">
        <img src="https://img.shields.io/badge/Freemarker-2.3.31-blue" alt="freemarker"/>
    </a>
    <a target="_blank" href="https://github.com/FasterXML/jackson">
        <img src="https://img.shields.io/badge/Jackson-2.12.1-blue" alt="jackson"/>
    </a>
    <a target="_blank" href="https://github.com/brettwooldridge/HikariCP">
        <img src="https://img.shields.io/badge/HikariCP-4.0.2-blue" alt="HikariCP"/>
    </a>
</p>

> 基于 Vert.x 的 后台 Web 快速开发框架

### 使用方法

1. 将此项目引入到您自己的项目中 。

``` xml
<dependency>
    <groupId>cool.scx</groupId>
    <artifactId>scx</artifactId>
    <version>0.9.3</version>
</dependency>
```

2. 编写启动类

``` java
public class YourApp {
    public static void main(String[] args) {
        ScxApp.run(YourApp.class, args);
    }
}
```

3. 运行 main 方法 。

[更多信息请查看文档](https://github.com/scx567888/scx/wiki) <br/>
[更新日志](https://github.com/scx567888/scx/wiki/CHANGELOG)
