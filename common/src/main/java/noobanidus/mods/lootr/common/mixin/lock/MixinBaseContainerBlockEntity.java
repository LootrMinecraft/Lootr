package noobanidus.mods.lootr.common.mixin.lock;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import noobanidus.mods.lootr.common.api.data.blockentity.LockMessageSuppression;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

/*
 These mixins allow `ILootrBlockEntity`'s default implementation of `ILootrInfo::canPlayerOpen`
 to check whether the container is locked without sending lock messages/sounds to the player
 with the default implementation.

 Obviously, for all containers that implement `canOpen` *differently*, those implementations
 may need to be separately handled.

 (Interestingly, Vanilla runs into this issue as it overrides `canOpen` in `RandomizableContainerBlockEntity`
 to prevent spectators from opening containers that have a loot table associated with them.
 This results in the "Container is locked!" message whenever a spectator tries to open it.)
 */
@Mixin(BaseContainerBlockEntity.class)
public class MixinBaseContainerBlockEntity {
  @WrapOperation(method = "sendChestLockedNotifications", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;displayClientMessage(Lnet/minecraft/network/chat/Component;Z)V"))
  private static void lootr$suppressLockMessage(Player instance, Component chatComponent, boolean actionBar, Operation<Void> original) {
    if (!LockMessageSuppression.isSuppressed()) {
      original.call(instance, chatComponent, actionBar);
    }
  }

  @WrapOperation(method = "sendChestLockedNotifications", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;playSound(Lnet/minecraft/world/entity/Entity;DDDLnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FF)V"))
  private static void lootr$suppressLockSound(Level instance, Entity entity, double x, double y, double z, SoundEvent sound, SoundSource source, float volume, float pitch, Operation<Void> original) {
    if (!LockMessageSuppression.isSuppressed()) {
      original.call(instance, entity, x, y, z, sound, source, volume, pitch);
    }
  }
}
