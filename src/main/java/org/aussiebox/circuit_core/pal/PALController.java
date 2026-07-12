package org.aussiebox.circuit_core.pal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.pal.animation.AnimationData;
import org.aussiebox.circuit_core.pal.handler.HandlerData;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PALController<D extends AnimationData> {
    public final Identifier id;
    public final int priority;
    @Getter protected final Object2ObjectOpenHashMap<Identifier, PALAnimation<D>> animations;
    public final Class<D> dataClass;

    protected final List<Consumer<PALController<D>>> observers;

    @SafeVarargs
    public PALController(Identifier id, int priority, Class<D> dataClass, PALAnimation<D>... animations) {
        this.id = id;
        this.priority = priority;
        Object2ObjectOpenHashMap<Identifier, PALAnimation<D>> map = new Object2ObjectOpenHashMap<>();
        for (PALAnimation<D> animation : animations) {
            map.put(animation.data.id, animation);
            animation.observe(this::onChange);
        }
        this.animations = map;
        this.dataClass = dataClass;

        this.observers = new ArrayList<>();
    }

    public PALAnimation<D> getAnimation(Identifier id) {
        for (PALAnimation<D> anim : animations.values())
            if (anim.data.matchesId(id)) return anim;
        return null;
    }

    public HandlerData createHandlerData() {
        try {
            return dataClass.getDeclaredConstructor().newInstance().newHandlerData();
        } catch (Exception e) {
            CircuitCore.LOGGER.error("Failed to create data for animation handler: {}", e.toString());
            return null;
        }
    }

    private void onChange(PALAnimation<D> animation) {
        if (animations.containsKey(animation.data.id)) {
            animations.put(animation.data.id, animation);
            notifyObservers();
        }
    }

    public void observe(Consumer<PALController<D>> consumer) {
        this.observers.add(consumer);
    }

    protected void notifyObservers() {
        for (var observer : this.observers) {
            observer.accept(this);
        }
    }
}
