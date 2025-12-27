package noobanidus.mods.lootr.common.api;

import noobanidus.mods.lootr.common.api.data.LootrBlockType;
import noobanidus.mods.lootr.common.api.registry.LootrProperties;
import org.jetbrains.annotations.Nullable;

public class BuiltInLootrTypes {
  public static final String TYPE_CHEST = LootrAPI.rl("chest").toString();
  public static final String TYPE_TRAPPED_CHEST = LootrAPI.rl("trapped_chest").toString();
  public static final String TYPE_BARREL = LootrAPI.rl("barrel").toString();
  public static final String TYPE_SHULKER = LootrAPI.rl("shulker").toString();
  public static final String TYPE_INVENTORY = LootrAPI.rl("inventory").toString();
  public static final String TYPE_MINECART = LootrAPI.rl("minecart").toString();
  public static final String TYPE_POT = LootrAPI.rl("pot").toString();
  public static final String TYPE_SAND = LootrProperties.SUSPICIOUS_SAND.toString();
  public static final String TYPE_GRAVEL = LootrProperties.SUSPICIOUS_GRAVEL.toString();
  public static final String TYPE_ITEM_FRAME = LootrProperties.ITEM_FRAME.toString();

  public static ILootrType CHEST;
  public static ILootrType TRAPPED_CHEST;
  public static ILootrType BARREL;
  public static ILootrType SHULKER;
  public static ILootrType INVENTORY;
  public static ILootrType MINECART;
  public static ILootrType POT;
  public static ILootrType SAND;
  public static ILootrType GRAVEL;
  public static ILootrType ITEM_FRAME;

  @Nullable
  @Deprecated
  public static ILootrType fromLegacy (LootrBlockType type) {
    return switch (type) {
      case CHEST -> CHEST;
      case TRAPPED_CHEST -> TRAPPED_CHEST;
      case BARREL -> BARREL;
      case SHULKER -> SHULKER;
      case INVENTORY -> INVENTORY;
      case ENTITY -> MINECART;
      default -> null;
    };
  }
}
