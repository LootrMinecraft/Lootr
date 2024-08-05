package noobanidus.mods.lootr.fabric;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import noobanidus.mods.lootr.api.LootrAPI;
import noobanidus.mods.lootr.api.registry.LootrRegistry;
import noobanidus.mods.lootr.fabric.config.LootrConfigInit;
import noobanidus.mods.lootr.fabric.event.LootrEventsInit;
import noobanidus.mods.lootr.fabric.impl.LootrAPIImpl;
import noobanidus.mods.lootr.fabric.impl.LootrRegistryImpl;
import noobanidus.mods.lootr.fabric.init.*;
import noobanidus.mods.lootr.fabric.network.to_client.PacketCloseCart;
import noobanidus.mods.lootr.fabric.network.to_client.PacketCloseContainer;
import noobanidus.mods.lootr.fabric.network.to_client.PacketOpenCart;
import noobanidus.mods.lootr.fabric.network.to_client.PacketOpenContainer;

public class Lootr implements ModInitializer {
  @Override
  public void onInitialize() {
    LootrAPI.INSTANCE = new LootrAPIImpl();
    LootrRegistry.INSTANCE = new LootrRegistryImpl();

    PayloadTypeRegistry.playS2C().register(PacketOpenCart.TYPE, PacketOpenCart.STREAM_CODEC);
    PayloadTypeRegistry.playS2C().register(PacketCloseCart.TYPE, PacketCloseCart.STREAM_CODEC);
    PayloadTypeRegistry.playS2C().register(PacketOpenContainer.TYPE, PacketOpenContainer.STREAM_CODEC);
    PayloadTypeRegistry.playS2C().register(PacketCloseContainer.TYPE, PacketCloseContainer.STREAM_CODEC);

    //   DUE TO SHENANIGANS, THIS IS NOW ACTIVATED IN
    //   LootrFabricMixinPlugin.shouldApplyMixin
    // LootrConfigInit.registerConfig();
    ModItems.registerItems();
    ModBlocks.registerBlocks();
    ModTabs.registerTabs();
    ModBlockEntities.registerBlockEntities();
    ModEntities.registerEntities();
    ModLoot.registerLoot();
    LootrEventsInit.registerEvents();
    ModStats.registerStats();
    ModAdvancements.registerAdvancements();
  }
}
