package cutter;

import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;
import java.util.Optional;

/**
 * 这是简易的剪贴板控制器<br>
 * <p/>
 *
 * 凑合着用吧，别太挑剔了。<br>
 * <p/>
 */
public class CutieCutter implements CutterAdapter{
    private static final Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

    private static String getStringFromTransferable(Transferable transferable) {
        try {
            return (String) transferable.getTransferData(DataFlavor.stringFlavor);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnsupportedFlavorException e) {
            // It's a frequently happening situation.
            // Pretty good to ignore it.
        }
        return null;
    }

    /**
     * 设置剪贴板內的信息<br>
     * <p/>
     *
     * 在剪贴板不可用的情形下，会抛出 {@link IllegalStateException}. <br>
     * <p/>
     *
     * @param stringContent 将要设置于剪贴板內的信息
     */
    @Override
    public void setClipboardContent(String stringContent) {
        var trans = new StringSelection(stringContent);
        clip.setContents(trans, null);
    }

    /**
     * 获取剪贴板信息方法<br>
     * <p/>
     *
     * 该方法能够从操作系统的剪贴板中获取内容！<br>
     * 以下情况下，该方法将会返回 null. <br>
     * - 剪贴板不支持将内容转换为字符串。<br>
     * - 剪贴板发生 IO 异常（同时打印异常信息）<br>
     * - 获取操作系统剪贴板失败 <br>
     * <p/>
     *
     * @return 剪贴板中的字符串信息
     */
    @Override
    public String getClipboardContent() {
        final Optional<Transferable> contents = Optional.ofNullable(clip.getContents(null));
        return contents.map(CutieCutter::getStringFromTransferable).orElse(null);
    }
}