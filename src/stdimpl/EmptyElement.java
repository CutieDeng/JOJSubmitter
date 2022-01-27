package stdimpl;

import interf.Element;

public class EmptyElement extends Element {
    private final int length;

    private EmptyElement typeBefore;
    private EmptyElement typeNext;

    public EmptyElement(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return " ".repeat(Math.max(0, length));
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
    public EmptyElement typeNext() {
        return this.typeNext;
    }

    @Override
    public EmptyElement typeBefore() {
        return this.typeBefore;
    }

    @Override
    protected void setTypeNext(Element element) {
        this.typeNext = (EmptyElement) element;
    }

    @Override
    protected void setTypeBefore(Element element) {
        this.typeBefore = (EmptyElement) element;
    }
}
