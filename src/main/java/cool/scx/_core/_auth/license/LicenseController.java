package cool.scx._core._auth.license;

import cool.scx._core._auth.AuthModuleOption;
import cool.scx.annotation.ScxMapping;
import cool.scx.enumeration.Method;
import cool.scx.util.CryptoUtils;
import cool.scx.vo.Html;

/**
 * <p>LicenseController class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxMapping("/license")
public class LicenseController {

    private final LicenseService licenseService;

    /**
     * <p>Constructor for LicenseController.</p>
     *
     * @param licenseService a {@link cool.scx._core._auth.license.LicenseService} object.
     */
    public LicenseController(LicenseService licenseService) {
        this.licenseService = licenseService;
    }

    /**
     * 加密时间
     *
     * @param endDate 截至时间
     * @return 加密后的字符串
     */
    private static String encryptionTime(String endDate) {
        var encrypt = "";
        try {
            encrypt = CryptoUtils.encryptText(endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypt;
    }

    /**
     * 跳转至密钥生成页面
     *
     * @return a {@link cool.scx.vo.Html} object
     */

    @ScxMapping(value = "index", method = Method.GET)
    public Html goLicense() {
        return Html.ofString(getHtml(""));
    }

    /**
     * <p>getHtml.</p>
     *
     * @param s a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
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
     * @param adminPassword adminPassword
     * @param stopTime      stopTime
     * @return a {@link cool.scx.vo.Html} object
     */
    @ScxMapping(value = "/make", method = Method.POST)
    public Html makeLicense(String adminPassword, String stopTime) {
        var license = "超级管理员密码错误";
        if (AuthModuleOption.licenseAdminPassword().equals(adminPassword)) {
            license = encryptionTime(stopTime.split("T")[0]);
        }
        return Html.ofString(getHtml(license));
    }

    /**
     * 恢复 License
     *
     * @param adminPassword adminPassword
     * @return a {@link cool.scx.vo.Html} object
     */
    @ScxMapping(value = "/recovery", method = Method.POST)
    public Html recoveryLicense(String adminPassword) {
        var myLicense = new License();
        var license = "超级管理员密码错误";
        if (AuthModuleOption.licenseAdminPassword().equals(adminPassword)) {
            myLicense.id = 1L;
            myLicense.flag = true;
            myLicense.lastTime = "2000-01-01";
            licenseService.update(myLicense);
            license = "密钥恢复完成现在可以正常使用";
        }
        return Html.ofString(getHtml(license));
    }

}
