package cool.scx._core.uploadfile;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Param;
import cool.scx.enumeration.SortType;

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
     * @param fileMD5 a {@link String} object.
     * @return a {@link UploadFile} object.
     */
    public UploadFile findFileByMd5(String fileMD5) {
        var p = new Param<>(new UploadFile()).addOrderBy("uploadTime", SortType.DESC);
        p.queryObject.fileMD5 = fileMD5;
        return get(p);
    }

}
