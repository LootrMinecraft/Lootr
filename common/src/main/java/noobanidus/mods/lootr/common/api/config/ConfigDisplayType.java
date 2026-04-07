package noobanidus.mods.lootr.common.api.config;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

import java.util.Locale;

public enum ConfigDisplayType implements StringRepresentable {
  DEFAULT,
  VANILLA;

  @Override
  public @NonNull String getSerializedName() {
    return name().toLowerCase(Locale.ROOT);
  }

  public static final Codec<ConfigDisplayType> CODEC = StringRepresentable.fromEnum(ConfigDisplayType::values);
}
