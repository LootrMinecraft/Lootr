package noobanidus.mods.lootr.init;

import net.minecraft.util.ResourceLocation;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.advancement.trigger.GenericTrigger;

import java.util.UUID;

public class ModAdvancements {
  public static final ResourceLocation CHEST_LOCATION = new ResourceLocation(Lootr.MODID, "chest_opened");
  public static final ResourceLocation BARREL_LOCATION = new ResourceLocation(Lootr.MODID, "barrel_opened");
  public static final ResourceLocation CART_LOCATION = new ResourceLocation(Lootr.MODID, "cart_opened");
  public static final ResourceLocation ADVANCEMENT_LOCATION = new ResourceLocation(Lootr.MODID, "advancement");
  public static final ResourceLocation SCORE_LOCATION = new ResourceLocation(Lootr.MODID, "score");
  public static final ResourceLocation SHULKER_LOCATION = new ResourceLocation(Lootr.MODID, "shulker_opened");
  public static GenericTrigger<UUID> CHEST_PREDICATE = null;
  public static GenericTrigger<UUID> BARREL_PREDICATE = null;
  public static GenericTrigger<UUID> CART_PREDICATE = null;
  public static GenericTrigger<UUID> SHULKER_PREDICATE = null;
  public static GenericTrigger<Void> SCORE_PREDICATE = null;
  public static GenericTrigger<ResourceLocation> ADVANCEMENT_PREDICATE = null;

  public static void load() {
  }
}
