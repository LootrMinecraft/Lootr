package noobanidus.mods.lootr.fabric.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.resources.model.BlockStateDefinitions;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import noobanidus.mods.lootr.common.client.entity.LootrBlockStateDefinitions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.HashMap;
import java.util.Map;

@Mixin(BlockStateDefinitions.class)
public class MixinBlockStateDefinitions {
  @WrapOperation(method="definitionLocationToBlockStateMapper", at=@At(value="NEW", target="java/util/HashMap"))
  private static HashMap<Identifier, StateDefinition<Block, BlockState>> lootr$injectItemFrameModels(Map<Identifier, StateDefinition<Block, BlockState>>m, Operation<HashMap<Identifier, StateDefinition<Block, BlockState>>> original) {
    m.putAll(LootrBlockStateDefinitions.getStaticDefinitions());
    return original.call(m);
  }
}
