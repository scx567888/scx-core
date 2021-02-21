package cool.scx.boot;

import java.util.ArrayList;

class SessionItem {
    public String token;//唯一的
    public String username;//唯一的
    public ArrayList<Object> userSessionList;

    /**
     * <p>Constructor for SessionItem.</p>
     *
     * @param _token           a {@link java.lang.String} object.
     * @param _username        a {@link java.lang.String} object.
     * @param _userSessionList a {@link java.util.ArrayList} object.
     */
    public SessionItem(String _token, String _username, ArrayList<Object> _userSessionList) {
        token = _token;
        username = _username;
        userSessionList = _userSessionList;
    }
}
