package net.zestyblaze.lootr.client.block;

import com.google.common.collect.Streams;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.zestyblaze.lootr.api.LootrAPI;
import net.zestyblaze.lootr.api.blockentity.ILootBlockEntity;
import net.zestyblaze.lootr.config.LootrModConfig;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BarrelModel implements UnbakedModel {
  private final UnbakedModel opened;
  private final UnbakedModel unopened;
  private final UnbakedModel vanilla;
  private final Collection<ResourceLocation> dependencies;

  public BarrelModel(UnbakedModel opened, UnbakedModel unopened, UnbakedModel vanilla) {
    this.opened = opened;
    this.unopened = unopened;
    this.vanilla = vanilla;
    this.dependencies = Streams.concat(opened.getDependencies().stream(), unopened.getDependencies().stream(), vanilla.getDependencies().stream()).collect(Collectors.toSet());
  }

  @Override
  public Collection<ResourceLocation> getDependencies() {
    return dependencies;
  }

  private Collection<Material> materials = null;

  @Override
  public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureErrors) {
    if (materials == null) {
      this.materials = Streams.concat(this.opened.getMaterials(modelGetter, missingTextureErrors).stream(), this.unopened.getMaterials(modelGetter, missingTextureErrors).stream(), this.vanilla.getMaterials(modelGetter, missingTextureErrors).stream()).collect(Collectors.toSet());
    }

    return materials;
  }

  @Nullable
  @Override
  public BakedModel bake(ModelBakery modelBakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation location) {
    return new BakedBarrelModel(opened.bake(modelBakery, spriteGetter, transform, location), unopened.bake(modelBakery, spriteGetter, transform, location), vanilla.bake(modelBakery, spriteGetter, transform, location));
  }

  public static class BakedBarrelModel implements BakedModel, FabricBakedModel {
    private final BakedModel opened;
    private final BakedModel unopened;
    private final BakedModel vanilla;

    public BakedBarrelModel(BakedModel opened, BakedModel unopened, BakedModel vanilla) {
      this.opened = opened;
      this.unopened = unopened;
      this.vanilla = vanilla;
    }

    @Override
    public boolean isVanillaAdapter() {
      return false;
    }

    @Override
    public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
      BlockEntity blockEntity = blockView.getBlockEntity(pos);
      BakedModel model = null;
      if (LootrModConfig.isVanillaTextures()) {
        model = vanilla;
      } else {
        if (blockEntity instanceof ILootBlockEntity lootContainer) {
          LocalPlayer player = Minecraft.getInstance().player;
          if (player != null && lootContainer.getOpeners().contains(player.getUUID())) {
            model = opened;
          } else {
            model = unopened;
          }
        }
      }

      if (model != null) {
        QuadEmitter emitter = context.getEmitter();
        Renderer renderer = RendererAccess.INSTANCE.getRenderer();
        if (renderer != null) {
          RenderMaterial material = renderer.materialById(RenderMaterial.MATERIAL_STANDARD);
          for (Direction dir : Direction.values()) {
            for (BakedQuad quad : model.getQuads(state, dir, randomSupplier.get())) {
              emitter.fromVanilla(quad, material, dir);
              emitter.emit();
            }
          }
        }
      }
    }

    @Override
    public void emitItemQuads(ItemStack stack, Supplier<Random> randomSupplier, RenderContext context) {
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, Random rand) {
      if (LootrModConfig.isVanillaTextures()) {
        return vanilla.getQuads(state, side, rand);
      } else {
        return unopened.getQuads(state, side, rand);
      }
    }

    @Override
    public boolean useAmbientOcclusion() {
      return true;
    }

    @Override
    public boolean isGui3d() {
      return true;
    }

    @Override
    public boolean usesBlockLight() {
      return true;
    }

    @Override
    public boolean isCustomRenderer() {
      return true;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
      return this.unopened.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
      return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
      return ItemOverrides.EMPTY;
    }
  }

  public static class BarrelModelLoader implements ModelResourceProvider {
    // Model references
    private static final ResourceLocation LOOTR_BARREL_MODEL_UNOPENED = new ResourceLocation(LootrAPI.MODID, "block/lootr_barrel");
    private static final ResourceLocation LOOTR_BARREL_MODEL_OPENED = new ResourceLocation(LootrAPI.MODID, "block/lootr_barrel_open");

    // Unopened models
    private static final ResourceLocation LOOTR_BARREL_UNOPENED = new ResourceLocation(LootrAPI.MODID, "block/lootr_barrel_unopened");
    private static final ResourceLocation LOOTR_BARREL_UNOPENED_OPEN = new ResourceLocation(LootrAPI.MODID, "block/lootr_barrel_unopened_open");

    // Opened models
    private static final ResourceLocation LOOTR_OPENED_BARREL = new ResourceLocation(LootrAPI.MODID, "block/lootr_opened_barrel");
    private static final ResourceLocation LOOTR_OPENED_BARREL_OPEN = new ResourceLocation(LootrAPI.MODID, "block/lootr_opened_barrel_open");

    // Vanilla models
    private static final ResourceLocation VANILLA = new ResourceLocation("minecraft", "block/barrel");
    private static final ResourceLocation VANILLA_OPEN = new ResourceLocation("minecraft", "block/barrel_open");

    @Override
    public @Nullable UnbakedModel loadModelResource(ResourceLocation resourceId, ModelProviderContext context) throws ModelProviderException {
      if (resourceId.equals(LOOTR_BARREL_MODEL_UNOPENED)) {
        return new BarrelModel(context.loadModel(LOOTR_BARREL_UNOPENED), context.loadModel(LOOTR_BARREL_UNOPENED_OPEN), context.loadModel(VANILLA));
      } else if (resourceId.equals(LOOTR_BARREL_MODEL_OPENED)) {
        return new BarrelModel(context.loadModel(LOOTR_OPENED_BARREL), context.loadModel(LOOTR_OPENED_BARREL_OPEN), context.loadModel(VANILLA_OPEN));
      } else {
        return null;
      }
    }
  }
}
