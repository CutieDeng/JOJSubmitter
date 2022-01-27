package filterimpl;

import interf.AbstractFilterInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;

public class TabTransfer extends AbstractFilterInputStream {

    private static final int TAB2BLANK = 4;

    private final int tab2Blank;
    private int nowCount;

    public TabTransfer(Reader stream) {
        this(stream, TAB2BLANK);
    }

    public TabTransfer(Reader stream, int tab2Blank) {
        super(stream);
        this.tab2Blank = tab2Blank;
    }

    @Override
    public int read() throws IOException {
        if (nowCount > 0) {
            nowCount--;
            return ' ';
        }
        int read = this.reader.read();
        if (read == '\t') {
            nowCount = tab2Blank - 1;
            return ' ';
        } else
            return read;
    }
}
