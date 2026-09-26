package noobanidus.mods.lootr.common.mixin.accessor;

import net.minecraft.client.renderer.blockentity.DecoratedPotRenderer;
import net.minecraft.client.resources.model.sprite.SpriteId;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemInstance;
import org.apache.commons.lang3.NotImplementedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import java.util.Optional;

@Mixin(DecoratedPotRenderer.class)
public interface AccessorMixinDecoratedPotRenderer {
  @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
  @Invoker("getSideSprite")
  DecoratedPotRenderer.SideSprite lootr$getSideSprite(Optional<? extends ItemInstance> item);

}
