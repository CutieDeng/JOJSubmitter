package interf;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.function.Function;

public abstract class AbstractFilterInputStream extends Reader {
    protected final Reader reader;

    protected AbstractFilterInputStream(Reader reader) {
        this.reader = reader;
    }

    @Override
    public void close() throws IOException {
        this.reader.close();
    }

    public final AbstractFilterInputStream andThen(Function<Reader, ? extends AbstractFilterInputStream> nextFilterInputStreamSupplier) {
        return nextFilterInputStreamSupplier.apply(this);
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int length = 0;
        for (int i = 0; i < len; ++i) {
            if (i + off >= cbuf.length) {
                return length;
            }
            cbuf[i + off] = (char) this.read();
            length++;
        }
        return length;
    }
}
