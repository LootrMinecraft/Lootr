package noobanidus.mods.lootr.advancement;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.api.advancement.IGenericPredicate;
import org.jetbrains.annotations.Nullable;

public class LootTablePredicate implements IGenericPredicate<ResourceLocation> {
  private ResourceLocation lootTable;

  public LootTablePredicate() {
  }

  public LootTablePredicate(ResourceLocation lootTable) {
    this.lootTable = lootTable;
  }

  @Override
  public boolean test(ServerPlayer player, ResourceLocation location) {
    return lootTable != null && lootTable.equals(location);
  }

  @Override
  public IGenericPredicate<ResourceLocation> deserialize(@Nullable  JsonObject element) {
    if (element == null) {
      throw new IllegalArgumentException("LootTablePredicate requires an object");
    }
    ResourceLocation rl = new ResourceLocation(element.getAsJsonPrimitive("loot_table").getAsString());
    return new LootTablePredicate(rl);
  }
}
