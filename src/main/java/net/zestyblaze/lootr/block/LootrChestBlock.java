package net.zestyblaze.lootr.block;

import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;

import java.util.function.Supplier;

public class LootrChestBlock extends ChestBlock {
    public LootrChestBlock(Settings settings) {
        super(settings, () -> BlockEntityType.CHEST);
    }

    public LootrChestBlock(Settings settings, Supplier<BlockEntityType<? extends ChestBlockEntity>> supplier) {
        super(settings, supplier);
    }
}
