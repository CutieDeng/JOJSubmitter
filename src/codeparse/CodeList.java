package codeparse;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class CodeList {
    private List<TokenRecord> sentences = new ArrayList<>();
    private BlockParser parser;

    public CodeList(InputStream stream) {
        parser = new BlockParser(stream);
    }

    public CodeList(Reader reader) {
        parser = new BlockParser(reader);
    }

    public CodeList(BufferedReader reader) {
        parser = new BlockParser(reader);
    }

    public void init() {
        while (parser.hasNextToken()) {
            sentences.add(parser.nextToken());
        }
        parser.transfer("private", "public");
        parser.transfer("protected", "public");
    }

    public List<TokenRecord> getList() {
        return this.sentences;
    }
}
