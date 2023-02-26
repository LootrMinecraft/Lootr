package noobanidus.mods.lootr.init;

import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.advancement.GenericTrigger;
import noobanidus.mods.lootr.api.LootrAPI;

import java.util.UUID;

public class ModAdvancements {
  public static final ResourceLocation CHEST_LOCATION = new ResourceLocation(LootrAPI.MODID, "chest_opened");
  public static final ResourceLocation BARREL_LOCATION = new ResourceLocation(LootrAPI.MODID, "barrel_opened");
  public static final ResourceLocation CART_LOCATION = new ResourceLocation(LootrAPI.MODID, "cart_opened");
  public static final ResourceLocation SHULKER_LOCATION = new ResourceLocation(LootrAPI.MODID, "shulker_opened");
  public static final ResourceLocation ADVANCEMENT_LOCATION = new ResourceLocation(LootrAPI.MODID, "advancement");
  public static final ResourceLocation LOOT_TABLE_LOCATION = new ResourceLocation(LootrAPI.MODID, "loot_table");
  public static final ResourceLocation SCORE_LOCATION = new ResourceLocation(LootrAPI.MODID, "score");
  public static GenericTrigger<UUID> CHEST_PREDICATE = null;
  public static GenericTrigger<UUID> BARREL_PREDICATE = null;
  public static GenericTrigger<UUID> CART_PREDICATE = null;
  public static GenericTrigger<UUID> SHULKER_PREDICATE = null;
  public static GenericTrigger<Void> SCORE_PREDICATE = null;

  public static GenericTrigger<ResourceLocation> LOOT_TABLE_PREDICATE = null;
  public static GenericTrigger<ResourceLocation> ADVANCEMENT_PREDICATE = null;

  public static void load() {
  }
}
