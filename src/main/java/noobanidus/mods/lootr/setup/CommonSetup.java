package noobanidus.mods.lootr.setup;

import com.google.common.collect.ImmutableSet;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;

import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.GameData;
import noobanidus.mods.lootr.advancement.*;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.impl.LootrAPIImpl;
import noobanidus.mods.lootr.init.ModAdvancements;
import noobanidus.mods.lootr.init.ModBlocks;
import noobanidus.mods.lootr.init.ModLoot;
import noobanidus.mods.lootr.init.ModStats;
import noobanidus.mods.lootr.network.PacketHandler;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(modid= LootrAPI.MODID, bus= Mod.EventBusSubscriber.Bus.MOD)
public class CommonSetup {
  @SubscribeEvent
  public static void init(FMLCommonSetupEvent event) {
    LootrAPI.INSTANCE = new LootrAPIImpl();

    event.enqueueWork(() -> {
      ModLoot.register();
      ModAdvancements.CHEST_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.CHEST_LOCATION, new ContainerPredicate()));
      ModAdvancements.BARREL_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.BARREL_LOCATION, new ContainerPredicate()));
      ModAdvancements.CART_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.CART_LOCATION, new ContainerPredicate()));
      ModAdvancements.SHULKER_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.SHULKER_LOCATION, new ContainerPredicate()));
      ModAdvancements.ADVANCEMENT_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.ADVANCEMENT_LOCATION, new AdvancementPredicate()));
      Registry.register(Registry.CUSTOM_STAT, ModStats.LOOTED_LOCATION, ModStats.LOOTED_LOCATION);
      ModStats.load();
      ModAdvancements.SCORE_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.SCORE_LOCATION, new LootedStatPredicate()));
      ModAdvancements.LOOT_TABLE_PREDICATE = CriteriaTriggers.register(new GenericTrigger<>(ModAdvancements.LOOT_TABLE_LOCATION, new LootTablePredicate()));
      PacketHandler.registerMessages();

      PoiType fisherman = ForgeRegistries.POI_TYPES.getValue(new ResourceLocation("minecraft", "fisherman"));
      Set<BlockState> states = new HashSet<>(fisherman.matchingStates);
      states.addAll(ModBlocks.BARREL.getStateDefinition().getPossibleStates());
      fisherman.matchingStates = ImmutableSet.copyOf(states);
      for (BlockState state : ModBlocks.BARREL.getStateDefinition().getPossibleStates()) {
        GameData.getBlockStatePointOfInterestTypeMap().put(state, fisherman);
      }
    });
  }
}
