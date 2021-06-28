package cool.scx._module.cms;

import cool.scx.annotation.ScxTemplateDirective;
import cool.scx.base.BaseTemplateDirective;

import java.util.Map;

/**
 * 自定义标签测试
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxTemplateDirective
public class ColumnListDirective implements BaseTemplateDirective {

    private final ColumnService columnService;

    /**
     * <p>Constructor for ColumnListDirective.</p>
     *
     * @param columnService a {@link cool.scx._module.cms.ColumnService} object.
     */
    public ColumnListDirective(ColumnService columnService) {
        this.columnService = columnService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * 重写方法
     */
    @Override
    public Object getResults(Map<String, Object> params) {
        return columnService.list();
    }

    /**
     * {@inheritDoc}
     * <p>
     * directiveName
     */
    @Override
    public String _DirectiveName() {
        return "column_list_tag";
    }

    /**
     * {@inheritDoc}
     * <p>
     * directiveName
     */
    @Override
    public String _VariableName() {
        return "column_list";
    }

}
