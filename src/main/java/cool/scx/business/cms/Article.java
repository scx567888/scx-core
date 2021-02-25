package cool.scx.business.cms;

import cool.scx.annotation.dao.Column;
import cool.scx.annotation.dao.ScxModel;
import cool.scx.base.dao.BaseModel;

/**
 * 文章类
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxModel(tablePrefix = "cms")
public class Article extends BaseModel {

    public String articleTitle;//文章标题

    @Column(type = "TEXT")
    public String articleContent;//文章内容

    @Column(notNull = true)
    public Integer columnId;//对应的 栏目 id
}
