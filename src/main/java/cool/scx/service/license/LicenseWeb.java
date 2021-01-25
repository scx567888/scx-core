package cool.scx.service.license;

import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.HttpMethod;
import org.springframework.beans.factory.annotation.Autowired;

@ScxMapping("/license")
public class LicenseWeb {

    @Autowired
    private LicenseService licenseService;

    /**
     * 跳转至密钥生成页面
     *
     * @return 密钥生成页面
     */

    @ScxMapping(value = "index", httpMethod = HttpMethod.GET)
    public String goLicense() {
        return getHtml("");
    }

    public String getHtml(String s) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-cn\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
                "<head>\n" +
                "    <meta charset=\"utf-8\">\n" +
                "    <title>生成license</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<h3>生成密钥</h3>\n" +
                "<form action=\"/license/make\" method=\"post\">\n" +
                "    超级管理员密码\n" +
                "    <label>\n" +
                "        <input name=\"adminPassword\" type=\"password\">\n" +
                "    </label>\n" +
                "    截至日期\n" +
                "    <label>\n" +
                "        <input name=\"stopTime\" type=\"datetime-local\">\n" +
                "    </label>\n" +
                "    <input type=\"submit\" value=\"生成密钥\">\n" +
                "</form>\n" +
                "<h3>恢复项目运行</h3>\n" +
                "<form action=\"/license/recovery\" method=\"post\">\n" +
                "    恢复管理员密码\n" +
                "    <label>\n" +
                "        <input name=\"adminPassword\" type=\"password\">\n" +
                "    </label>\n" +
                "    <input type=\"submit\" value=\"恢复项目运行\">\n" +
                "</form>\n" +
                "<h1 th:text=\"${license}\">" + s + "</h1>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * 生成密钥
     *
     * @return 密钥生成页面
     */
    @ScxMapping("/make")
    public String makeLicense(String adminPassword, String stopTime) {
        var license = "超级管理员密码错误";
        if (("scx567888").equals(adminPassword)) {
            license = licenseService.encryptionTime(stopTime.split("T")[0]);
        }

        return getHtml(license);
    }

    /**
     * 恢复 License
     *
     * @return 密钥生成页面
     */
    @ScxMapping("/recovery")
    public String recoveryLicense(String adminPassword) {
        var myLicense = new License();
        var license = "超级管理员密码错误";
        if (("q1w2e3r4t5").equals(adminPassword)) {
            myLicense.id = 1L;
            myLicense.flag = true;
            myLicense.lastTime = "2000-01-01";
            licenseService.updateById(myLicense);
            license = "密钥恢复完成现在可以正常使用";
        }
        return getHtml(license);
    }
}
