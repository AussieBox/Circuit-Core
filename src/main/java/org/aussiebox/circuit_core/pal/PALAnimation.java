package org.aussiebox.circuit_core.pal;

import com.zigythebird.playeranimcore.animation.Animation;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.util.Observable;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class PALAnimation {
    @Nullable public final Identifier id;
    public Observable<Animation.LoopType> loopType;

    protected final List<Consumer<PALAnimation>> observers;

    public PALAnimation(@Nullable Identifier id, Animation.LoopType playMode) {
        if (id == null) this.id = CircuitCoreConstants.NO_ANIMATION;
        else this.id = id;

        this.loopType = Observable.of(playMode);
        this.loopType.observe(this::updateLoopType);

        this.observers = new ArrayList<>();
    }

    public void observe(Consumer<PALAnimation> consumer) {
        this.observers.add(consumer);
    }

    protected void notifyObservers() {
        for (var observer : this.observers) {
            observer.accept(this);
        }
    }

    private void updateLoopType(Animation.LoopType loopType) {
        notifyObservers();
    }
}
