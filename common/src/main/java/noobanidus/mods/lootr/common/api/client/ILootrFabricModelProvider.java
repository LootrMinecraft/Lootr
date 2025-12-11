package noobanidus.mods.lootr.common.api.client;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * Used (in 1.21.0/1.21.1) to allow add-on mods to provide custom barrel
 * and brushable blocks without having to implement baked models in their
 * own code.
 * <br />
 * Note: This is only for Fabric! For NeoForge, use the model loaders:
 * - `lootr:custom_barrel` requires "unopened" and "opened" model definitions,
 *                         and optionally a "vanilla" model definition.
 * - `lootr:brushable` requires an "opened" model definition and the four "stage"
 *                     of brushable models.
 * <br />
 * This only deals with resource locations and thus should not cause and class loading
 * that would result in sidedness violations.
 */
public interface ILootrFabricModelProvider {
  void provideModels(Acceptor acceptor);

  interface Acceptor {
    /**
     * Accept a barrel model definition. Note: the custom barrel model does not
     * support the "old" vs "new" textures configuration of Lootr, and that is
     * completely ignored.
     * <br />
     * Note: you will need to register two models for your barrel: one for the
     * barrel's "open" block state, and one for its "closed" block state. You can
     * consult Lootr's `lootr_barrel` blockstate as an example.
     * <br />
     * @param modelName The resource location of the model. This parameter will be
     *                  the resource location used in the blockstate json.
     * @param modelOpenedLocation The resource location of the "opened" model. This
     *                            should be visually distinct from the "unopened" variant.
     * @param modelUnopenedLocation The resource location of the "unopened" model.
     * @param modelVanillaLocation @Nullable The resource location of your "vanilla" texture,
     *                             if any exists. If you do not have a separate "original"
     *                             texture in contrast to a "Lootr-ified" texture for the open/
     *                             unopened states, you can just pass null here.
     */
    void acceptBarrelModel (ResourceLocation modelName, ResourceLocation modelOpenedLocation, ResourceLocation modelUnopenedLocation, @Nullable ResourceLocation modelVanillaLocation);

    /**
     * Accept a brushable model definition.
     * <br />
     * For an example of this, see noobanidus.mods.lootr.fabric.impl.models.FabricBrushablesProvider
     *
     * @param modelName The resource location of the model. This should be used in your blockstate
     *                  json.
     * @param opened The resource location of the "opened" model.
     * @param stage0
     * @param stage1
     * @param stage2
     * @param stage3
     */
    void acceptBrushableModel (ResourceLocation modelName, ResourceLocation opened, ResourceLocation stage0, ResourceLocation stage1, ResourceLocation stage2, ResourceLocation stage3);
  }
}
