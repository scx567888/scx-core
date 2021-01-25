<p align="center">
    <img src="https://scx.cool/img/scx-logo.svg" width="300px"  alt="scx-logo"/>
</p>
<p align="center">
    <a target="_blank" href="https://dev.azure.com/scx567888/scx/_build/latest?definitionId=1&branchName=master">
        <img src="https://dev.azure.com/scx567888/scx/_apis/build/status/scx567888.scx?branchName=master"/>
    </a>
    <a target="_blank" href="https://github.com/scx567888/scx">
        <img src="https://img.shields.io/badge/version-0.1.3-blueviolet"/>
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
        <img src="https://img.shields.io/badge/Vert.x-4.0.0-blue"/>
    </a>
    <a target="_blank" href="https://github.com/apache/poi">
        <img src="https://img.shields.io/badge/Freemarker-2.3.30-blue"/>
    </a>
</p>

> 基于 Vert.x 的 后台框架

## 构建注意事项

项目采用 maven 构建<br>
版本要求

```
java  >= 11
mysql >= 8
```

打包

```
-> mvn package
```

打包 (构建脚本)

```
-> builder.bat
```

项目地址

```
https://github.com/scx567888/scx
```

联系方式

```
scx567888@outlook.com
```

通用 api 说明  !!!必看!!

```
建好实体类 (假如) BOOK 后
非特殊业务 service 和 controller 不用写
但是实体类名称必须唯一(即使不在同一模块下)
前台直接调用即可
数据库会自动生成
```

Api 说明

```
模块名称 -> demo
获取列表  POST    api/demo/book/list
获取单个  GET     api/demo/book/12
添加数据  POST    api/demo/book
修改数据  PUT     api/demo/book
删除数据  DELETE  api/demo/book/12
恢复删除  GET     api/demo/book/revokeDelete/12
批量删除  DELETE  api/demo/book/batchDelete
校验唯一  POST    api/demo/book/checkUnique
字段列表  POST    api/demo/book/getAutoComplete/fieldName
```

注意事项 !!!

```
本项目在打包时把会把 第三方引用的 jar 分离到 lib 目录
部署时记得放到 scx-*.jar 同级目录下
数据库脚本在 src/resources/sql 文件夹中
请按照顺序或者对应模块 执行 如 0_xxx.sql -> 1_xxx.sql -> 2_xxx.sql
```


使用方法

```
业务模块引入 scx-core
支持启动参数
如 : java -jar .\scx-bole-0.0.1.jar --scx.port=9999 --scx.fix-table=true
启动参数会覆盖配置文件中的值
```

注意事项 !!!

```
。。。
```
