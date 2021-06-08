package cool.scx._core._auth.auth;

import cool.scx.exception.AuthException;

/**
 * 登录错误次数多过异常
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class TooManyErrorsException extends AuthException {

    public long remainingTime;

    /**
     * 构造函数 设置剩余错误次数
     *
     * @param remainingTime a long.
     */
    public TooManyErrorsException(long remainingTime) {
        this.remainingTime = remainingTime;
    }

}
