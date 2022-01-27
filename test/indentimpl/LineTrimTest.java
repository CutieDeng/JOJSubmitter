package indentimpl;

import filterimpl.JNotesEraserP;
import filterimpl.TabTransfer;
import utils.LineInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class LineTrimTest {
    public static void main(String[] args) throws IOException {
        LazyIndentationController controller = new LazyIndentationController(new JNotesEraserP(new TabTransfer(
                new InputStreamReader(new FileInputStream(Paths.get("test", "fortest", "HelloWorld.java").toFile()))
        )));
        LineInfo info = null;
        do {
            info = controller.nextLine();
            if (info != null)
                System.out.println(String.format("[%3d]%s", info.indent(), info.str()));
        } while (info != null);
    }
}
