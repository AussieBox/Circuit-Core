package org.aussiebox.circuit_core.pal.animation;

import com.zigythebird.playeranimcore.animation.Animation;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.CircuitCoreConstants;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.util.Observable;
import org.jetbrains.annotations.Nullable;

public class PALStackAnimation extends PALAnimation {
    public Observable<Item> expectedItem;
    public Observable<Behavior> behavior;

    public final Identifier leftHandedId;
    public final Identifier rightHandedId;

    public static final Behavior DEFAULT_BEHAVIOR = new Behavior(false, false);

    public PALStackAnimation(Identifier id, Animation.LoopType playMode, Item expectedItem, @Nullable Behavior behavior) {
        super(id, playMode);
        if (behavior == null) behavior = DEFAULT_BEHAVIOR;

        this.expectedItem = Observable.of(expectedItem);
        this.expectedItem.observe(this::updateExpectedItem);
        this.behavior = Observable.of(behavior);
        this.behavior.observe(this::updateBehavior);
        this.leftHandedId = CircuitCoreConstants.NO_ANIMATION;
        this.rightHandedId = CircuitCoreConstants.NO_ANIMATION;
    }

    public PALStackAnimation(Identifier leftHandedId, Identifier rightHandedId, Animation.LoopType playMode, Item expectedItem, @Nullable Behavior behavior) {
        super(null, playMode);
        if (behavior == null) behavior = DEFAULT_BEHAVIOR;

        this.expectedItem = Observable.of(expectedItem);
        this.expectedItem.observe(this::updateExpectedItem);
        this.behavior = Observable.of(behavior);
        this.behavior.observe(this::updateBehavior);
        this.leftHandedId = leftHandedId;
        this.rightHandedId = rightHandedId;
    }

    private void updateExpectedItem(Item item) {
        notifyObservers();
    }

    private void updateBehavior(Behavior behavior) {
        notifyObservers();
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
