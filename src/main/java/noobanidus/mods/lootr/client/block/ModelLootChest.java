package noobanidus.mods.lootr.client.block;

import net.minecraft.client.model.ModelBox;
import net.minecraft.client.model.ModelChest;

/**
 * Clone of ModelChest that shrinks the inner part slightly to prevent z-fighting.
 */
public class ModelLootChest extends ModelChest {
    public ModelLootChest() {
        this.chestBelow.cubeList.remove(0);
        float zFightAdjustment = 0.01f;
        this.chestBelow.addBox(zFightAdjustment, 0.0F, zFightAdjustment, 14, 10, 14, 0.0F);
    }
}
