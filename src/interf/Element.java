package interf;

public abstract class Element {

    protected Element next;
    protected Element before;

    public abstract Element next();
    public abstract Element before();
    public abstract Element typeNext();
    public abstract Element typeBefore();

    protected abstract void setTypeNext(Element element);
    protected abstract void setTypeBefore(Element element);

    public static void connectDirectly(Element element, Element element2) {
        element.next = element2;
        element2.before = element;
    }

    public static <T extends Element> void connectType(T element, T element2) {
        element.setTypeNext(element2);
        element2.setTypeBefore(element);
    }
}
