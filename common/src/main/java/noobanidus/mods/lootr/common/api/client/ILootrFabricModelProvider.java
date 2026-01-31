package noobanidus.mods.lootr.common.api.client;

import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.Nullable;

/**
 * Used (in 1.21.0/1.21.1) to allow add-on mods to provide custom models for
 * brushable blocks etc without having to implement baked models in their
 * own code.
 * <br />
 * Note: This is only for Fabric! For NeoForge, use the model loaders:
 * - `lootr:custom_model` requires "opened" and "unopened" model definitions
 *                        and optionally a "vanilla" model definition.
 * - `lootr:custom_barrel` is deprecated, use `custom_model` instead.
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
     * Accept a custom model definition. Note: the custom model does not
     * support the "old" vs "new" textures configuration of Lootr, and that is
     * completely ignored.
     * <br />
     * Note: for any block that uses BlockStateProprties.OPEN, you will need to
     * register two separate models, one for the "open" and one for the "closed"
     * state, and then control which is used in the blockstate file.
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
    void acceptCustomModel (Identifier modelName, Identifier modelOpenedLocation, Identifier modelUnopenedLocation, @Nullable Identifier modelVanillaLocation);

    // Use acceptCustomModel instead.
    @Deprecated
    default void acceptBarrelModel (Identifier modelName, Identifier modelOpenedLocation, Identifier modelUnopenedLocation, @Nullable Identifier modelVanillaLocation) {
      acceptCustomModel(modelName, modelOpenedLocation, modelUnopenedLocation, modelVanillaLocation);
    }

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
    void acceptBrushableModel (Identifier modelName, Identifier opened, Identifier stage0, Identifier stage1, Identifier stage2, Identifier stage3);
  }
}
