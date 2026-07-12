package org.aussiebox.circuit_core.pal.animation;

import com.zigythebird.playeranimcore.animation.Animation;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.pal.handler.HandlerData;
import org.aussiebox.circuit_core.pal.handler.StackHandlerData;
import org.aussiebox.circuit_core.util.Observable;

public class StackAnimationData extends AnimationData {
    public final Observable<Item> expectedItem;
    public final Observable<Behavior> behavior;

    public final Identifier leftHandedId;
    public final Identifier rightHandedId;

    public static final Behavior DEFAULT_BEHAVIOR = new Behavior(false, false);

    public StackAnimationData() {
        super();
        this.leftHandedId = CircuitCoreConstants.NO_ANIMATION;
        this.rightHandedId = CircuitCoreConstants.NO_ANIMATION;
        this.expectedItem = Observable.of(Items.AIR);
        this.behavior = Observable.of(DEFAULT_BEHAVIOR);
    }

    public StackAnimationData(Identifier id, Identifier leftHandedId, Identifier rightHandedId, Animation.LoopType loopType, Item expectedItem, Behavior behavior) {
        super(id, loopType);
        this.leftHandedId = leftHandedId;
        this.rightHandedId = rightHandedId;
        this.expectedItem = Observable.of(expectedItem);
        this.behavior = Observable.of(behavior);

        this.expectedItem.observe(item -> notifyObservers());
        this.behavior.observe(behavior1 -> notifyObservers());
    }

    public StackAnimationData(Identifier id, Animation.LoopType loopType, Item expectedItem, Behavior behavior) {
        super(id, loopType);
        this.leftHandedId = id.withSuffixedPath("_left");
        this.rightHandedId = id.withSuffixedPath("_right");
        this.expectedItem = Observable.of(expectedItem);
        this.behavior = Observable.of(behavior);

        this.expectedItem.observe(item -> notifyObservers());
        this.behavior.observe(behavior1 -> notifyObservers());
    }

    public StackAnimationData(Identifier id, Identifier leftHandedId, Identifier rightHandedId, Animation.LoopType loopType, Item expectedItem) {
        super(id, loopType);
        this.leftHandedId = leftHandedId;
        this.rightHandedId = rightHandedId;
        this.expectedItem = Observable.of(expectedItem);
        this.behavior = Observable.of(DEFAULT_BEHAVIOR);

        this.expectedItem.observe(item -> notifyObservers());
        this.behavior.observe(behavior1 -> notifyObservers());
    }

    public StackAnimationData(Identifier id, Animation.LoopType loopType, Item expectedItem) {
        super(id, loopType);
        this.leftHandedId = id.withSuffixedPath("_left");
        this.rightHandedId = id.withSuffixedPath("_right");
        this.expectedItem = Observable.of(expectedItem);
        this.behavior = Observable.of(DEFAULT_BEHAVIOR);

        this.expectedItem.observe(item -> notifyObservers());
        this.behavior.observe(behavior1 -> notifyObservers());
    }

    @Override
    public boolean matchesId(Identifier id) {
        return this.id.equals(id) || this.leftHandedId.equals(id) || this.rightHandedId.equals(id);
    }

    @Override
    public HandlerData newHandlerData() {
        return new StackHandlerData();
    }

    @Override
    public Class<? extends HandlerData> handlerDataClass() {
        return StackHandlerData.class;
    }

    public static class Behavior {
        public boolean lockSlotWithStack;
        public boolean allowChangesToOtherHand;
        public boolean lockOtherHand;

        /// Creates a new Behavior instance, which describes what the animation is and isn't allowed to control about the player.
        /// @param lockSlotWithStack Stops the player from modifying the animation's {@link net.minecraft.item.ItemStack ItemStack}.
        /// @param lockOtherHand Stops the player from modifying the {@link net.minecraft.item.ItemStack ItemStack} in the animation's unused hand.
        public Behavior(boolean lockSlotWithStack, boolean lockOtherHand) {
            this.lockSlotWithStack = lockSlotWithStack;
            this.lockOtherHand = lockOtherHand;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Behavior behavior) return
                    lockSlotWithStack == behavior.lockSlotWithStack &&
                            allowChangesToOtherHand == behavior.allowChangesToOtherHand &&
                            lockOtherHand == behavior.lockOtherHand;
            return super.equals(obj);
        }
    }
}
