package cool.scx.service.system;

import cool.scx.service.user.User;

import java.util.ArrayList;
import java.util.List;

public class IMService {


    private static final List<UserSocket> userSocketList = new ArrayList<>();

//    public static synchronized void addUserSocket(User user, Session session) {
//        UserSocket userSocket = new UserSocket();
//        userSocket.session = session;
//        userSocket.user = user;
//        userSocketList.add(userSocket);
////        System.out.println("id 为" + user.id + "的用户已加入");
//    }
//
//    public static synchronized void removeUserSocket(User user, Session session) {
//        UserSocket userSocket = new UserSocket();
//        userSocket.session = session;
//        userSocket.user = user;
//        userSocketList.remove(userSocket);
//    }

    /**
     * 将消息发送给所有用户
     *
     * @param message 发送的消息
     */
    public static void sendToAllUsers(Object message) {
//        var objectMapper = new ObjectMapper();
//        System.out.println(userSocketList.size());
//        userSocketList.forEach(userSocket -> {
//            try {
//                userSocket.session.getAsyncRemote().sendText(objectMapper.writeValueAsString(message));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        });
    }

    /**
     * 将消息发送给指定用户
     *
     * @param userIds userIds
     */
    public static void sendByUserIds(List<Long> userIds, Object message) {
//        var objectMapper = new ObjectMapper();
//        String jsonStr = null;
//        try {
//            jsonStr = objectMapper.writeValueAsString(message);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//        String finalJsonStr = jsonStr;
//        //todo 此处待优化
//        userSocketList.stream().filter(userSocket ->
//                {
//                    if (userSocket != null && userSocket.user != null) {
//                        return userIds.contains(userSocket.user.id);
//                    } else {
//                        return false;
//                    }
//                }
//        ).forEach(userSocket ->
//                {
//                    try {
//                        userSocket.session.getAsyncRemote().sendText(finalJsonStr);
//                    } catch (Exception e) {
//                        //连接不上就删掉
////                        e.printStackTrace();
//                    }
//                }
//
//        );
    }

    //    将消息发送给拥有角色的用户
    public static void sendByRolesIds(ArrayList<Integer> rolesIds) {

    }

    //    将消息发送给拥有部门的用户
    public static void sendByDeptIds(ArrayList<Integer> DeptIds) {

    }

    public static class UserSocket {

        public User user;

        //public Session session;
    }
}
