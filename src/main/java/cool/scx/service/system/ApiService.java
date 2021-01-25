package cool.scx.service.system;

import cool.scx.annotation.ScxMapping;
import cool.scx.vo.Json;

public class ApiService {

    /**
     * 获取所有方法
     */
    @ScxMapping("getAllApi")
    public Json getAllApi() {
//        var entries = ScxRouterFactory.getMethodMappings().entrySet().iterator();
//        var list = new ArrayList<>();
//        while (entries.hasNext()) {
//            var entry = entries.next();
//            //list.add(entry.getKey() + "(" + Arrays.stream(entry.getValue().example).map(p -> p.getType().getName() + " " + p.getName()).collect(Collectors.joining(",")) + ")");
//        }
        return Json.ok().data("allApi", null);
    }


}
