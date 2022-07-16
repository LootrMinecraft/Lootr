package noobanidus.mods.lootr.compat;

import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.world.storage.loot.LootGenerationHandler;
import net.minecraft.inventory.IInventory;
import net.minecraft.world.WorldServer;
import net.minecraftforge.items.wrapper.InvWrapper;

import java.util.Random;

public class RecurrentComplexCompat {
    public static void checkInventory(WorldServer world, IInventory inventory, Random random) {
        InvWrapper wrapper = new InvWrapper(inventory);
        LootGenerationHandler.generateAllTags(world, wrapper, RecurrentComplex.specialRegistry.itemHidingMode(), random);
    }
}
