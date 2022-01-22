package cutter;

/**
 * 剪贴板输出适配器<br>
 * <p/>
 *
 * 剪贴板输出适配器提供了两个方法接口：<br>
 * {@link #getClipboardContent()} 获取当前剪贴板内容<br>
 * {@link #setClipboardContent(String)} 设置当前剪贴板内容<br>
 * <p/>
 */
public interface CutterAdapter {
    /**
     * 设置当前系统剪贴板的内容<br>
     * <p/>
     *
     * @param stringContent 被设置在剪贴板中的内容
     */
    void setClipboardContent(String stringContent);

    /**
     *
     * @return 当前系统剪贴板內的内容——如果不支持转化为字符串，则返回 null.
     */
    String getClipboardContent();
}
