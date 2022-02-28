package net.zestyblaze.lootr.blocks;

import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.zestyblaze.lootr.registry.LootrBlockEntityInit;

import java.util.function.Supplier;

public class LootrChestBlock extends ChestBlock {
    public LootrChestBlock(Properties properties) {
        super(properties, () -> LootrBlockEntityInit.SPECIAL_LOOT_CHEST);
    }

    public LootrChestBlock(Properties properties, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
        super(properties, supplier);
    }
}
