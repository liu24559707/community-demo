import cn.hutool.crypto.digest.MD5;
import com.liu.utils.MD5Util;

public class testMD5 {
    public static void main(String[] args) {
        String encrypt = MD5Util.encrypt("123456");
        System.out.println(encrypt);
    }
}
