import java.util.function.Function;

public class PMMDTest {
    public static void main(String[] args) {
        int x = 1, y = 2, z = 3;
        // 宽情形字符串切割
        x = y * y; y = x + z; z = y +++ x; z -= y % x;
        // 窄情形字符串切割
        x=y+3-z/x%0x21;x-=x-=x+=x*=x/=x%=x|=x&=x^=x;boolean boolean_=(x|y&z^0xFF)!=0;
        /** 复杂代码块注释测试
         * int a = 6; int b = 4; int c = 2; String x = "Hello World!"; System.out.println("print" + x); return 0;
         */ // 函数运算描述符 狭窄情形描述 及 空格杂音测试
        Function<Object, Object>function=o->o; var r=function.andThen(function)  .andThen(function).  andThen( function)
                .apply(function);

    }
}
