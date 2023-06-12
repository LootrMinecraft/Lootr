package net.zestyblaze.lootr.registry;

import com.google.common.collect.Sets;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.blocks.*;
import net.zestyblaze.lootr.blocks.entities.LootrShulkerBlockEntity;
import net.zestyblaze.lootr.config.LootrModConfig;

import java.util.Set;

public class LootrBlockInit {
    private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if(blockEntity instanceof LootrShulkerBlockEntity shulkerBlockEntity) {
            return shulkerBlockEntity.isClosed();
        } else {
            return false;
        }
    };

    public static final LootrChestBlock CHEST = new LootrChestBlock(BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5f));
    public static final LootrBarrelBlock BARREL = new LootrBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).strength(2.5f));
    public static final LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(BlockBehaviour.Properties.copy(Blocks.TRAPPED_CHEST).strength(2.5f));
    public static final LootrShulkerBlock SHULKER = new LootrShulkerBlock(BlockBehaviour.Properties.of(Material.SHULKER_SHELL).strength(2.5f).dynamicShape().noOcclusion().isSuffocating(posPredicate).isViewBlocking(posPredicate));

    public static final LootrInventoryBlock INVENTORY = new LootrInventoryBlock(BlockBehaviour.Properties.of(Material.WOOD).strength(2.5f).sound(SoundType.WOOD));

    public static final Block TROPHY = new TrophyBlock(BlockBehaviour.Properties.of(Material.METAL).strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15));

    public static Set<Block> specialLootChests = Sets.newHashSet(CHEST, TRAPPED_CHEST, SHULKER, INVENTORY, BARREL);

    public static void registerBlocks() {
        Registry.register(Registry.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_chest"), CHEST);
        Registry.register(Registry.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_barrel"), BARREL);
        Registry.register(Registry.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_trapped_chest"), TRAPPED_CHEST);
        Registry.register(Registry.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_shulker"), SHULKER);
        Registry.register(Registry.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_inventory"), INVENTORY);
        Registry.register(Registry.BLOCK, new ResourceLocation(LootrAPI.MODID, "trophy"), TROPHY);
    }
}
