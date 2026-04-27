package noobanidus.mods.lootr.common.mixin.client;

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
  @WrapOperation(method="<clinit>", at=@At(value="INVOKE", target="Ljava/util/Map;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/Map;"))
  private static Map<Object, Object> lootr$injectItemFrameModels(Object k1, Object v1, Object k2, Object v2, Operation<Map<Object, Object>> original) {
    Map<Object, Object> result = original.call(k1, v1, k2, v2);
    Map<Object, Object> map = new HashMap<>(result);
    map.putAll(LootrBlockStateDefinitions.getStaticDefinitions());
    return map;
  }
}
