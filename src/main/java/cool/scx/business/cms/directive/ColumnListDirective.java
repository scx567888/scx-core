package cool.scx.business.cms.directive;


import cool.scx.base.service.BaseTemplateDirective;
import cool.scx.business.cms.ColumnService;

import java.util.Map;

/**
 * 自定义标签测试
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class ColumnListDirective implements BaseTemplateDirective {

    ColumnService columnService = new ColumnService();

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public Object getParams(Map params) {
        return columnService.listAll();
    }

    /**
     * {@inheritDoc}
     * <p>
     * 获取自定义 标签名称
     */
    @Override
    public String getVariable() {
        return "column_list";
    }

}
