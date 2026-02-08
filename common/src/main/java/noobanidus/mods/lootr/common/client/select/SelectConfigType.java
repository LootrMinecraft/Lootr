package noobanidus.mods.lootr.common.client.select;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.properties.select.SelectItemModelProperty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import noobanidus.mods.lootr.common.api.client.ConfigDisplayType;
import noobanidus.mods.lootr.common.api.LootrAPI;
import org.jetbrains.annotations.Nullable;

public record SelectConfigType() implements SelectItemModelProperty<ConfigDisplayType> {
  public static final Type<SelectConfigType, ConfigDisplayType> TYPE = Type.create(
      MapCodec.unit(new SelectConfigType()),
      ConfigDisplayType.CODEC
  );

  @Override
  public @Nullable ConfigDisplayType get(ItemStack p_387845_, @Nullable ClientLevel p_387945_, @Nullable LivingEntity p_388349_, int p_388630_, ItemDisplayContext p_388902_) {
    if (LootrAPI.isVanillaTextures()) {
      return ConfigDisplayType.VANILLA;
    }
    return ConfigDisplayType.DEFAULT;
  }

  @Override
  public Codec<ConfigDisplayType> valueCodec() {
    return ConfigDisplayType.CODEC;
  }

  @Override
  public Type<? extends SelectItemModelProperty<ConfigDisplayType>, ConfigDisplayType> type() {
    return TYPE;
  }
}
