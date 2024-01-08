package noobanidus.mods.lootr.registry;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.blocks.*;
import noobanidus.mods.lootr.blocks.entities.LootrShulkerBlockEntity;

public class LootrBlockInit {
    private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LootrShulkerBlockEntity shulkerBlockEntity) {
            return shulkerBlockEntity.isClosed();
        } else {
            return false;
        }
    };

    public static final LootrChestBlock CHEST = new LootrChestBlock(BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5f));
    public static final LootrBarrelBlock BARREL = new LootrBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).strength(2.5f));
    public static final LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(BlockBehaviour.Properties.copy(Blocks.TRAPPED_CHEST).strength(2.5f));
    public static final LootrShulkerBlock SHULKER = new LootrShulkerBlock(BlockBehaviour.Properties.of().strength(2.5f).dynamicShape().noOcclusion().isSuffocating(posPredicate).isViewBlocking(posPredicate));

    public static final LootrInventoryBlock INVENTORY = new LootrInventoryBlock(BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD));

    public static final Block TROPHY = new TrophyBlock(BlockBehaviour.Properties.of().strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15));

    public static void registerBlocks() {
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_chest"), CHEST);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_barrel"), BARREL);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_trapped_chest"), TRAPPED_CHEST);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_shulker"), SHULKER);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_inventory"), INVENTORY);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "trophy"), TROPHY);
    }
}
