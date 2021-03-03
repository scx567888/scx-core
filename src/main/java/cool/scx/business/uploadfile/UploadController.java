package cool.scx.business.uploadfile;

import cool.scx.web.annotation.ScxController;
import cool.scx.web.annotation.ScxMapping;
import cool.scx.service.Param;
import cool.scx.config.ScxConfig;
import cool.scx.util.FileUtils;
import cool.scx.util.StringUtils;
import cool.scx.web.vo.Json;

import java.util.Map;

/**
 * <p>UploadController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController
public class UploadController {

    final UploadFileService uploadFileService;

    /**
     * <p>Constructor for UploadController.</p>
     *
     * @param uploadFileService a {@link cool.scx.business.uploadfile.UploadFileService} object.
     */
    public UploadController(UploadFileService uploadFileService) {
        this.uploadFileService = uploadFileService;
    }

    /**
     * <p>deleteFile.</p>
     *
     * @param id a {@link java.lang.Long} object.
     * @return a {@link cool.scx.web.vo.Json} object.
     */
    @ScxMapping
    public Json deleteFile(Long id) {
        var result = true;
        UploadFile uploadFile = uploadFileService.getById(id);
        if (uploadFile != null) {
            var b = FileUtils.deleteFileByPath(ScxConfig.uploadFilePath() + uploadFile.filePath);
            //当文件成功删除后在删除 数据库记录
            result = b && uploadFileService.deleteByIds(id) == 1;
        }
        return result ? Json.ok("success") : Json.ok("error");
    }

    /**
     * <p>listFile.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.web.vo.Json} object.
     */
    @ScxMapping()
    public Json listFile(Map<String, Object> params) {
        var param = new Param<>(new UploadFile());
        var fileIds = params.get("fileIds");
        if (StringUtils.isNotEmpty(fileIds)) {
            param.whereSql = " id in (" + String.join(",", fileIds.toString().split(",")) + ")";
        } else {
            param.whereSql = " id = -1";
        }
        return Json.ok().items(uploadFileService.list(param));
    }
}
