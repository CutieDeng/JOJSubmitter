package dsaa.lab01.dsaa.lab02;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * 阿里巴巴走进了装满宝藏的藏宝洞。藏宝洞里面有 N(N≤100) 堆金币，第 i 堆金币的总重量和总价值分别是 mi,vi(1 ≤ mi, vi ≤ 100)
 * 阿里巴巴有一个承重量为 T(T≤1000) 的背包，但并不一定有办法将全部的金币都装进去。
 * 他想装走尽可能多价值的金币。所有金币都可以随意分割，分割完的金币重量价值比（也就是单位价格）不变。请问阿里巴巴最多可以拿走多少价值的金币？
 */
public class CoinInBackpack {
    private static int n;

    public static void main(String[] args) {
        final Scanner input = new Scanner(System.in);
        n = input.nextInt();

        int leastRoom = input.nextInt();

        List<Double> massList = new ArrayList<>(n);
        List<Double> valueList = new ArrayList<>(n);

        for (int i = 0; i < n; i++) {
            massList.add(input.nextDouble());
            valueList.add(input.nextDouble());
        }

        final List<Integer> collect = IntStream.range(0, n).boxed().sorted((o1, o2) -> {
            double less = valueList.get(o1) / massList.get(o1) -
                    valueList.get(o2) / massList.get(o2);
            less = -less;
            if (less < 0)
                return -1;
            else if (less > 0)
                return 1;
            else
                return 0;
        }).collect(Collectors.toList());

        double ans = 0;


        for (int i = 0; i < n && leastRoom > 0; ++i) {
            if (leastRoom >= massList.get(collect.get(i))) {
                leastRoom -= massList.get(collect.get(i));
                ans += valueList.get(collect.get(i));
            } else {
                ans += valueList.get(collect.get(i)) / massList.get(collect.get(i)) * leastRoom;
                leastRoom = 0;
            }
        }

        System.out.printf("%.2f%n", ans);
    }
}
