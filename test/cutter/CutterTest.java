package cutter;

import cutter.CutieCutter;
import cutter.CutterAdapter;

public class CutterTest {

    public static void main(String[] args) {
        final CutterAdapter cutter = new CutieCutter();
        cutter.setClipboardContent(String.format("早上好？%n下午好！"));

        final String test = cutter.getClipboardContent();
        System.out.println(test);
    }

}
