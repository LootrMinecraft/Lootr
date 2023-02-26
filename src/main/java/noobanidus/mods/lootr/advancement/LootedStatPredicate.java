package noobanidus.mods.lootr.advancement;

import com.google.gson.JsonObject;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.api.advancement.IGenericPredicate;
import noobanidus.mods.lootr.init.LootrStatsInit;
import org.jetbrains.annotations.Nullable;

public class LootedStatPredicate implements IGenericPredicate<Void> {
  private int score = -1;

  public LootedStatPredicate() {
  }

  public LootedStatPredicate(int score) {
    this.score = score;
  }

  @Override
  public boolean test(ServerPlayer player, Void condition) {
    return player.getStats().getValue(LootrStatsInit.LOOTED_STAT) >= score;
  }

  @Override
  public IGenericPredicate<Void> deserialize(@Nullable JsonObject element) {
    if (element == null) {
      throw new IllegalArgumentException("Element cannot be null");
    }

    int score = element.getAsJsonPrimitive("score").getAsInt();
    return new LootedStatPredicate(score);
  }
}
