package cool.scx.bo;

/**
 * 分页参数
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

    public Pagination() {

    }

    public Pagination(int page, int limit) {
        set(page, limit);
    }

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

    public int page() {
        return page;
    }

    public int limit() {
        return limit;
    }
}
