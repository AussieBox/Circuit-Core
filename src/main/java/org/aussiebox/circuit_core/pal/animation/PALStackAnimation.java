package org.aussiebox.circuit_core.pal.animation;

import com.zigythebird.playeranimcore.animation.Animation;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import org.aussiebox.circuit_core.pal.PALAnimation;
import org.aussiebox.circuit_core.util.Observable;

public class PALStackAnimation extends PALAnimation {
    public Observable<Item> expectedItem;
    public Observable<Behavior> behavior;

    public static final Behavior DEFAULT_BEHAVIOR = new Behavior(false, false, true, false);

    public PALStackAnimation(Identifier id, Animation.LoopType playMode, Item expectedItem, Behavior behavior) {
        super(id, playMode);

        this.expectedItem = Observable.of(expectedItem);
        this.expectedItem.observe(this::updateExpectedItem);
        this.behavior = Observable.of(behavior);
        this.behavior.observe(this::updateBehavior);
    }

    private void updateExpectedItem(Item item) {
        notifyObservers();
    }

    private void updateBehavior(Behavior behavior) {
        notifyObservers();
    }

    public static class Behavior {
        public boolean allowAnyStackInstance;
        public boolean lockSlotWithStack;
        public boolean allowChangesToOtherHand;
        public boolean lockOtherHand;

        /// Creates a new Behavior instance, which describes what the animation is and isn't allowed to control about the player.
        /// @param allowAnyStackInstance Allows the animation to continue if the player switches to a new {@link net.minecraft.item.ItemStack ItemStack} of the same item.
        /// @param lockSlotWithStack Stops the player from modifying the animation's {@link net.minecraft.item.ItemStack ItemStack}.
        /// @param allowChangesToOtherHand Allows the animation to continue if the player modifies the {@link net.minecraft.item.ItemStack ItemStack} in the animation's unused hand.
        /// @param lockOtherHand Stops the player from modifying the {@link net.minecraft.item.ItemStack ItemStack} in the animation's unused hand.
        public Behavior(boolean allowAnyStackInstance, boolean lockSlotWithStack, boolean allowChangesToOtherHand, boolean lockOtherHand) {
            this.allowAnyStackInstance = allowAnyStackInstance;
            this.lockSlotWithStack = lockSlotWithStack;
            this.allowChangesToOtherHand = allowChangesToOtherHand;
            this.lockOtherHand = lockOtherHand;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof Behavior behavior) return allowAnyStackInstance == behavior.allowAnyStackInstance &&
                    lockSlotWithStack == behavior.lockSlotWithStack &&
                    allowChangesToOtherHand == behavior.allowChangesToOtherHand &&
                    lockOtherHand == behavior.lockOtherHand;
            return super.equals(obj);
        }
    }
}
