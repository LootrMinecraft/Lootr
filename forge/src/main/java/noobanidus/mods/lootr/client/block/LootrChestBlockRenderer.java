package noobanidus.mods.lootr.client.block;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.ChestRenderer;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.block.state.properties.ChestType;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.blockentity.ILootBlockEntity;
import noobanidus.mods.lootr.blocks.entities.LootrChestBlockEntity;
import noobanidus.mods.lootr.config.LootrModConfig;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class LootrChestBlockRenderer<T extends LootrChestBlockEntity & ILootBlockEntity> extends ChestRenderer<T> {
    private UUID playerId = null;
    public static final Material MATERIAL = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(LootrAPI.MODID, "chest"));
    public static final Material MATERIAL2 = new Material(InventoryMenu.BLOCK_ATLAS, new ResourceLocation(LootrAPI.MODID, "chest_opened"));

    public LootrChestBlockRenderer(BlockEntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    protected @NotNull Material getMaterial(@NotNull T blockEntity, @NotNull ChestType chestType) {
        if (LootrModConfig.isVanillaTextures()) {
            return Sheets.chooseMaterial(blockEntity, chestType, false);
        }
        if (playerId == null) {
            Player player = Minecraft.getInstance().player;
            if (player == null) {
                return MATERIAL;
            }
            playerId = player.getUUID();
        }
        if (blockEntity.isOpened() || blockEntity.getOpeners().contains(playerId)) {
            return MATERIAL2;
        }
        return MATERIAL;
    }
}
