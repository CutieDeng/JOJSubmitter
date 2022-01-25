package cutter;

import codeparse.CodeList;
import codeparse.TokenRecord;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class Cutter3Test {
    public static void main(String[] args) throws FileNotFoundException {
        final CodeList list = new CodeList(new FileInputStream(Paths.get("test", "fortest", "Hello2.java").toFile()));
        list.init();
        final String codes = list.getList().stream().map(TokenRecord::content).map(AtomicReference::get).collect(Collectors.joining("|", "|", ""));
        System.out.println(codes);
    }
}