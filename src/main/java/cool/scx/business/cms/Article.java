package cool.scx.business.cms;

import cool.scx.dao.BaseModel;
import cool.scx.dao.annotation.Column;
import cool.scx.dao.annotation.ScxModel;

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
