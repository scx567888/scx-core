package cool.scx.business.cms;

import cool.scx.annotation.ScxController;
import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.HttpMethod;
import cool.scx.boot.ScxConfig;
import cool.scx.util.FileUtils;
import cool.scx.vo.Json;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;


@ScxController("template")
public class TemplateController {

    @ScxMapping(httpMethod = {HttpMethod.GET, HttpMethod.POST})
    public Json Index() throws IOException {
        var fileList = FileUtils.getFileList(ScxConfig.cmsRoot.getPath());
        var collect = fileList.stream().filter(fileInfo -> "Directory".equals(fileInfo.type)).collect(Collectors.toList());
        var collect1 = fileList.stream().filter(fileInfo -> "File".equals(fileInfo.type)).collect(Collectors.toList());
        collect.addAll(collect1);
//        让文件夹永远在前边
        return Json.ok().data("cmsRootTreeList", collect);
    }

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

    @ScxMapping("setFileContent")
    public Json setFileContent(Map<String, String> params) {
        String filePath = params.get("filePath");
        String fileContent = params.get("fileContent");
//        注意 向文件写入字符串请用此方法 不要用 nio nio 有bug
        FileUtils.setFileContent(filePath, fileContent);
        return getFileContent(params);
    }


    @ScxMapping("file/delete")
    public Json delete(Map<String, String> params) throws IOException {
        String filePath = params.get("filePath");
        var file = Paths.get(filePath);
        FileUtils.deleteIfExists(file);
        return Json.ok();
    }

    @ScxMapping("upload")
    public Json upload(File file, String filePath) throws IOException {
        //filePath = filePath + "\\" + file.getOriginalFilename();
        //FileUtils.fileAppend(filePath, file.getBytes());
        return Json.ok();
    }

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
