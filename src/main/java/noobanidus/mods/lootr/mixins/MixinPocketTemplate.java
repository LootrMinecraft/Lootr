package noobanidus.mods.lootr.mixins;

import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.event.HandleWorldGen;
import org.dimdev.dimdoors.shared.pockets.PocketTemplate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Random;

@Mixin(PocketTemplate.class)
public class MixinPocketTemplate {
    @Redirect(method = "setup", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/storage/loot/LootTable;fillInventory(Lnet/minecraft/inventory/IInventory;Ljava/util/Random;Lnet/minecraft/world/storage/loot/LootContext;)V"))
    private void fillViaLootr(LootTable table, IInventory inventory, Random random, LootContext context) {
        if(inventory instanceof TileEntityChest) {
            /* No way to convert LootTable to ResourceLocation */
            ResourceLocation tableLoc = new ResourceLocation("dimdoors:dungeon_chest");
            if(!ConfigManager.isBlacklisted(tableLoc)) {
                BlockPos pos = ((TileEntity)inventory).getPos();
                WorldServer world = context.getWorld();
                IBlockState replacement = ConfigManager.replacement(world.getBlockState(pos));
                if(replacement != null) {
                    TileEntity lootrChest = HandleWorldGen.replaceOldLootBlockAt(world.getChunk(pos), pos, replacement);
                    if(lootrChest instanceof ILootTile) {
                        ((TileEntityLockableLoot)lootrChest).setLootTable(tableLoc, random.nextLong());
                    } else
                        Lootr.LOG.error("replacement TE " + lootrChest + " is not an ILootTile dim " + world.provider.getDimension() + " at " + pos);
                    return;
                }
            }
        }
        table.fillInventory(inventory, random, context);
    }
}
