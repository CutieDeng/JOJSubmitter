package indentimpl;

import interf.AbstractIndentationController;
import utils.LineInfo;

import java.io.IOException;
import java.io.Reader;

public class LazyIndentationController extends AbstractIndentationController {
    public LazyIndentationController(Reader reader) {
        super(reader);
    }

    @Override
    public LineInfo nextLine() throws IOException {
        // blank flag, if it's true, then this line is empty!
        boolean blankLine = true;

        // The indentation length
        int indentLen = 0;
        final StringBuilder builder = new StringBuilder();

        // Till read the end of the separator.
        // Using the special separator tip, then quickly get it well.

        while (true) {
            int r = reader.read();
            if (r == 0 || r == -1) {
                // Meet another 0, then return the result.
                do {
                    r = reader.read();
                } while (r != 0 && r != -1);
                if (r == -1 && builder.length() == 0 && indentLen == 0) {
                    return null;
                }
                break;
            }
            if (blankLine) {
                if (r == ' ')
                    ++indentLen;
                else
                    blankLine = false;
            }
            if (!blankLine) {
                builder.append((char )r);
            }
        }

        if (blankLine)
            return new LineInfo(0, "");
        else {
            return new LineInfo(indentLen, builder.toString());
        }
    }
}
