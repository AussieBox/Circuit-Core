package org.aussiebox.circuit_core.pal;

import org.aussiebox.circuit_core.pal.animation.AnimationData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PALAnimation<D extends AnimationData> {
    public final D data;

    protected final List<Consumer<PALAnimation<D>>> observers;

    public PALAnimation(D data) {
        this.data = data;
        this.data.observe(animationData -> notifyObservers());

        this.observers = new ArrayList<>();
    }

    public void observe(Consumer<PALAnimation<D>> consumer) {
        this.observers.add(consumer);
    }

    protected void notifyObservers() {
        for (var observer : this.observers) {
            observer.accept(this);
        }
    }
}
