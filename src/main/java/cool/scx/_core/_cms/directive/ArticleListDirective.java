package cool.scx._core._cms.directive;


import cool.scx._core._cms.article.ArticleService;
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
public class ArticleListDirective implements BaseTemplateDirective {


    private final ArticleService articleService;

    /**
     * a
     *
     * @param articleService a
     */
    public ArticleListDirective(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * {@inheritDoc}
     * <p>
     * getResults
     */
    @Override
    public Object getResults(Map<String, Object> params) {
        return articleService.testListAll(params);
    }

    /**
     * {@inheritDoc}
     * <p>
     * getResults
     */
    @Override
    public String _DirectiveName() {
        return "article_list_tag";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String _VariableName() {
        return "article_list";
    }
}
