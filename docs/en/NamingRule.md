### The naming rules in the project !!!

#### 1 , Java 和 JavaScript variables and method names are named by camel's hump starting with lowercase

java example:

``` java 
 public Car getBuleCar() {
     Car buleCar = new BuleCar("blue");
     int nowCarPrice = 30000;
     buleCar.price = nowCarPrice;
     retuen buleCar;
 }
```

javascript example:

``` javascript 
 function getBuleCar() {
     var buleCar = {color : 'blue', price : 0}; 
     var nowCarPrice = 30000;
     buleCar.price = nowCarPrice;
     reruen buleCar;
 }
```

#### 2 , Java 和 JavaScript constants are all capitalized

java example:

``` java 
 List<Car> GLOBAL_CAR_LIST = new ArrayList<>();
 int MAX_CAR_PRICE = 90000; 
```

javascript example:

``` javascript 
 var GLOBAL_CAR_LIST = [];
 var MAX_CAR_PRICE = 90000; 
```

#### 3,The Server api used short line nomenclature

java example:

``` java 
 @ScxMapping("/api/auth/check-car-color-exists")
 public Json checkCarColorExists(String carColor) {
     XXX = XXXXXXXX;
     return XXX     
 }
```

#### 4,The database table names are all lowercase

4.1 Prefixes are strongly recommended for custom modules <br>

SQL example :

``` sql 
 create table my_module_car (
     `color` varchar(123),
     `price` int
 )
```

Use the function of internal table building ( ScxModule ) example : <br >
About @ScxModel See [@ScxModel](./annotation/ScxModel.md) And [FixTable](./feature/FixTable.md) <br/>

``` java 
 @ScxModel(tablePrefix = "my_module")
 public class Car extends BaseModel {

     // car color
     public String color;

     // car price
     public String price;

 }
```
