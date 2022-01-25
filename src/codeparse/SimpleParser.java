package codeparse;

import java.io.*;
import java.nio.file.Paths;
import java.util.Objects;

public class SimpleParser {
    private BufferedReader reader;

    public SimpleParser(InputStream input) {
        this(new InputStreamReader(input));
    }

    public SimpleParser(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    private StringBuilder builder = new StringBuilder();

    private final char[] chars = new char[1024];

    private int position = 0;
    private int length = 0;

    private State state = State.NORMAL;

    enum State {
        NORMAL,
        OUT_ONCE,
        IN_QUOTE,
        NOTION
    }

    /**
     * 获得 Java 文件中的关键字！<br>
     * <p/>
     *
     * 每次调用
     *
     * @return
     */
    public String nextToken() {
        switch (state) {
            case OUT_ONCE -> {
                String result = builder.toString();
                builder = new StringBuilder();
                state = State.NORMAL;
                return result;
            }
            case NORMAL -> {
                whileLoop:
                while (true) {
                    while (position < length) {
                        switch (chars[position]) {
                            case '{', '}', '(', ')', '[', ']', ';', '.' -> {
                                var other = builder;
                                if (builder.isEmpty()) {
                                    other.append(chars[position++]);
                                    builder = new StringBuilder();
                                } else {
                                    builder = new StringBuilder().append(chars[position++]);
                                    this.state = State.OUT_ONCE;
                                }
                                return other.toString();
                            }
                            case '\"' -> {
                                String result = this.builder.isEmpty() ? null : this.builder.toString();
                                this.builder = new StringBuilder().append(chars[position++]);
                                this.state = State.IN_QUOTE;
                                if (Objects.nonNull(result))
                                    return result;
                                else
                                    return this.nextToken();
                            }
                            case ' ', '\n' -> {
                                if (builder.length() > 0) {
                                    var result = builder.toString();
                                    builder = new StringBuilder();
                                    position++;
                                    return result;
                                } else {
                                    position++;
                                }
                            }
                            default -> {
                                assert chars[position] != '\t';
                                builder.append(chars[position++]);
                            }
                        }
                    }
                    assert position >= length;
                    try {
                        length = reader.read(chars);
                        position = 0;
                        if (length == 0) {
                            var other = builder;
                            builder = new StringBuilder();
                            return other.toString();
                        }
                    } catch (IOException e) {
                        var other = builder;
                        builder = new StringBuilder();
                        return other.toString();
                    }
                }
            }
            case IN_QUOTE -> {
//                whileLoop:
                while (true) {
                    while (position < length) {
                        switch (chars[position]) {
                            case '\"' -> {
                                this.builder.append(chars[position++]);
                                if (this.builder.length() < 2 || this.builder.charAt(this.builder.length() - 2) != '\\') {
                                    this.state = State.NORMAL;
                                    var result = this.builder;
                                    this.builder = new StringBuilder();
                                    return result.toString();
                                }
                            }
                            default -> this.builder.append(chars[position++]);
                        }
                    }
                    assert position >= length;
                    try {
                        length = reader.read(chars);
                        position = 0;
                        if (length == 0) {
                            var other = builder;
                            builder = new StringBuilder();
                            return other.toString();
                        }
                    } catch (IOException e) {
                        var other = builder;
                        builder = new StringBuilder();
                        return other.toString();
                    }
                }
            }
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        File file = Paths.get("test", "fortest", "HelloWorld.java").toFile();
        InputStream stream = new FileInputStream(file);
        var parser = new SimpleParser(stream);
        for (var s = parser.nextToken(); s != null && s.length() > 0; s = parser.nextToken()) {
            System.out.println(s);
        }
    }

}
