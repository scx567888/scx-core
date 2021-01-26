package cool.scx.business.user.exception;

public class TooManyErrorsException extends AuthException {
    public long remainingTime;

    public TooManyErrorsException(long remainingTime) {
        this.remainingTime = remainingTime;
    }
}
