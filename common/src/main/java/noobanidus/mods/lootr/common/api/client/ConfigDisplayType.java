package noobanidus.mods.lootr.common.api.client;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;

import java.util.Locale;

public enum ConfigDisplayType implements StringRepresentable {
  DEFAULT,
  VANILLA;

  @Override
  public String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static final Codec<ConfigDisplayType> CODEC = StringRepresentable.fromEnum(ConfigDisplayType::values);
}
