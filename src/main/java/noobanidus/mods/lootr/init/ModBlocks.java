package noobanidus.mods.lootr.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.block.LootrBarrelBlock;
import noobanidus.mods.lootr.block.LootrChestBlock;
import noobanidus.mods.lootr.block.LootrInventoryBlock;
import noobanidus.mods.lootr.block.LootrShulkerBlock;
import noobanidus.mods.lootr.block.LootrTrappedChestBlock;
import noobanidus.mods.lootr.block.TrophyBlock;
import noobanidus.mods.lootr.block.entities.LootrShulkerBlockEntity;

public class ModBlocks {
    public static final LootrChestBlock CHEST = new LootrChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.CHEST).strength(2.5f));
    public static final LootrBarrelBlock BARREL = new LootrBarrelBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BARREL).strength(2.5f).forceSolidOff());
    public static final LootrTrappedChestBlock TRAPPED_CHEST = new LootrTrappedChestBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TRAPPED_CHEST).strength(2.5f));
    public static final LootrInventoryBlock INVENTORY = new LootrInventoryBlock(BlockBehaviour.Properties.of().strength(2.5f).sound(SoundType.WOOD));
    public static final Block TROPHY = new TrophyBlock(BlockBehaviour.Properties.of().strength(15f).sound(SoundType.METAL).noOcclusion().lightLevel((o) -> 15));
    private static final BlockBehaviour.StatePredicate posPredicate = (state, level, pos) -> {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof LootrShulkerBlockEntity shulkerBlockEntity) {
            return shulkerBlockEntity.isClosed();
        } else {
            return false;
        }
    };
    public static final LootrShulkerBlock SHULKER = new LootrShulkerBlock(BlockBehaviour.Properties.of().strength(2.5f).dynamicShape().noOcclusion().isSuffocating(posPredicate).isViewBlocking(posPredicate));

    public static void registerBlocks() {
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "lootr_chest"), CHEST);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "lootr_barrel"), BARREL);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "lootr_trapped_chest"), TRAPPED_CHEST);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "lootr_shulker"), SHULKER);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "lootr_inventory"), INVENTORY);
        Registry.register(BuiltInRegistries.BLOCK, ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "trophy"), TROPHY);
    }
}
