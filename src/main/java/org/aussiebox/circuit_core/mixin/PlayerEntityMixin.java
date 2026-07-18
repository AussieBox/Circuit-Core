package org.aussiebox.circuit_core.mixin;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.aussiebox.circuit_core.CircuitCore;
import org.aussiebox.circuit_core.exclusive.item.PlayerExclusiveItemsPersistentState;
import org.aussiebox.circuit_core.helper.PlayerExclusiveItemHelper;
import org.aussiebox.circuit_core.network.UpdateExclusiveItemsS2CPayload;
import org.aussiebox.circuit_core.util.ExclusiveItemHolder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements ExclusiveItemHolder {
    @Unique private final List<RegistryKey<Item>> allowedItems = new ArrayList<>();

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Override
    public void circuitCore$allowItem(Item item) {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
        if (key.isEmpty()) return;

        if (!allowedItems.contains(key.get())) allowedItems.add(key.get());
        circuitCore$syncToClient();
    }

    @Override
    public void circuitCore$disallowItem(Item item) {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
        if (key.isEmpty()) return;

        allowedItems.removeAll(Collections.singleton(key.get()));
        circuitCore$syncToClient();
    }

    @Override
    public boolean circuitCore$itemAllowed(Item item) {
        Optional<RegistryKey<Item>> key = Registries.ITEM.getKey(item);
        if (!PlayerExclusiveItemHelper.canBeExclusive(item)) return true;
        return key.isPresent() && allowedItems.contains(key.get());
    }

    @Override
    public void circuitCore$allowItem(RegistryKey<Item> item) {
        if (!allowedItems.contains(item)) allowedItems.add(item);
        circuitCore$syncToClient();
    }

    @Override
    public void circuitCore$disallowItem(RegistryKey<Item> item) {
        allowedItems.removeAll(Collections.singleton(item));
        circuitCore$syncToClient();
    }

    @Override
    public boolean circuitCore$itemAllowed(RegistryKey<Item> item) {
        if (!PlayerExclusiveItemHelper.canBeExclusive(item)) return true;
        return allowedItems.contains(item);
    }

    @Override
    public void circuitCore$syncToClient() {
        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player instanceof ServerPlayerEntity playerEntity) {
            UpdateExclusiveItemsS2CPayload payload = new UpdateExclusiveItemsS2CPayload(allowedItems);
            ServerPlayNetworking.send(playerEntity, payload);
        }
    }

    //? 1.21.1 {
    @Inject(method = "<init>", at = @At("RETURN"))
    private void circuitCore$initAllowedItems(World genericWorld, BlockPos pos, float yaw, GameProfile gameProfile, CallbackInfo ci) {
        if (!(genericWorld instanceof ServerWorld world)) return;
        PlayerExclusiveItemsPersistentState state = PlayerExclusiveItemsPersistentState.getServerState(world);
        allowedItems.addAll(state.allPlayerCanGet(gameProfile.getId()));
    }
    //? }
    //? >=1.21.8 {
    /*@Inject(method = "<init>", at = @At("RETURN"))
    private void circuitCore$initAllowedItems(World genericWorld, GameProfile profile, CallbackInfo ci) {
        if (!(genericWorld instanceof ServerWorld world)) return;
        PlayerExclusiveItemsPersistentState state = PlayerExclusiveItemsPersistentState.getServerState(world);
        //? 1.21.8
        //allowedItems.addAll(state.allPlayerCanGet(profile.getId()));
        //? >=1.21.10
        //allowedItems.addAll(state.allPlayerCanGet(profile.id()));
    }
    *///? }
}
