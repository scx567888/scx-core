<p align="center">
    <img src="https://scx.cool/img/scx-logo.svg" width="300px"  alt="scx-logo"/>
</p>
<p align="center">
    <a target="_blank" href="https://github.com/scx567888/scx/actions/workflows/ci.yml">
        <img src="https://github.com/scx567888/scx/actions/workflows/ci.yml/badge.svg" alt="CI"/>
    </a>
    <a target="_blank" href="https://search.maven.org/artifact/cool.scx/scx">
        <img src="https://img.shields.io/maven-central/v/cool.scx/scx?color=ff69b4" alt="maven-central"/>
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
        <img src="https://img.shields.io/badge/Vert.x-4.1.1-blue" alt="Vert.x"/>
    </a>
    <a target="_blank" href="https://github.com/apache/freemarker">
        <img src="https://img.shields.io/badge/Freemarker-2.3.31-blue" alt="freemarker"/>
    </a>
    <a target="_blank" href="https://github.com/FasterXML/jackson">
        <img src="https://img.shields.io/badge/Jackson-2.12.3-blue" alt="jackson"/>
    </a>
    <a target="_blank" href="https://github.com/spring-projects/spring-framework">
        <img src="https://img.shields.io/badge/Spring--Framework-5.3.8-blue" alt="Spring-Framework"/>
    </a>
    <a target="_blank" href="https://github.com/brettwooldridge/HikariCP">
        <img src="https://img.shields.io/badge/HikariCP-4.0.3-blue" alt="HikariCP"/>
    </a>
</p>

> 一个 Web 后台快速开发框架

### 快速开始

1. 将此项目引入到您自己的项目中 。

``` xml
<dependency>
    <groupId>cool.scx</groupId>
    <artifactId>scx</artifactId>
    <version>1.2.5</version>
</dependency>
```

2. 编写您自己的模块 。

``` java
public class YourModule implements ScxModule {

}
```

3. 运行 main 方法 。

``` java
//只使用自己的模块
public static void main(String[] args) {
    ScxApp.run(new YourModule(), args);
}
//同时使用内置的模块
public static void main(String[] args) {
    ScxModule[] modules = {
            new BaseModule(), //基本模块
            new AuthModule(), //认证模块
            new CmsModule(),  //CMS 模块
            new YourModule()  //自定义模块
        };
    ScxApp.run(modules, args);
}
```

更多信息请查看文档 (DOCS)

[中文](./docs/zh-cn/index.md) | [English](./docs/en/index.md)
