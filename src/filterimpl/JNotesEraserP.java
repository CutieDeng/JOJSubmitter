package filterimpl;

import interf.AbstractFilterInputStream;

import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * JNotesEraser is a filter to deal with the notes in Java codes.<br>
 * <p/>
 *
 * Using this filter input stream, you can easily eraser the useless codes information.<br>
 * It's the part of the code parser.<br>
 * <p/>
 *
 */
public class JNotesEraserP extends AbstractFilterInputStream {

    private static final int STRING_QUANTITY = 0x1;
    private static final int LINE_NOTE = 0x2;
    private static final int BLOCK_NOTE = 0x4;

    public JNotesEraserP(Reader reader) {
        super(reader);
    }

    private int state;

    private void unsafeGetLineNote() {
        assert !isStringQuantity();
        this.state |= LINE_NOTE;
    }

    private void unsafeGetBlockNote() {
        assert !isStringQuantity();
        this.state |= BLOCK_NOTE;
    }

    private void unsafeGetStringQuantity() {
        assert !isBlockNote() && !isLineNote();
        this.state |= STRING_QUANTITY;
    }

    private void cancelLineNote() {
        this.state &= ~LINE_NOTE;
    }

    private void cancelBlockNote() {
        this.state &= ~BLOCK_NOTE;
    }

    private void cancelStringQuantity() {
        this.state &= ~STRING_QUANTITY;
    }

    private boolean isLineNote() {
        return (this.state & LINE_NOTE) != 0;
    }

    private boolean isBlockNote() {
        return (this.state & BLOCK_NOTE) != 0;
    }

    private boolean isStringQuantity() {
        return (this.state & STRING_QUANTITY) != 0;
    }

    private boolean isNote() {
        return isLineNote() || isBlockNote();
    }

    private final Queue<Integer> buffers = new LinkedList<>();

    private final Queue<Integer> directlyOutput = new LinkedList<>();

    private int bufferRead() throws IOException {
        if (buffers.isEmpty())
            return this.reader.read();
        else
            return buffers.remove();
    }

    @Override
    public int read() throws IOException {
        // Firstly, the blank count has the highest priority, means you must print all the blanks out, then
        // you can get something to deal with.
        if (!directlyOutput.isEmpty()) {
            return directlyOutput.remove();
        }
        if (isNote()) {
            int r = bufferRead();
            // Consider the different line separator to use in different System.
            if (r == System.lineSeparator().charAt(0)) {
                if (System.lineSeparator().length() == 1) {
                    this.cancelLineNote();
                    directlyOutput.add(r);
                    directlyOutput.add(0);
                    return '\0';
                } else if (System.lineSeparator().length() == 2) {
                    int r2 = bufferRead();
                    if (r2 == System.lineSeparator().charAt(1)) {
                        directlyOutput.add(r);
                        directlyOutput.add(r2);
                        directlyOutput.add(0);
                        this.cancelLineNote();
                        return '\0';
                    } else {
                        buffers.add(r2);
                        return ' ';
                    }
                } else {
                    List<Integer> reads = new LinkedList<>();

                    final String sep = System.lineSeparator();

                    int i;
                    for (i = 1; i < sep.length(); ++i) {
                        int t = bufferRead();
                        reads.add(t);
                        if (t != sep.charAt(i))
                            break;
                    }

                    // Now confirm the length of the line separator is too long!
                    // Try to satisfy it!

                    if (i == sep.length()) {
                        // It's truly a line ending token!
                        directlyOutput.addAll(reads);
                        directlyOutput.add(0);
                        this.cancelLineNote();
                        return '\0';
                    } else {
                        // Not a line ending token!
//                            Stream.generate(() -> (int )' ').limit(reads.size()).forEach(directlyOutput::add);
                        buffers.addAll(reads);
                        return ' ';
                    }
                }
            }
            if (r == '*') {
                int r2 = reader.read();
                if (r2 == '/') {
                    directlyOutput.add((int) ' ');
                    this.cancelBlockNote();
                } else {
                    this.buffers.add(r2);
                }
            }
            return ' ';
        }

        // Then else, you should consider the string quantity state!
        if (isStringQuantity()) {
            int r = bufferRead();
            if (r == '"') {
                this.cancelStringQuantity();
            }
            // Then consider the backslash, the inversion meaning character, or escape character!
            if (r == '\\') {
                int r2 = bufferRead();
                directlyOutput.add(r2);
            }
            return r;
        }

        // What's more, we should talk about the normal situations.
        // Consider how to get in the other states, when you're at the normal state.
        int r = bufferRead();
        if (r == '"') {
            // Consider you cannot use an escape char in the normal sentence, this char must means the state would move to
            // the string quantity!
            this.unsafeGetStringQuantity();
            return r;
        }
        if (r == '/') {
            // This is a division mark, also the part of a note mark.
            int r2 = bufferRead();
            if (r2 == '/') {
                this.unsafeGetLineNote();
            } else if (r2 == '*') {
                this.unsafeGetBlockNote();
            } else {
                buffers.add(r2);
                return r;
            }
            this.directlyOutput.add((int) ' ');
            return ' ';
        }
        if (r == System.lineSeparator().charAt(0)) {
            if (System.lineSeparator().length() == 1) {
                directlyOutput.add(r);
                directlyOutput.add(0);
                return 0;
            } else if (System.lineSeparator().length() == 2) {
                int r2 = bufferRead();
                if (r2 == System.lineSeparator().charAt(1)) {
                    directlyOutput.add(r);
                    directlyOutput.add(r2);
                    directlyOutput.add(0);
                    return 0;
                } else {
                    buffers.add(r2);
                }
            } else
                throw new UnsupportedOperationException("System.lineSeparator.length > 2");
        }
        return r;
    }
}