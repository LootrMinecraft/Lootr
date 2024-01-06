package noobanidus.mods.lootr.advancement;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import noobanidus.mods.lootr.api.advancement.IGenericPredicate;
import org.jetbrains.annotations.Nullable;

public class AdvancementPredicate implements IGenericPredicate<ResourceLocation> {
    private ResourceLocation advancementId;

    public AdvancementPredicate() {
    }

    public AdvancementPredicate(ResourceLocation advancementId) {
        this.advancementId = advancementId;
    }

    @Override
    public boolean test(ServerPlayer player, ResourceLocation condition) {
        return advancementId != null && advancementId.equals(condition);
    }

    @Override
    public IGenericPredicate<ResourceLocation> deserialize(@Nullable JsonObject element) {
        if(element == null) {
            throw new IllegalArgumentException("AdvancementPredicate requires an object");
        }
        ResourceLocation rl = new ResourceLocation(element.getAsJsonPrimitive("advancement").getAsString());
        return new AdvancementPredicate(rl);
    }
}
