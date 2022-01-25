package fortest;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.function.Consumer;

public class Hello2 {
    private String name;
    private boolean isMale;
    private transient String passwd;

    private Date birthDate;
    private transient int age;

    private void fresh() {
        Arrays.stream(Hello2.class.getDeclaredMethods()).
                filter(n -> !n.getName().equals("fresh") && n.getName().startsWith("fresh"))
                .forEach(m -> {
                    try {
                        m.setAccessible(true);
                        m.invoke(this);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        System.err.println(e.getMessage());
                    }
                });
        doSomethings.accept(null);
    }

    private void freshDate() {
        Date now = Date.from(Instant.now());
        int diff = birthDate.compareTo(birthDate);
        this.age = diff / 365 + 1;
    }

    private Consumer<Void> doSomethings = (avoid) -> {};

    protected void addAction(Consumer<Void> afterTheAction) {
        this.doSomethings = this.doSomethings.andThen(afterTheAction);
    }
}
