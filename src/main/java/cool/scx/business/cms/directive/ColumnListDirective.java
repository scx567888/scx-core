package cool.scx.business.cms.directive;


import cool.scx.base.FreemarkerSupperTag;
import cool.scx.business.cms.ColumnService;

import java.util.Map;

/**
 * 自定义标签测试
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class ColumnListDirective implements FreemarkerSupperTag {

    ColumnService columnService = new ColumnService();

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getParams(Map params) {
        return columnService.listMapAll();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getVariable() {
        return "column_list";
    }

}
