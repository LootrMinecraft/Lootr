package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.criterion.PlayerTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.advancements.AdvancementSubProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import noobanidus.mods.lootr.common.advancement.AdvancementTrigger;
import noobanidus.mods.lootr.common.advancement.ContainerTrigger;
import noobanidus.mods.lootr.common.advancement.LootedStatTrigger;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

import java.util.function.Consumer;

public class LootrAdvancementGenerator implements AdvancementSubProvider {
  @Override
  public void generate(HolderLookup.Provider arg, Consumer<AdvancementHolder> consumer) {
    AdvancementHolder lootrRoot = Advancement.Builder.advancement()
        .display(Blocks.CHEST, Component.translatable("lootr.advancements.root.title"), Component.translatable("lootr.advancements.root.description"), Identifier.parse("minecraft:block/dark_oak_log"), AdvancementType.TASK, false, false, false)
        .addCriterion("always_true", PlayerTrigger.TriggerInstance.tick()).save(consumer, LootrAPI.rl("root"));
    AdvancementHolder one_barrel = Advancement.Builder.advancement().parent(lootrRoot)
        .display(LootrRegistry.getBarrelBlock(), Component.translatable("lootr.advancements.1barrel.title"), Component.translatable("lootr.advancements.1barrel.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("opened_barrel", ContainerTrigger.looted(LootrRegistry.getBarrelTrigger()))
        .save(consumer, LootrAPI.rl("1barrel"));
    // 1cart
    AdvancementHolder one_cart = Advancement.Builder.advancement().parent(lootrRoot)
        .display(Items.CHEST_MINECART, Component.translatable("lootr.advancements.1cart.title"), Component.translatable("lootr.advancements.1cart.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("opened_cart", ContainerTrigger.looted(LootrRegistry.getCartTrigger()))
        .save(consumer, LootrAPI.rl("1cart"));
    // 1chest
    AdvancementHolder one_chest = Advancement.Builder.advancement().parent(lootrRoot)
        .display(LootrRegistry.getChestBlock(), Component.translatable("lootr.advancements.1chest.title"), Component.translatable("lootr.advancements.1chest.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("opened_chest", ContainerTrigger.looted(LootrRegistry.getChestTrigger()))
        .save(consumer, LootrAPI.rl("1chest"));
    // 1shulker
    AdvancementHolder one_shulker = Advancement.Builder.advancement().parent(lootrRoot)
        .display(LootrRegistry.getShulkerBlock(), Component.translatable("lootr.advancements.1shulker.title"), Component.translatable("lootr.advancements.1shulker.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("opened_shulker", ContainerTrigger.looted(LootrRegistry.getShulkerTrigger()))
        .save(consumer, LootrAPI.rl("1shulker"));
    // all gravels
    AdvancementHolder brush = Advancement.Builder.advancement().parent(lootrRoot)
        .display(LootrRegistry.getSuspiciousGravelBlock(), Component.translatable("lootr.advancements.all_gravel.title"), Component.translatable("lootr.advancements.all_gravel.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("brushed_gravel", ContainerTrigger.looted(LootrRegistry.getGravelTrigger()))
        .addCriterion("brushed_sand", ContainerTrigger.looted(LootrRegistry.getSandTrigger()))
        .save(consumer, LootrAPI.rl("all_gravel"));
    // a pot
    AdvancementHolder pot = Advancement.Builder.advancement().parent(lootrRoot)
        .display(LootrRegistry.getDecoratedPotBlock(), Component.translatable("lootr.advancements.a_pot.title"), Component.translatable("lootr.advancements.a_pot.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("pot_opened", ContainerTrigger.looted(LootrRegistry.getPotTrigger()))
        .save(consumer, LootrAPI.rl("pot_opened"));
    AdvancementHolder archaeologist = Advancement.Builder.advancement().parent(pot)
        .display(Items.BRUSH, Component.translatable("lootr.advancements.archaeologist.title"), Component.translatable("lootr.advancements.archaeologist.description"), null, AdvancementType.CHALLENGE, true, true, false)
        .addCriterion("got_brush", AdvancementTrigger.completed(brush.id()))
        .addCriterion("got_pot", AdvancementTrigger.completed(pot.id())).save(consumer, LootrAPI.rl("archaeologist"));
    // item frame
    AdvancementHolder item_frame = Advancement.Builder.advancement().parent(lootrRoot)
        .display(Items.ITEM_FRAME, Component.translatable("lootr.advancements.1frame.title"), Component.translatable("lootr.advancements.1frame.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("looted_frame", ContainerTrigger.looted(LootrRegistry.getItemFrameTrigger()))
        .save(consumer, LootrAPI.rl("1frame"));
    // 10loot
    AdvancementHolder ten_loot = Advancement.Builder.advancement().parent(one_chest)
        .display(Blocks.GOLD_BLOCK, Component.translatable("lootr.advancements.10loot.title"), Component.translatable("lootr.advancements.10loot.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("opened_10", LootedStatTrigger.looted(10)).save(consumer, LootrAPI.rl("10loot"));
    // 25loot
    AdvancementHolder twentyfive_loot = Advancement.Builder.advancement().parent(ten_loot)
        .display(Blocks.EMERALD_BLOCK, Component.translatable("lootr.advancements.25loot.title"), Component.translatable("lootr.advancements.25loot.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("opened_25", LootedStatTrigger.looted(25)).save(consumer, LootrAPI.rl("25loot"));
    // 50loot
    AdvancementHolder fifty_loot = Advancement.Builder.advancement().parent(twentyfive_loot)
        .display(Blocks.DIAMOND_BLOCK, Component.translatable("lootr.advancements.50loot.title"), Component.translatable("lootr.advancements.50loot.description"), null, AdvancementType.TASK, true, true, false)
        .addCriterion("opened_50", LootedStatTrigger.looted(50)).save(consumer, LootrAPI.rl("50loot"));
    // 100loot
    Advancement.Builder.advancement().parent(fifty_loot)
        .display(Blocks.NETHERITE_BLOCK, Component.translatable("lootr.advancements.100loot.title"), Component.translatable("lootr.advancements.100loot.description"), null, AdvancementType.CHALLENGE, true, true, false)
        .addCriterion("opened_100", LootedStatTrigger.looted(100))
        .rewards(AdvancementRewards.Builder.loot(LootrAPI.TROPHY_REWARD)).save(consumer, LootrAPI.rl("100loot"));
    Advancement.Builder.advancement().parent(one_chest)
        .display(Items.ENCHANTED_GOLDEN_APPLE, Component.translatable("lootr.advancements.social.title"), Component.translatable("lootr.advancements.social.description"), null, AdvancementType.CHALLENGE, true, true, true)
        .addCriterion("opened_chest", AdvancementTrigger.completed(one_chest.id()))
        .addCriterion("opened_barrel", AdvancementTrigger.completed(one_barrel.id()))
        .addCriterion("opened_cart", AdvancementTrigger.completed(one_cart.id()))./*addCriterion("opened_shulker", AdvancementTrigger.completed(one_shulker.id())).*/save(consumer, LootrAPI.rl("social"));
  }
}
