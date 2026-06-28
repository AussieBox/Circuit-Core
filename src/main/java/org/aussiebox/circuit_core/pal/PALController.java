package org.aussiebox.circuit_core.pal;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.pal.animation.PALStackAnimation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PALController {
    public final Identifier id;
    public final int priority;
    protected final Object2ObjectOpenHashMap<Identifier, PALAnimation> animations;

    protected final List<Consumer<PALController>> observers;

    public PALController(Identifier id, int priority, PALAnimation... animations) {
        this.id = id;
        this.priority = priority;
        Object2ObjectOpenHashMap<Identifier, PALAnimation> map = new Object2ObjectOpenHashMap<>();
        for (PALAnimation animation : animations) {
            map.put(animation.id, animation);
            animation.observe(this::onChange);
        }
        this.animations = map;

        this.observers = new ArrayList<>();
    }

    public PALAnimation getAnimation(Identifier id) {
        for (PALAnimation anim : animations.values()) {
            if (!(anim instanceof PALStackAnimation stackAnim)) continue;
            if (stackAnim.leftHandedId.equals(id) || stackAnim.rightHandedId.equals(id)) return stackAnim;
        }
        return animations.getOrDefault(id, null);
    }

    private void onChange(PALAnimation animation) {
        if (animations.containsKey(animation.id)) {
            animations.put(animation.id, animation);
            notifyObservers();
        }
    }

    public void observe(Consumer<PALController> consumer) {
        this.observers.add(consumer);
    }

    protected void notifyObservers() {
        for (var observer : this.observers) {
            observer.accept(this);
        }
    }
}
