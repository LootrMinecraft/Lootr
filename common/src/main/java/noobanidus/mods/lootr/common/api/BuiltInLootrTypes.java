package noobanidus.mods.lootr.common.api;

import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;

public final class BuiltInLootrTypes {
  public static final String TYPE_CHEST = LootrConstants.Identifiers.CHEST.toString();
  public static final String TYPE_TRAPPED_CHEST = LootrConstants.Identifiers.TRAPPED_CHEST.toString();
  public static final String TYPE_BARREL = LootrConstants.Identifiers.BARREL.toString();
  public static final String TYPE_SHULKER_BOX = LootrConstants.Identifiers.SHULKER_BOX.toString();
  @Deprecated
  public static final String TYPE_INVENTORY = LootrConstants.Identifiers.INVENTORY.toString();
  public static final String TYPE_MINECART = LootrConstants.Identifiers.MINECART_WITH_CHEST.toString();
  public static final String TYPE_DECORATED_POT = LootrConstants.Identifiers.DECORATED_POT.toString();
  public static final String TYPE_SAND = LootrConstants.Identifiers.SUSPICIOUS_SAND.toString();
  public static final String TYPE_GRAVEL = LootrConstants.Identifiers.SUSPICIOUS_GRAVEL.toString();
  public static final String TYPE_ITEM_FRAME = LootrConstants.Identifiers.ITEM_FRAME.toString();
  public static final String TYPE_SIMPLE = LootrConstants.Identifiers.SIMPLE.toString();

  public static ILootrType CHEST;
  public static ILootrType TRAPPED_CHEST;
  public static ILootrType BARREL;
  public static ILootrType SHULKER_BOX;
  @Deprecated
  public static ILootrType INVENTORY;
  public static ILootrType MINECART;
  public static ILootrType DECORATED_POT;
  public static ILootrType SAND;
  public static ILootrType GRAVEL;
  public static ILootrType ITEM_FRAME;
  public static ILootrType SIMPLE;
}
