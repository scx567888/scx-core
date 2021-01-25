package cool.scx.business.user.exception;

/**
 * 登录错误次数过多
 */
public class TooManyErrorsException extends AuthException {
    public Long remainingTime;

    public TooManyErrorsException(Long remainingTime) {
        this.remainingTime = remainingTime;
    }
}
