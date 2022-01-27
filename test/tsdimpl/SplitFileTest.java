package tsdimpl;

import filterimpl.JNotesEraserP;
import filterimpl.TabTransfer;
import indentimpl.LazyIndentationController;
import interf.AbstractFilterInputStream;
import interf.AbstractIndentationController;
import interf.Element;
import interf.LineTokenTrans;
import lineimpl.LineTokenImpl;
import stdimpl.TokenElement;
import utils.LineInfo;

import java.io.*;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SplitFileTest {
    public static void main(String[] args) throws FileNotFoundException {
//        File toSplitFile = Paths.get("test", "fortest", "HelloWorld.java").toFile();
        File toSplitFile = Paths.get("test", "PMMDTest.java").toFile(); 

        BufferedReader fileRead = new BufferedReader(new InputStreamReader(new FileInputStream(toSplitFile)));
        AbstractFilterInputStream fileFilter = new TabTransfer(fileRead).andThen(JNotesEraserP::new);

        AbstractIndentationController controller = new LazyIndentationController(fileFilter);

        LineTokenTrans trans = new LineTokenImpl();
        LineInfo f = null;

//        do {
//            try {
//                f = controller.nextLine();
//                if (f != null)
//                    System.out.println(f.str());
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } while (f != null);

        do {
            try {
                f = controller.nextLine();
                if (f != null) {
                    System.out.print(" ".repeat(f.indent()));
                    List<Element> list = new LinkedList<>();
                    for (Element e = trans.apply(f); e != null; e = e.next()) {
                        list.add(e);
                    }
                    if (list.size() == 0)
                        System.out.println();
                    else
                        System.out.println(list.stream().map(Object::toString).collect(Collectors.joining("|", "|", "|")));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (f != null);

    }
}
