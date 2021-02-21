package cool.scx.boot;

import java.util.ArrayList;

class SessionItem {
    public String token;//唯一的
    public String username;//唯一的
    public ArrayList<Object> userSessionList;

    public SessionItem(String _token, String _username, ArrayList<Object> _userSessionList) {
        token = _token;
        username = _username;
        userSessionList = _userSessionList;
    }
}
