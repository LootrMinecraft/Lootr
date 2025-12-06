package noobanidus.mods.lootr.common.api;

public class BuiltInLootrTypes {
  public static final String TYPE_CHEST = LootrAPI.rl("chest").toString();
  public static final String TYPE_TRAPPED_CHEST = LootrAPI.rl("trapped_chest").toString();
  public static final String TYPE_BARREL = LootrAPI.rl("barrel").toString();
  public static final String TYPE_SHULKER = LootrAPI.rl("shulker").toString();
  public static final String TYPE_INVENTORY = LootrAPI.rl("inventory").toString();
  public static final String TYPE_MINECART = LootrAPI.rl("minecart").toString();

  public static ILootrType CHEST;
  public static ILootrType TRAPPED_CHEST;
  public static ILootrType BARREL;
  public static ILootrType SHULKER;
  public static ILootrType INVENTORY;
  public static ILootrType MINECART;
}
