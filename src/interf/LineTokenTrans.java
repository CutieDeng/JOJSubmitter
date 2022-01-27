package interf;

import stdimpl.TokenElement;
import utils.LineInfo;

import java.util.function.Function;

public interface LineTokenTrans extends Function<LineInfo, TokenElement> {
}
