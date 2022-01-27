package interf;

import utils.LineInfo;

import java.io.IOException;
import java.io.Reader;

public abstract class AbstractIndentationController implements AutoCloseable{

    protected final Reader reader;

    protected AbstractIndentationController(Reader reader) {
        this.reader = reader;
    }

    public abstract LineInfo nextLine() throws IOException;

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
