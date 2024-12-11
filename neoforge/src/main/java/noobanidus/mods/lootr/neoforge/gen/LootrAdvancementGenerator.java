package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.critereon.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import noobanidus.mods.lootr.common.advancement.AdvancementTrigger;
import noobanidus.mods.lootr.common.advancement.ContainerTrigger;
import noobanidus.mods.lootr.common.advancement.LootedStatTrigger;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

import java.util.function.Consumer;

public class LootrAdvancementGenerator implements AdvancementProvider.AdvancementGenerator {
  @Override
  public void generate(HolderLookup.Provider arg, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {
    AdvancementHolder lootrRoot = Advancement.Builder.advancement().display(Blocks.CHEST, Component.translatable("lootr.advancements.root.title"), Component.translatable("lootr.advancements.root.description"), ResourceLocation.parse("minecraft:textures/block/dark_oak_log.png"), AdvancementType.TASK, false, false, false).addCriterion("always_true", PlayerTrigger.TriggerInstance.tick()).save(consumer, LootrAPI.rl("root"), existingFileHelper);
    AdvancementHolder one_barrel = Advancement.Builder.advancement().parent(lootrRoot).display(LootrRegistry.getBarrelBlock(), Component.translatable("lootr.advancements.1barrel.title"), Component.translatable("lootr.advancements.1barrel.description"), null, AdvancementType.TASK, true, true, false).addCriterion("opened_barrel", ContainerTrigger.looted(LootrRegistry.getBarrelTrigger())).save(consumer, LootrAPI.rl("1barrel"), existingFileHelper);
    // 1cart
    AdvancementHolder one_cart = Advancement.Builder.advancement().parent(lootrRoot).display(Items.CHEST_MINECART, Component.translatable("lootr.advancements.1cart.title"), Component.translatable("lootr.advancements.1cart.description"), null, AdvancementType.TASK, true, true, false).addCriterion("opened_cart", ContainerTrigger.looted(LootrRegistry.getCartTrigger())).save(consumer, LootrAPI.rl("1cart"), existingFileHelper);
    // 1chest
    AdvancementHolder one_chest = Advancement.Builder.advancement().parent(lootrRoot).display(LootrRegistry.getChestBlock(), Component.translatable("lootr.advancements.1chest.title"), Component.translatable("lootr.advancements.1chest.description"), null, AdvancementType.TASK, true, true, false).addCriterion("opened_chest", ContainerTrigger.looted(LootrRegistry.getChestTrigger())).save(consumer, LootrAPI.rl("1chest"), existingFileHelper);
    // 1shulker
    AdvancementHolder one_shulker = Advancement.Builder.advancement().parent(lootrRoot).display(LootrRegistry.getShulkerBlock(), Component.translatable("lootr.advancements.1shulker.title"), Component.translatable("lootr.advancements.1shulker.description"), null, AdvancementType.TASK, true, true, false).addCriterion("opened_shulker", ContainerTrigger.looted(LootrRegistry.getShulkerTrigger())).save(consumer, LootrAPI.rl("1shulker"), existingFileHelper);
    // 10loot
    AdvancementHolder ten_loot = Advancement.Builder.advancement().parent(one_chest).display(Blocks.GOLD_BLOCK, Component.translatable("lootr.advancements.10loot.title"), Component.translatable("lootr.advancements.10loot.description"), null, AdvancementType.TASK, true, true, false).addCriterion("opened_10", LootedStatTrigger.looted(10)).save(consumer, LootrAPI.rl("10loot"), existingFileHelper);
    // 25loot
    AdvancementHolder twentyfive_loot = Advancement.Builder.advancement().parent(ten_loot).display(Blocks.EMERALD_BLOCK, Component.translatable("lootr.advancements.25loot.title"), Component.translatable("lootr.advancements.25loot.description"), null, AdvancementType.TASK, true, true, false).addCriterion("opened_25", LootedStatTrigger.looted(25)).save(consumer, LootrAPI.rl("25loot"), existingFileHelper);
    // 50loot
    AdvancementHolder fifty_loot = Advancement.Builder.advancement().parent(twentyfive_loot).display(Blocks.DIAMOND_BLOCK, Component.translatable("lootr.advancements.50loot.title"), Component.translatable("lootr.advancements.50loot.description"), null, AdvancementType.TASK, true, true, false).addCriterion("opened_50", LootedStatTrigger.looted(50)).save(consumer, LootrAPI.rl("50loot"), existingFileHelper);
    // 100loot
    Advancement.Builder.advancement().parent(fifty_loot).display(Blocks.NETHERITE_BLOCK, Component.translatable("lootr.advancements.100loot.title"), Component.translatable("lootr.advancements.100loot.description"), null, AdvancementType.CHALLENGE, true, true, false).addCriterion("opened_100", LootedStatTrigger.looted(100)).rewards(AdvancementRewards.Builder.loot(LootrAPI.TROPHY_REWARD)).save(consumer, LootrAPI.rl("100loot"), existingFileHelper);
    Advancement.Builder.advancement().parent(one_chest).display(Items.ENCHANTED_GOLDEN_APPLE, Component.translatable("lootr.advancements.social.title"), Component.translatable("lootr.advancements.social.description"), null, AdvancementType.CHALLENGE, true, true, true).addCriterion("opened_chest", AdvancementTrigger.completed(one_chest.id())).addCriterion("opened_barrel", AdvancementTrigger.completed(one_barrel.id())).addCriterion("opened_cart", AdvancementTrigger.completed(one_cart.id())).addCriterion("opened_shulker", AdvancementTrigger.completed(one_shulker.id())).save(consumer, LootrAPI.rl("social"), existingFileHelper);
  }
}
