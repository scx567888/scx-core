package cool.scx.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * 处理字节工具类
 *
 * @author 司昌旭
 * @version 1.0.10
 */
public final class ByteUtils {

    /**
     * 根据指定的分段大小 将数组切割
     * 如 原数组 [1,2,3,4,5,6,7,8,9,10,11] 分段大小 3 切割后 [ [1,2,3],[4,5,6],[7,8,9],[10,11] ]
     *
     * @param splitNum 切割的大小
     * @param list     待切割的数组
     * @return a 切割后的list
     */
    public static List<byte[]> getSplitList(int splitNum, byte[] list) {
        List<byte[]> splitList = new LinkedList<>();
        int groupFlag = list.length % splitNum == 0 ? (list.length / splitNum) : (list.length / splitNum + 1);
        for (int j = 1; j <= groupFlag; j++) {
            if ((j * splitNum) <= list.length) {
                var newArray = Arrays.copyOfRange(list, j * splitNum - splitNum, j * splitNum);
                splitList.add(newArray);
            } else if ((j * splitNum) > list.length) {
                var newArray = Arrays.copyOfRange(list, j * splitNum - splitNum, list.length);
                splitList.add(newArray);
            }
        }
        return splitList;
    }
}
