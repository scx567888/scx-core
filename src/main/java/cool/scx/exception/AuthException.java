package cool.scx.exception;

/**
 * 认证异常
 *
 * @author 司昌旭
 * @version 0.3.6
 */
public class AuthException extends Exception {

    /**
     * {@inheritDoc}
     */
    @Override
    public String getMessage() {
        return "AuthException";
    }
}
