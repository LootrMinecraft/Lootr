package noobanidus.mods.lootr.common.api;

import noobanidus.mods.lootr.common.api.interfaces.type.ILootrType;

public final class BuiltInLootrTypes {
  public static final String TYPE_CHEST = LootrConstants.CHEST.toString();
  public static final String TYPE_TRAPPED_CHEST = LootrConstants.TRAPPED_CHEST.toString();
  public static final String TYPE_BARREL = LootrConstants.BARREL.toString();
  public static final String TYPE_SHULKER_BOX = LootrConstants.SHULKER_BOX.toString();
  public static final String TYPE_INVENTORY = LootrConstants.INVENTORY.toString();
  public static final String TYPE_MINECART = LootrConstants.MINECART_WITH_CHEST.toString();
  public static final String TYPE_DECORATED_POT = LootrConstants.DECORATED_POT.toString();
  public static final String TYPE_SAND = LootrConstants.SUSPICIOUS_SAND.toString();
  public static final String TYPE_GRAVEL = LootrConstants.SUSPICIOUS_GRAVEL.toString();
  public static final String TYPE_ITEM_FRAME = LootrConstants.ITEM_FRAME.toString();
  public static final String TYPE_SIMPLE = LootrConstants.SIMPLE.toString();

  public static ILootrType CHEST;
  public static ILootrType TRAPPED_CHEST;
  public static ILootrType BARREL;
  public static ILootrType SHULKER_BOX;
  public static ILootrType INVENTORY;
  public static ILootrType MINECART;
  public static ILootrType DECORATED_POT;
  public static ILootrType SAND;
  public static ILootrType GRAVEL;
  public static ILootrType ITEM_FRAME;
  public static ILootrType SIMPLE;
}
