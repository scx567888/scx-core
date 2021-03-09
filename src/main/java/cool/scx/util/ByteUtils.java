package cool.scx.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class ByteUtils {

    public static  List<byte[]> getSplitList(int splitNum, byte[] list) {
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
