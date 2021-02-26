package cool.scx.context;

class LoginItem {
    public String token;//唯一的
    public String username;//唯一的

    /**
     * <p>Constructor for SessionItem.</p>
     *
     * @param _token    a {@link java.lang.String} object.
     * @param _username a {@link java.lang.String} object.
     */
    public LoginItem(String _token, String _username) {
        token = _token;
        username = _username;
    }
}
