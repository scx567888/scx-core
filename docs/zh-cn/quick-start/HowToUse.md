## 如何使用

### 项目架构

项目采用 java 进行编写 ， maven 构建<br>
所以要求使用者使用 java 语言进行开发 , 当然除此之外还有一些版本上的要求

```
java  >= 11
maven >= 3.6.3
mysql >= 8
```

### 如何构建 (打包)

```
-> 执行根目录下的 script.ps1 安装提示进行操作即可
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
业务模块引入 scx
支持启动参数
如 : java -jar .\scx-bole-0.0.1.jar --scx.port=9999 --scx.fix-table=true
启动参数会覆盖配置文件中的值
```

注意事项 !!!

```
。。。
```
