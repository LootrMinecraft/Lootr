/*package noobanidus.mods.lootr.client;

import net.minecraft.block.BarrelBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IEnviromentBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.client.model.ModelDataManager;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.client.model.data.IModelData;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.tiles.SpecialLootBarrelTile;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class SpecialLootBarrelTileRenderer<T extends SpecialLootBarrelTile> extends TileEntityRenderer<T> {
  private Map<BlockState, BlockState> stateMap = new HashMap<>();

  @Override
  public void render(T tile, double x, double y, double z, float partial, int damage) {
    if (tile.isSpecialLootChest() && tile.getWorld() != null) {
      if (stateMap.isEmpty()) {
        buildStateMap();
      }

      BlockPos pos = tile.getPos();
      IEnviromentBlockReader world = MinecraftForgeClient.getRegionRenderCache(tile.getWorld(), pos);
      BlockState state = world.getBlockState(pos);
      BlockState model = stateMap.get(state);

      if (model == null) {
        Lootr.LOG.error("Invalid state for Barrel, equivalent not found in Lootr barrel map: " + state + " at " + x + "," + y + "," + z);
        return;
      }
      Minecraft mc = Minecraft.getInstance();
      BlockRendererDispatcher renderer = mc.getBlockRendererDispatcher();
      IBakedModel bakedModel = renderer.getModelForState(model);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder buffer = tessellator.getBuffer();
      renderer.getBlockModelRenderer().renderModel(mc.world, bakedModel, model, pos, buffer, false, new Random(), state.getPositionRandom(pos), EmptyModelData.INSTANCE);


*//*      IBakedModel modelBaked = mc.getBlockRendererDispatcher().getBlockModelShapes().getModel(state);
      IModelData data = modelBaked.getModelData(world, pos, state, ModelDataManager.getModelData(tile.getWorld(), pos));*//*
*//*      mc.getBlockRendererDispatcher().getBlockModelRenderer().renderModel(world, modelBaked, state, pos, buffer, false, new Random(), 42, data);*//*
*//*      mc.getBlockRendererDispatcher().renderBlock(model, pos, world, buffer, new Random(), EmptyModelData.INSTANCE);*//*
    }
  }

  private void buildStateMap() {
    stateMap = new HashMap<>();

    BlockState baseBarrel = Blocks.BARREL.getDefaultState();
    BlockState baseLootr = ModBlocks.BARREL.getDefaultState();

    stateMap.put(baseBarrel, baseLootr);

    for (Direction facing : Direction.values()) {
      stateMap.put(
          baseBarrel.with(BarrelBlock.PROPERTY_FACING, facing).with(BarrelBlock.PROPERTY_OPEN, true),
          baseLootr.with(BarrelBlock.PROPERTY_FACING, facing).with(BarrelBlock.PROPERTY_OPEN, true));
      stateMap.put(
          baseBarrel.with(BarrelBlock.PROPERTY_FACING, facing).with(BarrelBlock.PROPERTY_OPEN, false),
          baseLootr.with(BarrelBlock.PROPERTY_FACING, facing).with(BarrelBlock.PROPERTY_OPEN, false));
    }
  }
}*/
