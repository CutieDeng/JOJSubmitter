package filterimpl;

import interf.AbstractFilterInputStream;

import java.io.IOException;
import java.io.Reader;

public class JNotesEraser extends AbstractFilterInputStream {

    public JNotesEraser(Reader reader) {
        super(new JNotesEraserP(reader));
    }

    @Override
    public int read() throws IOException {
        int r = 0;
        do {
            r = reader.read();
        } while (r == 0);
        return r;
    }
}
