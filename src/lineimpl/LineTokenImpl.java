package lineimpl;

import interf.Element;
import interf.LineTokenTrans;
import stdimpl.EmptyElement;
import stdimpl.TokenElement;
import utils.LineInfo;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class LineTokenImpl implements LineTokenTrans {

    private final Node root = new Node();

    private static final Set<String> tokenSet = Stream.of("++", "--", "||", "&&", "^^", "->", "+=", "-=",
            "*=", "/=", "%=", "~", "!", "()", "[]", "{}", ")", "]", "}", ".", ";", "::", ",", "<>", ">",
                    "=", "^=", "&=", "|=", "!=", ">=", "<=")
            .collect(Collectors.toSet());

    private void addString2Set(CharSequence info) {
        var ii = info.chars().iterator();
        var tmp = root;
        while (ii.hasNext()) {
            tmp = tmp.getNode(ii.next());
        }
    }

    {
        tokenSet.forEach(this::addString2Set);
    }

    private static class Node {
        private Map<Integer, Node> map = null;

        public Node getNode(int value) {
            if (map == null) {
                map = new HashMap<>();
            }
            if (!map.containsKey(value)) {
                var n = new Node();
                map.put(value, n);
                return n;
            }
            return map.get(value);
        }

        public Node getNodeNullable(int value) {
            if (map == null)
                return null;
            return map.getOrDefault(value, null);
        }
    }

    private int state;

    /**
     * blank state means you now are reading the blank information.
     */
    private static final int BLANK_STATE = 0x1;
    private static final int NORMAL_STATE = 0x2;
    private static final int SPECIAL_STATE = 0x3;
    private static final int STRING_STATE = 0x4;

    public void reset() {
        state = 0;
    }

    public void blank() {
        state = BLANK_STATE;
    }

    public void normal() {
        state = NORMAL_STATE;
    }

    public void special() {
        state = SPECIAL_STATE;
    }

    public void string() {
        state = STRING_STATE;
    }

    private PrimitiveIterator.OfInt iterator;

    private int blankLength;
    private StringBuilder builder;

    private Queue<Integer> queue = new LinkedList<>();

    private int nextCodePoint() {
        if (queue.isEmpty())
            return iterator.next();
        else
            return queue.remove();
    }

    private boolean hasNextCodePoint() {
        return !queue.isEmpty() || iterator.hasNext();
    }

    private TokenElement firstElement;

    private TokenElement lastToken;
    private Element lastEle;
    private EmptyElement lastEmpty;
    private boolean backSlash;

    private void conduct(Element element) {

        // When we met the first element!
        if (firstElement == null) {
            // The element is invalid!
            if (!(element instanceof TokenElement))
                throw new RuntimeException("First element shouldn't be blank! ");
            lastEle = lastToken = firstElement = (TokenElement) element;
            return ;
        }

        Element.connectDirectly(lastEle, element);
        lastEle = element;

        if (element instanceof TokenElement) {
            // Connect it with token element.
            //TODO: it seems there are some bugs here!
            Element.connectType(lastToken, element);
            lastToken = (TokenElement) element;
        } else if (element instanceof EmptyElement) {
            if (lastEmpty != null)
                Element.connectType(lastEmpty, element);
            lastEmpty = (EmptyElement) element;
        } else
            throw new RuntimeException(String.format("Element(%s)'s version is too high! Cannot understand it. ",
                    element.getClass().getSimpleName()));
    }

    @Override
    public synchronized TokenElement apply(LineInfo lineInfo) {
        Node now = root;

        if (lineInfo.str().isEmpty())
            return null;


        firstElement = null;
        lastEle = null;
        lastEmpty = null;
        lastToken = null;

        queue.clear();
        iterator = lineInfo.str().codePoints().iterator();
        blankLength = 0;
        backSlash = false;
        state = 0;
        builder = new StringBuilder();

        while (hasNextCodePoint()) {
            int r = nextCodePoint();

            // When we haven't met any state!
            switch (state) {
                case 0 -> {
                    // We're reading a blank char.
                    if (r == ' ') {
                        blank();
                        ++blankLength;
                        continue;
                    }

                    var attempt = now.getNodeNullable(r);

                    builder.append((char )r);
                    // When we're reading a special char.
                    if (attempt != null) {
                        now = attempt;
                        special();
                        continue;
                    }

                    if (r == '"') {
                        string();
                        continue;
                    }

                    // Otherwise, we met a normal char.
                    normal();
                }
                case STRING_STATE -> {
                    builder.append((char )r);
                    if (!backSlash && r == '"') {
                        conduct(new TokenElement(builder.toString()));
                        builder = new StringBuilder();
                        reset();
                    }
                    if (r == '\\') {
                        backSlash = !backSlash;
                    } else {
                        backSlash = false;
                    }
                }
                case NORMAL_STATE -> {
                    var attempt = now.getNodeNullable(r);
                    if (attempt != null) {
                        conduct(new TokenElement(builder.toString()));
                        builder = new StringBuilder().append((char )r);
                        special();
                        now = attempt;
                    } else if (r == ' ') {
                        conduct(new TokenElement(builder.toString()));
                        builder = new StringBuilder();
                        blank();
                        blankLength++;
                    } else {
                        builder.append((char )r);
                    }
                }
                case SPECIAL_STATE -> {
                    var attempt = now.getNodeNullable(r);
                    if (attempt != null) {
                        now = attempt;
                        builder.append((char )r);
                        continue;
                    }
                    // Quit the special state.
                    conduct(new TokenElement(builder.toString()));
                    builder = new StringBuilder();
                    now = root;
                    reset();
                    queue.add(r);
                }
                case BLANK_STATE -> {
                    if (r == ' ') {
                        blankLength++;
                        continue;
                    }
                    conduct(new EmptyElement(blankLength));
                    blankLength = 0;
                    reset();
                    queue.add(r);
                }
            }
        }
        if (!builder.isEmpty()) {
            conduct(new TokenElement(builder.toString()));
        }
        return firstElement;
    }
}
