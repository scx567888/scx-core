package cool.scx._core._auth.auth;

import java.time.LocalDateTime;

/**
 * 用户登录时的校验类 包含最后一次登录错误时间 和 错误次数
 */
class LoginError {

    final LocalDateTime lastErrorDate;

    final Integer errorTimes;

    /**
     * <p>Constructor for LoginError.</p>
     *
     * @param lastErrorDate a {@link java.time.LocalDateTime} object.
     * @param errorTimes    a {@link java.lang.Integer} object.
     */
    LoginError(LocalDateTime lastErrorDate, Integer errorTimes) {
        this.lastErrorDate = lastErrorDate;
        this.errorTimes = errorTimes;
    }

}
