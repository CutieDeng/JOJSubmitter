package codeparse;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 一个高级代码处理器 <br>
 * <p/>
 *
 * - 内置若干状态和状态转换机制 <br>
 * - 各状态有状态移动描述和状态缓存栈 <br>
 * <p/>
 *
 */
public class BlockParser implements Parser, AutoCloseable{

    private static class CharBufferImpl implements AutoCloseable{

        private static final short LENGTH = 1024;

        private BufferedReader reader;

        private final char[] chars = new char[LENGTH];

        private int length;
        private int pointer;

        public CharBufferImpl(Reader reader) {
            this.reader = new BufferedReader(reader);
        }

        public CharBufferImpl(BufferedReader reader) {
            this.reader = reader;
        }

        public char nextChar() {
            class CharNotExistException extends RuntimeException {
            }
            if (!hasNextChar())
                throw new CharNotExistException();
            return this.chars[pointer++];
        }

        public boolean hasNextChar() {
            if (pointer >= length) {
                try {
                    length = reader.read(chars);
                    pointer = 0;
                    return pointer < length;
                } catch (IOException e) {
                    return false;
                }
            }
            return true;
        }

        @Override
        public void close() throws Exception {
            Objects.requireNonNull(this.reader);
            this.reader.close();
            this.reader = null;
        }
    }

    private CharBufferImpl cImpl;

    public BlockParser(InputStream stream) {
        cImpl = new CharBufferImpl(new InputStreamReader(stream));
    }

    public BlockParser(Reader reader) {
        cImpl = new CharBufferImpl(reader);
    }

    public BlockParser(BufferedReader reader) {
        cImpl = new CharBufferImpl(reader);
    }

    private StringBuilder cache = new StringBuilder();

    private static final Set<Character> separateSet = Stream.of('{', '}', '[', ']', '(', ')', ';', '.', ',', '+', '%',
                    '*', '<', '>', '=', '|', '&', '!')
            .collect(Collectors.toSet());

    private static final Set<Character> blankSet = Stream.of(' ', '\t', '\n').collect(Collectors.toSet());

    private static final Map<String, AtomicReference<String>> cacheRef = new HashMap<>();

    private boolean lineNotion = false;
    private boolean blockNotion = false;
    private boolean readStarNotion = false;
    private boolean readingBLank = false;

    private boolean characterDirectQuantityState = false;
    private boolean backSlash = false;

    @Override
    public boolean hasNextToken() {
        return this.cache.length()!=0 || this.cImpl.hasNextChar();
    }

    private TokenRecord out(boolean block) {
        var result = cache.toString();
        cache = new StringBuilder();
        if (!cacheRef.containsKey(result))
            cacheRef.put(result, new AtomicReference<>(result));
        return new TokenRecord(block, cacheRef.get(result));
    }

    private TokenRecord out(boolean block, char least) {
        var result = out(block);
        this.cache.append(least);
        assert this.cache.length() == 1;
        this.readingBLank = blankSet.contains(this.cache.charAt(0));
        return result;
    }

    private void inLineNotes() {
        lineNotion = true;
        if (this.cache.length() > 0)
            this.cache = new StringBuilder();
    }

    private void inBlockNotes() {
        blockNotion = true;
        if (this.cache.length() > 0)
            this.cache = new StringBuilder();
    }

    private void outLineNotes() {
        lineNotion = false;
    }

    private void outBlockNotes() {
        blockNotion = false;
    }

    @Override
    public TokenRecord nextToken() {
        while (true) {
            // The special tokens needn't to be hesitate.
            if (cache.length() == 1 && separateSet.contains(cache.charAt(0))) {
                return out(true);
            }
            if (cache.length() == 1 && cache.charAt(0) == '"') {
                characterDirectQuantityState = true;
            }
            if (!cImpl.hasNextChar()) {
                return out(!readingBLank);
            }
            char r = cImpl.nextChar();
            if (r == '\n') {
                outLineNotes();
            }
            if (lineNotion || blockNotion) {
                if (readStarNotion && blockNotion && r == '/')
                    outBlockNotes();
                readStarNotion = r == '*';
                continue;
            }
            if (characterDirectQuantityState) {
                this.cache.append(r);
                if (r == '"' && !backSlash) {
                    characterDirectQuantityState = false;
                    return out(false);
                }
                backSlash = r == '\\';
                continue;
            }
            if (cache.length() == 0) {
                readingBLank = blankSet.contains(r);
                this.cache.append(r);
                continue;
            }
            if (readingBLank) {
                if (blankSet.contains(r))
                    cache.append(r);
                else
                    return out(false, r);
            } else {
                if (cache.length() == 1 && cache.charAt(0) == '-') {
                    if (r == '>') {
                        this.cache.append(r);
                        return out(true);
                    } else
                        return out(true, r);
                }

                if (cache.length() == 1 && cache.charAt(0) == '/') {
                    switch (r) {
                        case '/' -> {
                            assert this.cache.length() == 1;
                            inLineNotes();
                        }
                        case '*' -> {
                            assert this.cache.length() == 1;
                            inBlockNotes();
                        }
                        default -> {
                            return out(true, r);
                        }
                    }
                    continue ;
                }

                assert this.cache.length() > 0;
                if (separateSet.contains(r) || blankSet.contains(r)) {
                    return out(true, r);
                }
                this.cache.append(r);
            }
        }
    }

    @Override
    public void close() throws Exception {
        this.cImpl.close();
    }

}