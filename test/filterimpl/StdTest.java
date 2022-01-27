package filterimpl;

import interf.AbstractFilterInputStream;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;

public class StdTest {
    public static void main(String[] args) throws IOException {
        final AbstractFilterInputStream stream = new JNotesEraser(
                new InputStreamReader(
                new FileInputStream(Paths.get("test", "fortest", "HelloWorld.java").toFile()
                )
            ));
        int r = -1;
        do {
            try {
                r = stream.read();
                System.out.print((char )r);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } while (r != -1);
        stream.close();
    }
}
