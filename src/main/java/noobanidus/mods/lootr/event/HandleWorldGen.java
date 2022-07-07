package noobanidus.mods.lootr.event;

import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.block.tile.LootrInventoryTileEntity;
import noobanidus.mods.lootr.config.ConfigManager;
import noobanidus.mods.lootr.init.ModBlocks;
import org.apache.commons.lang3.tuple.Pair;

import java.lang.ref.WeakReference;
import java.util.*;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public class HandleWorldGen {
    private static LinkedList<Pair<WeakReference<World>, ChunkPos>> generatedChunks = new LinkedList<>();
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onGenerate(PopulateChunkEvent.Post event) {
        if (ConfigManager.CONVERT_WORLDGEN_INVENTORIES) {
            generatedChunks.add(Pair.of(new WeakReference<>(event.getWorld()), new ChunkPos(event.getChunkX(), event.getChunkZ())));
        }
    }

    public static TileEntity replaceOldLootBlockAt(Chunk chunk, BlockPos worldPos, IBlockState newState) {
        World world = chunk.getWorld();
        world.destroyBlock(worldPos, false);
        TileEntity prevTe = chunk.getTileEntityMap().remove(worldPos);
        if(prevTe != null)
            prevTe.invalidate();
        world.setBlockState(worldPos, newState, 2);
        return chunk.getTileEntity(worldPos, Chunk.EnumCreateEntityType.IMMEDIATE);
    }

    private static void processChunkForWorldgen(Chunk chunk) {
        List<TileEntity> tileEntities = new ArrayList<>(chunk.getTileEntityMap().values());
        for (TileEntity te : tileEntities) {
            if (te instanceof TileEntityLockableLoot && !(te instanceof ILootTile)) {
                TileEntityLockableLoot teLockable = (TileEntityLockableLoot) te;
                if (teLockable.lootTable == null) {
                    NonNullList<ItemStack> newInventory = NonNullList.create();
                    int size = teLockable.getSizeInventory();
                    for (int slot = 0; slot < size; slot++) {
                        ItemStack stack = teLockable.getStackInSlot(slot);
                        if (!stack.isEmpty() || size <= 27) {
                            newInventory.add(stack.copy());
                        }
                        teLockable.setInventorySlotContents(slot, ItemStack.EMPTY);
                    }
                    if (newInventory.size() > 0) {
                        BlockPos pos = te.getPos();
                        teLockable.clear();
                        IBlockState currentBlockState = chunk.getBlockState(pos);
                        EnumFacing newFacing = EnumFacing.SOUTH;
                        if (currentBlockState.getPropertyKeys().contains(BlockChest.FACING)) {
                            newFacing = currentBlockState.getValue(BlockChest.FACING);
                        }
                        IBlockState replacement = ModBlocks.INVENTORY.getDefaultState().withProperty(BlockChest.FACING, newFacing);
                        te = replaceOldLootBlockAt(chunk, pos, replacement);
                        if (te instanceof LootrInventoryTileEntity) {
                            LootrInventoryTileEntity inventory = (LootrInventoryTileEntity) te;
                            inventory.setCustomInventory(newInventory);
                            inventory.markDirty();
                        } else {
                            Lootr.LOG.error("replacement TE " + te + " is not an LootrInventoryTileEntity dim " + chunk.getWorld().provider.getDimension() + " at " + pos);
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (ConfigManager.CONVERT_WORLDGEN_INVENTORIES && event.phase == TickEvent.Phase.END && generatedChunks.size() > 0) {
            generatedChunks.removeIf(pair -> {
                World world = pair.getLeft().get();
                if(world == null)
                   return true;
                Chunk chunk = world.getChunkProvider().getLoadedChunk(pair.getRight().x, pair.getRight().z);
                if(chunk == null)
                    return false;
                processChunkForWorldgen(chunk);
                return true;
            });
        }
    }
}
