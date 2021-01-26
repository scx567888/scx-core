package cool.scx.business.cms.directive;


import cool.scx.base.FreemarkerSupperTag;
import cool.scx.business.cms.ColumnService;

import java.util.Map;

/**
 * 自定义标签测试
 */
public class ColumnListDirective implements FreemarkerSupperTag {

    ColumnService columnService = new ColumnService();

    @Override
    public Object getParams(Map params) {
        return columnService.listMapAll();
    }

    @Override
    public String getVariable() {
        return "column_list";
    }

}
