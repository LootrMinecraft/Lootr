package noobanidus.mods.lootr.mixins;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import noobanidus.mods.lootr.api.LootrAPI;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BlockEntity.class)
public class MixinBlockEntity {
  private static final String LOOTR_SPECIAL_CHEST = "lootr:special_loot_chest";
  private static final String LOOTR_SPECIAL_BARREL = "lootr:special_loot_barrel";
  private static final String LOOTR_SPECIAL_TRAPPED_CHEST = "lootr:special_trapped_loot_chest";
  private static final String LOOTR_SPECIAL_SHULKER = "lootr:special_loot_shulker";
  private static final String LOOTR_SPECIAL_INVENTORY = "lootr:special_loot_inventory";

  private static final ResourceLocation LOOTR_CHEST = LootrAPI.rl("lootr_chest");
  private static final ResourceLocation LOOTR_TRAPPED_CHEST = LootrAPI.rl("lootr_trapped_chest");
  private static final ResourceLocation LOOTR_SHULKER = LootrAPI.rl("lootr_shulker");
  private static final ResourceLocation LOOTR_BARREL = LootrAPI.rl("lootr_barrel");
  private static final ResourceLocation LOOTR_INVENTORY = LootrAPI.rl("lootr_inventory");

  @Redirect(method = "loadStatic", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;tryParse(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"))
  private static ResourceLocation LootrLoadStatic(String value) {
    if (value.equals(LOOTR_SPECIAL_CHEST)) {
      return LOOTR_CHEST;
    } else if (value.equals(LOOTR_SPECIAL_BARREL)) {
      return LOOTR_BARREL;
    } else if (value.equals(LOOTR_SPECIAL_TRAPPED_CHEST)) {
      return LOOTR_TRAPPED_CHEST;
    } else if (value.equals(LOOTR_SPECIAL_SHULKER)) {
      return LOOTR_SHULKER;
    } else if (value.equals(LOOTR_SPECIAL_INVENTORY)) {
      return LOOTR_INVENTORY;
    }
    return ResourceLocation.tryParse(value);
  }
}
