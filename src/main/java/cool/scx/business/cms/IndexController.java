package cool.scx.business.cms;


import cool.scx.annotation.ScxController;
import cool.scx.vo.Html;

/**
 * <p>IndexController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController
public class IndexController {


    /**
     * 跳转至首页 测试
     *
     * @param name 测试参数
     * @param age  测试参数
     * @return 页面
     */
//    @ScxMapping(value = "/", returnType = ReturnType.HTML, httpMethod = {HttpMethod.POST,HttpMethod.GET})
    public Html Index(Long name, String age) {
        return new Html("index");
    }

}
