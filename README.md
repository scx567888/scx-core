<p align="center">
    <img src="https://scx.cool/img/scx-logo.svg" width="300px"  alt="scx-logo"/>
</p>
<p align="center">
    <a target="_blank" href="https://github.com/scx567888/scx">
        <img src="https://img.shields.io/badge/version-0.4.3-ff69b4"/>
    </a> 
    <a target="_blank" href="https://github.com/scx567888/scx">
        <img src="https://img.shields.io/github/languages/code-size/scx567888/scx?color=orange"/>
    </a>
    <a target="_blank" href="https://github.com/scx567888/scx/issues">
        <img src="https://img.shields.io/github/issues/scx567888/scx"/>
    </a> 
    <a target="_blank" href="https://github.com/scx567888/scx/blob/master/LICENSE">
        <img src="https://img.shields.io/github/license/scx567888/scx"/>
    </a>
</p>
<p align="center">
   <a target="_blank" href="https://github.com/spring-projects/spring-boot">
        <img src="https://img.shields.io/badge/Vert.x-4.0.2-blue"/>
    </a>
    <a target="_blank" href="https://github.com/apache/poi">
        <img src="https://img.shields.io/badge/Freemarker-2.3.31-blue"/>
    </a>
    <a target="_blank" href="https://github.com/apache/poi">
        <img src="https://img.shields.io/badge/Jackson-2.12.1-blue"/>
    </a>
</p>

> 基于 Vert.x 的 后台 Web 快速开发框架

### 使用方法

1. 将此项目引入到您自己的项目中 。

``` xml
<dependency>
    <groupId>cool.scx</groupId>
    <artifactId>scx</artifactId>
    <version>0.4.3</version>
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
