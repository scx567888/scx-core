package cool.scx.util;

import java.util.ArrayList;
import java.util.List;

public class DsfCycle {

    /**
     * 限制node最大数
     */
    private static final int MAX_NODE_COUNT = 100;

    /**
     * node集合
     */
    private static final List<String> nodes = new ArrayList<>();

    /**
     * 有向图的邻接矩阵
     */
    private static final int[][] adjacencyMatrix = new int[MAX_NODE_COUNT][MAX_NODE_COUNT];

    /**
     * @param nodeName
     * @return
     * @Title addNode
     * @Description 添加节点
     * @date 2018年5月17日
     */
    private static int addNode(String nodeName) {
        if (!nodes.contains(nodeName)) {
            if (nodes.size() >= MAX_NODE_COUNT) {
                System.out.println("nodes超长:" + nodeName);
                return -1;
            }
            nodes.add(nodeName);
            return nodes.size() - 1;
        }
        return nodes.indexOf(nodeName);
    }

    /**
     * @param startNode
     * @param endNode
     * @Title addLine
     * @Description 添加线，初始化邻接矩阵
     * @date 2018年5月17日
     */
    public static void addLine(String startNode, String endNode) {
        int startIndex = addNode(startNode);
        int endIndex = addNode(endNode);
        if (startIndex >= 0 && endIndex >= 0) {
            adjacencyMatrix[startIndex][endIndex] = 1;
        }
    }

    /**
     * @return
     * @Title find
     * @Description 寻找闭环
     * @date 2018年5月17日
     */
    public static List<String> find() {
        // 从出发节点到当前节点的轨迹
        List<Integer> trace = new ArrayList<Integer>();
        //返回值
        List<String> reslut = new ArrayList<>();
        findCycle(0, trace, reslut);
        if (reslut.size() == 0) {
            reslut.add("no cycle!");
        }
        return reslut;
    }

    /**
     * @param v
     * @param trace
     * @param reslut
     * @Title findCycle
     * @Description dfs
     * @date 2018年5月17日
     */
    private static void findCycle(int v, List<Integer> trace, List<String> reslut) {
        int j = trace.indexOf(v);
        //添加闭环信息
        if (j != -1) {
            StringBuilder sb = new StringBuilder();
            String startNode = nodes.get(trace.get(j));
            while (j < trace.size()) {
                sb.append(nodes.get(trace.get(j))).append("-");
                j++;
            }
            reslut.add("cycle:" + sb.toString() + startNode);
            return;
        }
        trace.add(v);
        for (int i = 0; i < nodes.size(); i++) {
            if (adjacencyMatrix[v][i] == 1) {
                findCycle(i, trace, reslut);
            }
        }
        trace.remove(trace.size() - 1);
    }

    //测试
    public static void main(String[] args) {
        DsfCycle.addLine("cool.scx.business.license.LicenseController", "cool.scx.business.license.LicenseService");
//        DsfCycle.addLine("cool.scx.business.user.UserController","cool.scx.business.user.UserService");
//        DsfCycle.addLine("cool.scx.business.user.UserController","cool.scx.business.system.ScxLogService");
//        DsfCycle.addLine("cool.scx.business.user.UserController","cool.scx.business.license.LicenseService");
//        DsfCycle.addLine("cool.scx.business.user.UserController","cool.scx.business.dept.DeptService");
//        DsfCycle.addLine("cool.scx.business.user.UserController","cool.scx.business.role.RoleService");
//        DsfCycle.addLine("cool.scx.business.user.UserController","cool.scx.business.role.UserRoleService");
//        DsfCycle.addLine("cool.scx.business.user.UserController","cool.scx.business.dept.UserDeptService");
//        DsfCycle.addLine("cool.scx.business.uploadfile.UploadController","cool.scx.business.uploadfile.UploadFileService");
        DsfCycle.addLine("cool.scx.business.role.UserRoleService", "cool.scx.business.user.UserService");
//        DsfCycle.addLine("cool.scx.base.BaseController","cool.scx.business.system.ScxLogService");
//        DsfCycle.addLine("cool.scx.base.BaseController","cool.scx.business.user.UserService");
//        DsfCycle.addLine("cool.scx.base.BaseController","cool.scx.business.uploadfile.UploadFileService");
//        DsfCycle.addLine("cool.scx.business.role.RoleController","cool.scx.business.role.RoleService");
//        DsfCycle.addLine("cool.scx.business.dept.DeptController","cool.scx.business.dept.DeptService");
//        DsfCycle.addLine("cool.scx.business.license.LicenseService","cool.scx.business.system.ScxLogService");
//        DsfCycle.addLine("cool.scx.business.user.UserService","cool.scx.business.dept.DeptService");
//        DsfCycle.addLine("cool.scx.business.user.UserService","cool.scx.business.role.RoleService");
//        DsfCycle.addLine("cool.scx.business.user.UserService","cool.scx.business.dept.UserDeptService");
        DsfCycle.addLine("cool.scx.business.user.UserService", "cool.scx.business.role.UserRoleService");
//        DsfCycle.addLine("cool.scx.business.dept.DeptService","cool.scx.business.dept.UserDeptService");
//        DsfCycle.addLine("cool.scx.business.role.RoleService","cool.scx.business.role.UserRoleService");


        List<String> reslut = DsfCycle.find();
        for (String string : reslut) {
            System.out.println(string);
        }
    }
}
