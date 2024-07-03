package noobanidus.mods.lootr.client.block;

import com.google.common.collect.Streams;
import net.fabricmc.fabric.api.renderer.v1.Renderer;
import net.fabricmc.fabric.api.renderer.v1.RendererAccess;
import net.fabricmc.fabric.api.renderer.v1.material.RenderMaterial;
import net.fabricmc.fabric.api.renderer.v1.mesh.QuadEmitter;
import net.fabricmc.fabric.api.renderer.v1.model.FabricBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.fabricmc.fabric.api.rendering.data.v1.RenderAttachedBlockView;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.config.ConfigManager;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class BarrelModel implements UnbakedModel {
    private final UnbakedModel opened;
    private final UnbakedModel unopened;
    private final UnbakedModel vanilla;
    private final UnbakedModel old_opened;
    private final UnbakedModel old_unopened;
    private Collection<ResourceLocation> dependencies = null;

    public BarrelModel(UnbakedModel opened, UnbakedModel unopened, UnbakedModel vanilla, UnbakedModel old_opened, UnbakedModel old_unopened) {
        this.opened = opened;
        this.unopened = unopened;
        this.vanilla = vanilla;
        this.old_opened = old_opened;
        this.old_unopened = old_unopened;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        if (dependencies == null) {
            this.dependencies = Streams.concat(opened.getDependencies().stream(), unopened.getDependencies().stream(), vanilla.getDependencies().stream(), old_opened.getDependencies().stream(), old_unopened.getDependencies().stream()).collect(Collectors.toSet());
        }
        return dependencies;
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> function) {
        this.opened.resolveParents(function);
        this.unopened.resolveParents(function);
        this.vanilla.resolveParents(function);
        this.old_opened.resolveParents(function);
        this.old_unopened.resolveParents(function);
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBaker modelBakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation location) {
        return new BakedBarrelModel(opened.bake(modelBakery, spriteGetter, transform, location), unopened.bake(modelBakery, spriteGetter, transform, location), vanilla.bake(modelBakery, spriteGetter, transform, location), old_opened.bake(modelBakery, spriteGetter, transform, location), old_unopened.bake(modelBakery, spriteGetter, transform, location));
    }

    public static class BakedBarrelModel implements BakedModel, FabricBakedModel {
        private final BakedModel opened;
        private final BakedModel unopened;
        private final BakedModel vanilla;
        private final BakedModel old_opened;
        private final BakedModel old_unopened;

        public BakedBarrelModel(BakedModel opened, BakedModel unopened, BakedModel vanilla, BakedModel old_opened, BakedModel old_unopened) {
            this.opened = opened;
            this.unopened = unopened;
            this.vanilla = vanilla;
            this.old_opened = old_opened;
            this.old_unopened = old_unopened;
        }

        @Override
        public boolean isVanillaAdapter() {
            return false;
        }

        @Override
        public void emitBlockQuads(BlockAndTintGetter blockView, BlockState state, BlockPos pos, Supplier<RandomSource> randomSupplier, RenderContext context) {
            Object data = ((RenderAttachedBlockView) blockView).getBlockEntityRenderAttachment(pos);
            BakedModel model = ConfigManager.get().client.old_textures ? old_unopened : unopened;
            if (ConfigManager.isVanillaTextures()) {
                model = vanilla;
            } else if (data == Boolean.TRUE) {
                model = ConfigManager.get().client.old_textures ? old_opened : opened;
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
                    for (BakedQuad quad : model.getQuads(state, null, randomSupplier.get())) {
                        emitter.fromVanilla(quad, material, null);
                        emitter.emit();
                    }
                }
            }
        }

        @Override
        public void emitItemQuads(ItemStack stack, Supplier<RandomSource> randomSupplier, RenderContext context) {
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, RandomSource rand) {
            // TODO: I don't think is ever being called
            if (ConfigManager.isVanillaTextures()) {
                return vanilla.getQuads(state, side, rand);
            } else if (ConfigManager.get().client.old_textures) {
                return old_unopened.getQuads(state, side, rand);
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
        private static final ResourceLocation LOOTR_BARREL_MODEL_UNOPENED = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/lootr_barrel");
        private static final ResourceLocation LOOTR_BARREL_MODEL_OPENED = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/lootr_barrel_open");

        // Unopened models
        private static final ResourceLocation LOOTR_BARREL_UNOPENED = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/lootr_barrel_unopened");
        private static final ResourceLocation LOOTR_BARREL_UNOPENED_OPEN = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/lootr_barrel_unopened_open");

        // Opened models
        private static final ResourceLocation LOOTR_OPENED_BARREL = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/lootr_opened_barrel");
        private static final ResourceLocation LOOTR_OPENED_BARREL_OPEN = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/lootr_opened_barrel_open");

        // Vanilla models
        private static final ResourceLocation VANILLA = ResourceLocation.fromNamespaceAndPath("minecraft", "block/barrel");
        private static final ResourceLocation VANILLA_OPEN = ResourceLocation.fromNamespaceAndPath("minecraft", "block/barrel_open");

        // Old unopened models
        private static final ResourceLocation OLD_LOOTR_BARREL_UNOPENED = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/old_lootr_barrel_unopened");
        private static final ResourceLocation OLD_LOOTR_BARREL_UNOPENED_OPEN = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/old_lootr_barrel_unopened_open");

        // Old opened models
        private static final ResourceLocation OLD_LOOTR_OPENED_BARREL = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/old_lootr_opened_barrel");
        private static final ResourceLocation OLD_LOOTR_OPENED_BARREL_OPEN = ResourceLocation.fromNamespaceAndPath(LootrAPI.MODID, "block/old_lootr_opened_barrel_open");

        @Override
        public @Nullable UnbakedModel loadModelResource(ResourceLocation resourceId, ModelProviderContext context) throws ModelProviderException {
            if (resourceId.equals(LOOTR_BARREL_MODEL_UNOPENED)) {
                return new BarrelModel(context.loadModel(LOOTR_OPENED_BARREL), context.loadModel(LOOTR_BARREL_UNOPENED), context.loadModel(VANILLA), context.loadModel(OLD_LOOTR_OPENED_BARREL), context.loadModel(OLD_LOOTR_BARREL_UNOPENED));
            } else if (resourceId.equals(LOOTR_BARREL_MODEL_OPENED)) {
                return new BarrelModel(context.loadModel(LOOTR_OPENED_BARREL_OPEN), context.loadModel(LOOTR_BARREL_UNOPENED_OPEN), context.loadModel(VANILLA_OPEN), context.loadModel(OLD_LOOTR_OPENED_BARREL_OPEN), context.loadModel(OLD_LOOTR_BARREL_UNOPENED_OPEN));
            } else {
                return null;
            }
        }
    }
}
