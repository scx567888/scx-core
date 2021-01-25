package cool.scx.service.cms;


import cool.scx.annotation.ScxService;
import cool.scx.vo.Html;

@ScxService
public class IndexController {

    /**
     * 跳转至首页
     *
     * @return 首页
     */
//    @ScxMapping(value = "/", returnType = ReturnType.HTML, httpMethod = {HttpMethod.POST,HttpMethod.GET})
    public Html Index(Long name, String age) {
        return new Html("index");
    }

}
