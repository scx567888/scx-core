### 项目中命名规则如下 !!!

#### 1 , Java 和 JavaScript 变量及方法名 均采用 小写开头的驼峰命名法

java 示例:

``` java 
 public Car getBuleCar() {
     Car buleCar = new BuleCar("blue");
     int nowCarPrice = 30000;
     buleCar.price = nowCarPrice;
     retuen buleCar;
 }
```

javascript 示例:

``` javascript 
 function getBuleCar() {
     var buleCar = {color : 'blue', price : 0}; 
     var nowCarPrice = 30000;
     buleCar.price = nowCarPrice;
     reruen buleCar;
 }
```

#### 2 , Java 和 JavaScript 常量 均采用 全大写的下划线命名法

java 示例:

``` java 
 List<Car> GLOBAL_CAR_LIST = new ArrayList<>();
 int MAX_CAR_PRICE = 90000; 
```

javascript 示例:

``` javascript 
 var GLOBAL_CAR_LIST = [];
 var MAX_CAR_PRICE = 90000; 
```

#### 3,后台 api 均采用 短横线命名法

java 示例:

``` java 
 @ScxMapping("/api/auth/check-car-color-exists")
 public Json checkCarColorExists(String carColor) {
     XXX = XXXXXXXX;
     return XXX     
 }
```

#### 4,数据库表名均采用 全小写的下划线命名法

4.1 自定义模块强烈建议添加前缀 <br>

SQL 示例 :

``` sql 
 create table my_module_car (
     `color` varchar(123),
     `price` int
 )
```

使用内部建表功能 ( ScxModule 注解 ) 示例 : <br >
有关 @ScxModel 请查看 [@ScxModel](./annotation/ScxModel.md) 和 [FixTable](./feature/FixTable.md) <br/>

``` java 
 @ScxModel(tablePrefix = "my_module")
 public class Car extends BaseModel {

     // 汽车颜色
     public String color;
 
     // 汽车价格
     public String price;

 }
```
