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

import java.util.function.Supplier;

public class LootrBlockInit {
    private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LootrShulkerBlockEntity shulkerBlockEntity) {
            return shulkerBlockEntity.isClosed();
        } else {
            return false;
        }
    };

    private static LootrChestBlock chestBlock;
    public static final Supplier<LootrChestBlock> CHEST = () -> chestBlock;
    private static LootrBarrelBlock barrelBlock;
    public static final Supplier<LootrBarrelBlock> BARREL = () -> barrelBlock;
    private static LootrTrappedChestBlock trappedChestBlock;
    public static final Supplier<LootrTrappedChestBlock> TRAPPED_CHEST = () -> trappedChestBlock;
    private static LootrShulkerBlock shulkerBlock;
    public static final Supplier<LootrShulkerBlock> SHULKER = () -> shulkerBlock;

    private static LootrInventoryBlock inventoryBlock;
    public static final Supplier<LootrInventoryBlock> INVENTORY = () -> inventoryBlock;

    private static Block trophyBlock;
    public static final Supplier<Block> TROPHY = () -> trophyBlock;

    public static void registerBlocks() {
        chestBlock = new LootrChestBlock(BlockBehaviour.Properties.copy(Blocks.CHEST).strength(2.5f));
        barrelBlock = new LootrBarrelBlock(BlockBehaviour.Properties.copy(Blocks.BARREL).strength(2.5f));
        trappedChestBlock = new LootrTrappedChestBlock(BlockBehaviour.Properties.copy(Blocks.TRAPPED_CHEST).strength(2.5f));
        shulkerBlock = new LootrShulkerBlock(BlockBehaviour.Properties.of().strength(2.5f).dynamicShape().noOcclusion().isSuffocating(posPredicate).isViewBlocking(posPredicate));
        inventoryBlock = new LootrInventoryBlock(BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD));
        trophyBlock = new TrophyBlock(BlockBehaviour.Properties.of().strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel(o -> 15));
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_chest"), chestBlock);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_barrel"), barrelBlock);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_trapped_chest"), trappedChestBlock);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_shulker"), shulkerBlock);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "lootr_inventory"), inventoryBlock);
        Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(LootrAPI.MODID, "trophy"), trophyBlock);
    }
}
