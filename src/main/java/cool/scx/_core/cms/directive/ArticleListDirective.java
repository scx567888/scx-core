package cool.scx._core.cms.directive;


import cool.scx._core.cms.ArticleService;
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
public class ArticleListDirective extends BaseTemplateDirective {


    private final ArticleService articleService;

    /**
     * <p>Constructor for ArticleListDirective.</p>
     *
     * @param articleService a {@link cool.scx._core.cms.ArticleService} object.
     */
    public ArticleListDirective(ArticleService articleService) {
        this.articleService = articleService;
    }

    /** getResults */
    @Override
    public Object getResults(Map<String, Object> params) {
        return articleService.listAll();
    }

    /** getResults */
    @Override
    public String directiveName() {
        return "article_list_tag";
    }

    /** {@inheritDoc} */
    @Override
    public String variableName() {
        return "article_list";
    }
}
