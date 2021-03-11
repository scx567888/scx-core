package cool.scx.util;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * <p>ByteUtils class.</p>
 *
 * @author scx56
 * @version $Id: $Id
 */
public class ByteUtils {

    /**
     * <p>getSplitList.</p>
     *
     * @param splitNum a int.
     * @param list     an array of {@link byte} objects.
     * @return a {@link java.util.List} object.
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
