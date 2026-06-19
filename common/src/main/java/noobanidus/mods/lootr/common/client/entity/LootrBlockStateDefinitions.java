package noobanidus.mods.lootr.common.client.entity;

import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import noobanidus.mods.lootr.common.api.LootrConstants;
import noobanidus.mods.lootr.common.client.state.LootrItemFrameRenderState;

import java.util.Map;

public class LootrBlockStateDefinitions {
  private static final StateDefinition<Block, BlockState> LOOTR_ITEM_FRAME_FAKE_DEFINITION = createItemFrameFakeState();
  private static final StateDefinition<Block, BlockState> LOOTR_OPEN_ITEM_FRAME_FAKE_DEFINITION = createItemFrameFakeState();
  public static final Identifier LOOTR_OPEN_ITEM_FRAME_LOCATION = LootrConstants.Identifiers.ITEM_FRAME.withSuffix("_open");
  public static final Identifier LOOTR_ITEM_FRAME_LOCATION = LootrConstants.Identifiers.ITEM_FRAME;
  private static final Map<Identifier, StateDefinition<Block, BlockState>> STATIC_DEFINITIONS = Map.of(
      LOOTR_ITEM_FRAME_LOCATION, LOOTR_ITEM_FRAME_FAKE_DEFINITION, LOOTR_OPEN_ITEM_FRAME_LOCATION, LOOTR_OPEN_ITEM_FRAME_FAKE_DEFINITION
  );

  private static StateDefinition<Block, BlockState> createItemFrameFakeState() {
    return new StateDefinition.Builder<Block, BlockState>(Blocks.AIR).create(Block::defaultBlockState, BlockState::new);
  }

  public static BlockState getItemFrameFakeState (boolean visuallyOpen) {
    return (visuallyOpen ? LOOTR_OPEN_ITEM_FRAME_FAKE_DEFINITION : LOOTR_ITEM_FRAME_FAKE_DEFINITION).any();
  }

  public static BlockState getItemFrameFakeState(LootrItemFrameRenderState state) {
    return (state.visuallyOpen ? LOOTR_OPEN_ITEM_FRAME_FAKE_DEFINITION : LOOTR_ITEM_FRAME_FAKE_DEFINITION).any();
  }

  public static Map<Identifier, StateDefinition<Block, BlockState>> getStaticDefinitions() {
    return STATIC_DEFINITIONS;
  }
}
