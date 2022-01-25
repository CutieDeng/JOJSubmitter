package cutter;

import codeparse.BlockParser;
import codeparse.Parser;
import codeparse.TokenRecord;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Paths;

public class Cutter2Test {
    public static void main(String[] args) throws FileNotFoundException {
        Parser parser = new BlockParser(new FileInputStream(Paths.get("test", "fortest", "HelloWorld.java").toFile()));

        while (parser.hasNextToken()) {
            final TokenRecord tokenRecord = parser.nextToken();
            System.out.printf("|%s", tokenRecord.content().get());
        }
    }
}
