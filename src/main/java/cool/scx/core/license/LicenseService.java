package cool.scx.core.license;

import cool.scx.annotation.ScxService;
import cool.scx.base.BaseService;
import cool.scx.config.ScxConfig;
import cool.scx.core.system.ScxLogService;
import cool.scx.util.CryptoUtils;
import cool.scx.util.LogUtils;

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

    private final ScxLogService scxLogService;

    /**
     * <p>Constructor for LicenseService.</p>
     *
     * @param scxLogService a {@link cool.scx.core.system.ScxLogService} object.
     */
    public LicenseService(ScxLogService scxLogService) {
        this.scxLogService = scxLogService;
    }


    /**
     * 加密时间
     *
     * @param endDate 截至时间
     * @return 加密后的字符串
     */
    public String encryptionTime(String endDate) {
        var encrypt = "";
        try {
            encrypt = CryptoUtils.encryptText(endDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encrypt;
    }

    /**
     * <p>passLicense.</p>
     *
     * @return a boolean.
     */
    public boolean passLicense() {
        var myLicense = getById(1L);

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
        var date = decryptTime(ScxConfig.license());
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
     * 将加密后的乱码解密
     *
     * @param license 密钥
     */
    private Date decryptTime(String license) {
        var format = new SimpleDateFormat("yyyy-MM-dd");
        String decrypt;
        try {
            decrypt = CryptoUtils.decryptText(license);
            return format.parse(decrypt);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.recordLog("License 有误 :" + license);
        }
        return null;
    }

}
