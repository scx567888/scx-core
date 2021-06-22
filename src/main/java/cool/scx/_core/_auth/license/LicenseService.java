package cool.scx._core._auth.license;

import cool.scx._core._auth.AuthConfig;
import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.util.Ansi;
import cool.scx.util.CryptoUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * <p>LicenseService class.</p>
 *
 * @author 司昌旭
 * @version 0.3.6
 */
@ScxService
public class LicenseService extends BaseService<License> {

    /**
     * 将加密后的乱码解密
     *
     * @param license 密钥
     */
    private static Date decryptTime(String license) {
        var format = new SimpleDateFormat("yyyy-MM-dd");
        String decrypt;
        try {
            decrypt = CryptoUtils.decryptText(license);
            return format.parse(decrypt);
        } catch (Exception e) {
            e.printStackTrace();
            Ansi.OUT.print("License 有误 :" + license).ln();
        }
        return null;
    }

    /**
     * <p>passLicense.</p>
     *
     * @return a boolean.
     */
    public boolean passLicense() {
        var myLicense = get(1L);

        var now = new Date();
        var simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date lastTime;
        try {
            lastTime = simpleDateFormat.parse(myLicense.lastTime);
            now = simpleDateFormat.parse(simpleDateFormat.format(now));
        } catch (Exception e) {
            return false;
        }
        //如果当前时间 大于 license 时间 证明 license 已过期
        var date = decryptTime(AuthConfig.license());
        //如果密钥 不符合规则 直接 返回错误
        if (date == null) {
            myLicense.flag = false;
            update(myLicense);
            return false;
        } else if (date.getTime() < now.getTime()) {
            myLicense.flag = false;
            update(myLicense);
            return false;
        }
        //如果上一次正确的时间 大于 当前时间 证明改过系统时间
        else if (lastTime.getTime() > now.getTime()) {
            myLicense.flag = false;
            update(myLicense);
            return false;
        }
        //只要 数据库 lincense 的 flag  值  为 1 证明 已经过期 一次了
        else if (!myLicense.flag) {
            return false;
        } else {
            myLicense.flag = true;
            myLicense.lastTime = simpleDateFormat.format(now);
            update(myLicense);
            return true;
        }
    }

    /**
     * {@inheritDoc}
     */
    public void checkLicense() {
        Ansi.OUT.brightCyan("校验 license 中 ...").ln();
        var licenseRight = passLicense();
        if (!licenseRight) {
            Ansi.OUT.print("license 已失效!!! 请联系服务商...").ln();
        } else {
            Ansi.OUT.print("license 通过校验 ...").ln();
        }
    }
}
