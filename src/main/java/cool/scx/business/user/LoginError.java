package cool.scx.business.user;

import java.time.LocalDateTime;

/**
 * 用户登录时的校验类 包含最后一次登录错误时间 和 错误次数
 */
class LoginError {
    LocalDateTime lastErrorDate;
    Integer errorTimes;

    public LoginError(LocalDateTime lastErrorDate, Integer errorTimes) {
        this.lastErrorDate = lastErrorDate;
        this.errorTimes = errorTimes;
    }
}
