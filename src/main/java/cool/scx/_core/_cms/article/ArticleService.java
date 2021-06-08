package cool.scx._core._cms.article;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Param;

import java.util.ArrayList;
import java.util.Map;

/**
 * ArticleService
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
public class ArticleService extends BaseService<Article> {

    /**
     * <p>testListAll.</p>
     *
     * @param params a {@link java.util.Map} object
     * @return a {@link java.lang.Object} object
     */
    public Object testListAll(Map<String, Object> params) {
        var author = params.get("author") != null ? params.get("author").toString() : "";
        var p = new Param<>(new Article());
        Integer count = count(p);
        if (count < 20) {
            var l = new ArrayList<Article>();
            for (int i = 0; i < 50; i++) {
                var a = new Article();
                a.columnId = i;
                a.articleTitle = author + i;
                a.articleContent = "文章内容" + i;
                l.add(a);
            }
            saveList(l);
        }
        return listAll();
    }
}
