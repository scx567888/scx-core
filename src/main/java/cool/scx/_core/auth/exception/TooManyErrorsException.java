package cool.scx._core.auth.exception;

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
     * <p>Constructor for TooManyErrorsException.</p>
     *
     * @param remainingTime a long.
     */
    public TooManyErrorsException(long remainingTime) {
        this.remainingTime = remainingTime;
    }
}
