package org.aussiebox.circuit_core.pal.animation;

import com.zigythebird.playeranimcore.animation.Animation;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.pal.handler.DefaultHandlerData;
import org.aussiebox.circuit_core.pal.handler.HandlerData;
import org.aussiebox.circuit_core.util.Observable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class AnimationData {
    public final Identifier id;
    public Observable<Animation.LoopType> loopType;

    protected final List<Consumer<AnimationData>> observers = new ArrayList<>();

    public AnimationData() {
        this.id = CircuitCoreConstants.NO_ANIMATION;
        this.loopType = Observable.of(Animation.LoopType.DEFAULT);
        this.loopType.observe(loopType1 -> notifyObservers());
    }

    public AnimationData(Identifier id, Animation.LoopType loopType) {
        if (id == null) this.id = CircuitCoreConstants.NO_ANIMATION;
        else this.id = id;

        this.loopType = Observable.of(loopType);
        this.loopType.observe(loopType1 -> notifyObservers());
    }

    public AnimationData(Identifier id) {
        if (id == null) this.id = CircuitCoreConstants.NO_ANIMATION;
        else this.id = id;

        this.loopType = Observable.of(Animation.LoopType.DEFAULT);
    }

    public void observe(Consumer<AnimationData> consumer) {
        this.observers.add(consumer);
    }

    protected void notifyObservers() {
        for (Consumer<AnimationData> observer : this.observers) {
            observer.accept(this);
        }
    }

    public boolean matchesId(Identifier id) {
        return this.id.equals(id);
    }

    public HandlerData newHandlerData() {
        return new DefaultHandlerData();
    }

    public Class<? extends HandlerData> handlerDataClass() {
        return HandlerData.class;
    }
}
