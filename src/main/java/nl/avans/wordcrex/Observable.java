package nl.avans.wordcrex;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Observable<T> {
    private final List<Consumer<T>> observers = new ArrayList<>();

    private T last;

    protected void next(T next) {
        this.observers.forEach((o) -> o.accept(next));
        this.last = next;
    }

    protected T getLast() {
        return this.last;
    }

    public void observe(Consumer<T> observer) {
        this.observers.add(observer);
        observer.accept(this.last);
    }
}
