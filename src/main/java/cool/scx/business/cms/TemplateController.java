package cool.scx.business.cms;

import cool.scx.config.ScxConfig;
import cool.scx.util.FileUtils;
import cool.scx.web.annotation.ScxController;
import cool.scx.web.annotation.ScxMapping;
import cool.scx.web.type.RequestMethod;
import cool.scx.web.vo.Json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * <p>TemplateController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxController("template")
public class TemplateController {

    /**
     * <p>Index.</p>
     *
     * @return a {@link cool.scx.web.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    @ScxMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public Json Index() throws IOException {
        var fileList = FileUtils.getFileList(ScxConfig.cmsRoot().getPath());
        var collect = fileList.stream().filter(fileInfo -> "Directory".equals(fileInfo.type)).collect(Collectors.toList());
        var collect1 = fileList.stream().filter(fileInfo -> "File".equals(fileInfo.type)).collect(Collectors.toList());
        collect.addAll(collect1);
//        让文件夹永远在前边
        return Json.ok().data("cmsRootTreeList", collect);
    }

    /**
     * <p>getFileContent.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.web.vo.Json} object.
     */
    @ScxMapping("getFileContent")
    public Json getFileContent(Map<String, String> params) {
        String filePath = params.get("filePath");
        try {
            String fileContent = Files.readString(Paths.get(filePath));
            return Json.ok().data("fileContent", fileContent);
        } catch (Exception exception) {
            return Json.ok().data("fileContent", "此文件无法编辑");
        }
    }

    /**
     * <p>setFileContent.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.web.vo.Json} object.
     */
    @ScxMapping("setFileContent")
    public Json setFileContent(Map<String, String> params) {
        String filePath = params.get("filePath");
        String fileContent = params.get("fileContent");
//        注意 向文件写入字符串请用此方法 不要用 nio nio 有bug
        FileUtils.setFileContent(filePath, fileContent);
        return getFileContent(params);
    }


    /**
     * <p>delete.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.web.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    @ScxMapping("file/delete")
    public Json delete(Map<String, String> params) throws IOException {
        String filePath = params.get("filePath");
        var file = Paths.get(filePath);
        FileUtils.deleteIfExists(file);
        return Json.ok();
    }

    /**
     * <p>upload.</p>
     *
     * @param file     a {@link java.io.File} object.
     * @param filePath a {@link java.lang.String} object.
     * @return a {@link cool.scx.web.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    @ScxMapping(value = "upload", unCheckedPerms = true)
    public Json upload(File file, String filePath) throws IOException {
        //filePath = filePath + "\\" + file.getOriginalFilename();
        //FileUtils.fileAppend(filePath, file.getBytes());
        return Json.ok();
    }

    /**
     * <p>rename.</p>
     *
     * @param params a {@link java.util.Map} object.
     * @return a {@link cool.scx.web.vo.Json} object.
     * @throws java.io.IOException if any.
     */
    @ScxMapping("file/rename")
    public Json rename(Map<String, String> params) throws IOException {
        String newFilePath = params.get("newFilePath");
        String oldFilePath = params.get("oldFilePath");
        Path path = Paths.get(oldFilePath);
        String parent = path.getParent().toFile().getPath();
        path.toFile().renameTo(new File(parent + "\\" + newFilePath));
        return Json.ok();
    }

}
