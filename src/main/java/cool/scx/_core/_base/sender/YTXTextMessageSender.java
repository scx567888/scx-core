package cool.scx._core._base.sender;

import cool.scx.annotation.ScxService;
import cool.scx.config.ScxConfig;
import cool.scx.message.BaseSender;
import cool.scx.util.CryptoUtils;
import cool.scx.util.DigestUtils;
import cool.scx.util.HttpUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 云通讯短信发送功能
 *
 * @author 司昌旭
 * @version 1.1.9
 */
@ScxService
public class YTXTextMessageSender implements BaseSender<List<String>, Map<String, Object>, String> {

    private final String YTX_BASE_URL = "https://app.cloopen.com:8883";
    private final String YTX_ACCOUNT_SID;
    private final String YTX_AUTH_TOKEN;
    private final String YTX_APP_ID;

    private final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    /**
     * <p>Constructor for YTXTextMessageSender.</p>
     */
    public YTXTextMessageSender() {
        YTX_ACCOUNT_SID = ScxConfig.get("core.base.ytx-account-sid");
        YTX_AUTH_TOKEN = ScxConfig.get("core.base.ytx-auth-token");
        YTX_APP_ID = ScxConfig.get("core.base.ytx-app-id");
    }

    /**
     * {@inheritDoc}
     * <p>
     * 向手机号发送短信
     */
    @Override
    public String send(List<String> address, Map<String, Object> message) {
        var timeStampStr = getTimeStampStr();
        var authorization = getAuthorization(timeStampStr);
        var sigParameter = getSigParameter(timeStampStr);

        var map = new HashMap<String, Object>();
        map.put("to", address.stream().collect(Collectors.joining(",", "", "")));
        map.put("appId", YTX_APP_ID);
        map.put("templateId", message.get("templateId"));
        map.put("datas", message.get("datas"));

        var header = new HashMap<String, String>();
        header.put("Authorization", authorization);
        var post = HttpUtils.post(getSendUrl(sigParameter), header, map);
        return post.body();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String senderName() {
        return "ytx";
    }

    private String getSendUrl(String sigParameter) {
        var s = "/2013-12-26/Accounts/" + YTX_ACCOUNT_SID + "/SMS/TemplateSMS?sig=" + sigParameter;
        return YTX_BASE_URL + s;
    }

    private String getSigParameter(String TimeStampStr) {
        return DigestUtils.md5(YTX_ACCOUNT_SID + YTX_AUTH_TOKEN + TimeStampStr);
    }

    private String getAuthorization(String TimeStampStr) {
        return CryptoUtils.encodeBase64(YTX_ACCOUNT_SID + ":" + TimeStampStr);
    }

    private String getTimeStampStr() {
        return LocalDateTime.now().format(DATETIME_FORMAT);
    }
}
