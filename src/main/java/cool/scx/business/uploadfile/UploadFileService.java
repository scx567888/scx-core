package cool.scx.business.uploadfile;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.bo.Param;

/**
 * <p>UploadFileService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
public class UploadFileService extends BaseService<UploadFile> {
    public UploadFile findFileByMd5(String fileMD5) {
        var p = new Param<>(new UploadFile());
        p.queryObject.fileMD5 = fileMD5;
        return get(p);
    }
}
