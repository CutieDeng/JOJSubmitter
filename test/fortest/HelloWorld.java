package fortest;

import java.math.BigInteger;
import java.util.Arrays;

public class HelloWorld {

    /**
     * 该主方法负责实现一个特殊的加法程序，通过从 args 从获取所有的程序参数（约定为数字），将其求和并输出！
     *
     * args 的值为 152 448 6600
     *
     * @param args 待求和的数字描述
     */
    public static void main(String[] args) {
        final BigInteger result = Arrays.stream(args).map(BigInteger::new).reduce(BigInteger::add).orElseThrow(() -> new RuntimeException(String.format("args 不存在！")));
        System.out.println(result);
    }
}
