package stdimpl;

import interf.Element;

public class TokenElement extends Element{
    private final String token;

    private TokenElement typeBefore;
    private TokenElement typeNext;

    public TokenElement(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return token;
    }

    @Override
    public Element next() {
        return this.next;
    }

    @Override
    public Element before() {
        return this.before;
    }

    @Override
    public TokenElement typeNext() {
        return this.typeNext;
    }

    @Override
    public TokenElement typeBefore() {
        return this.typeBefore;
    }

    @Override
    protected void setTypeNext(Element element) {
        this.typeNext = (TokenElement) element;
    }

    @Override
    protected void setTypeBefore(Element element) {
        this.typeBefore = (TokenElement) element;
    }
}

