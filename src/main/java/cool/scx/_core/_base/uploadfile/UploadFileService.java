package cool.scx._core._base.uploadfile;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.QueryParam;
import cool.scx.enumeration.OrderByType;
import cool.scx.enumeration.WhereType;

/**
 * UploadFileService
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
public class UploadFileService extends BaseService<UploadFile> {

    /**
     * 根据 md5 查找文件
     *
     * @param fileMD5 md5 值
     * @return 找的的数据
     */
    public UploadFile findFileByMd5(String fileMD5) {
        var p = new QueryParam()
                .addOrderBy("uploadTime", OrderByType.DESC)
                .addWhere("fileMD5", WhereType.EQUAL, fileMD5);
        return get(p);
    }

}
