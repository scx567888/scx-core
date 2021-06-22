package cool.scx.bo;

/**
 * 分页参数
 *
 * @author scx56
 * @version $Id: $Id
 */
public class Pagination {

    /**
     * 分页 页码
     */
    private int page = 0;

    /**
     * 分页 每页数量
     */
    private int limit = 0;

    /**
     * <p>Constructor for Pagination.</p>
     */
    public Pagination() {

    }

    /**
     * <p>Constructor for Pagination.</p>
     *
     * @param page  a int
     * @param limit a int
     */
    public Pagination(int page, int limit) {
        set(page, limit);
    }

    /**
     * <p>Constructor for Pagination.</p>
     *
     * @param limit a int
     */
    public Pagination(int limit) {
        set(limit);
    }


    /**
     * 设置分页参数
     *
     * @param page  分页页码
     * @param limit 每页数量
     * @return p
     */
    public Pagination set(int page, int limit) {
        if (page >= 0 && limit >= 0) {
            this.page = page;
            this.limit = limit;
            return this;
        } else {
            throw new RuntimeException("分页参数错误!!!");
        }
    }

    /**
     * 设置分页 默认 第一页
     *
     * @param limit a {@link java.lang.Integer} object.
     * @return a 当前实例
     */
    public Pagination set(int limit) {
        if (limit >= 0) {
            this.page = 1;
            this.limit = limit;
            return this;
        } else {
            throw new RuntimeException("分页参数错误!!!");
        }
    }

    /**
     * 分页内容是否正确
     * <br>
     * 是否可用
     *
     * @return 是否正确
     */
    public boolean canUse() {
        return limit > 0 && page > 0;
    }

    /**
     * <p>page.</p>
     *
     * @return a int
     */
    public int page() {
        return page;
    }

    /**
     * <p>limit.</p>
     *
     * @return a int
     */
    public int limit() {
        return limit;
    }
}
