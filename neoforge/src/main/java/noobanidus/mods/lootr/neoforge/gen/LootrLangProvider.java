package noobanidus.mods.lootr.neoforge.gen;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;
import noobanidus.mods.lootr.common.api.config.LootrConfigStrings;
import noobanidus.mods.lootr.common.api.LootrRegistry;

public class LootrLangProvider extends LanguageProvider {
  public LootrLangProvider(PackOutput output) {
    super(output, LootrAPI.MODID, "en_us");
  }

  @Override
  protected void addTranslations() {
    add("lootr.commands.usage", "/lootr cart | cart <loot-table> | %s | custom-chest | custom-area <x> <y> <z> <x> <y> <z> | refresh | decay | open_as <player> | open_as_uuid <uuid> | id | openers | clear <player> | cclear <entity matcher>");

    add("lootr.message.should_sneak", "Breaking this block will prevent others from obtaining loot! Only break if really needed.");
    add("lootr.message.cart_should_sneak", "Destroying this entity will prevent others from obtaining loot! Only destroy if really needed.");
    add("lootr.message.should_sneak2", "To break the block anyway, sneak while breaking.");
    add("lootr.message.cart_should_sneak2", "To destroy this entity anyway, sneak while attacking.");
    add("lootr.message.cannot_break", "Lootr blocks and entities cannot be broken or destroyed.");
    add("lootr.message.cannot_break_sneak", "While in creative mode, you must sneak to break or destroy Lootr blocks and entities.");
    add("lootr.message.decayed", "The decaying container crumbles at your touch!");
    add("lootr.message.decay_in", "This container will decay completely in %s seconds.");
    add("lootr.message.decay_start", "The container begins to crumble at your touch! It will decay completely in %s seconds.");
    add("lootr.message.refreshed", "The contents of the container are refreshed at your touch!");
    add("lootr.message.refresh_in", "This container will refresh its contents in %s seconds.");
    add("lootr.message.refresh_start", "The container will refresh with new contents in %s seconds!");
    add("lootr.message.invalid_table", "Error with mod [%s], not Lootr! The loot table for this container [%s] does not exist or could not be loaded. Please report this to the mod author, not to Lootr.");

    add(LootrRegistry.getChestBlock(), "Loot Chest");
    add(LootrRegistry.getTrappedChestBlock(), "Loot Chest");
    add(LootrRegistry.getBarrelBlock(), "Loot Barrel");
    add(LootrRegistry.getShulkerBoxBlock(), "Loot Shulker");
    add(LootrRegistry.getInventoryBlock(), "Loot Chest");
    add(LootrRegistry.getMinecart(), "Minecart with Loot Chest");
    add(LootrRegistry.getTrophyBlock(), "Centennial Trophy");
    add(LootrRegistry.getSuspiciousGravelBlock(), "Gravel");
    add(LootrRegistry.getSuspiciousSandBlock(), "Sand");
    add(LootrRegistry.getDecoratedPotBlock(), "Decorated Loot Pot");
    add(LootrRegistry.getItemFrame(), "Item Frame with Loot");

    add("stat.lootr.looted_stat", "Containers and entities looted");

    add("lootr.commands.create", "Created a Lootr %s at %s using the loot table %s.");
    add("lootr.commands.summon", "Summoned a Lootr Cart at %s using the loot table %s.");
    add("lootr.commands.blockpos", "%s,%s,%s");

    add("lootr.advancements.100loot.title", "Centennial");
    add("lootr.advancements.100loot.description", "Open 100 Lootr containers!");
    add("lootr.advancements.50loot.title", "Half-Century");
    add("lootr.advancements.50loot.description", "Open 50 Lootr containers!");
    add("lootr.advancements.25loot.title", "Close Quarters");
    add("lootr.advancements.25loot.description", "Open 25 Lootr containers!");
    add("lootr.advancements.10loot.title", "Delightful Decade");
    add("lootr.advancements.10loot.description", "Open 10 Lootr containers!");
    add("lootr.advancements.1chest.title", "'X' Marks the Spot");
    add("lootr.advancements.1chest.description", "Open your first Lootr chest!");
    add("lootr.advancements.1barrel.title", "Barrel Booty");
    add("lootr.advancements.1barrel.description", "Open your first Lootr barrel!");
    add("lootr.advancements.1cart.title", "Tunnel Treasures");
    add("lootr.advancements.1cart.description", "Open your first Lootr minecart!");
    add("lootr.advancements.1shulker.title", "Bounty Box");
    add("lootr.advancements.1shulker.description", "Open your first Lootr shulker box!");
    add("lootr.advancements.1frame.title", "I was framed!");
    add("lootr.advancements.1frame.description", "Loot an item from a Lootr item frame!");
    add("lootr.advancements.social.title", "Socialised Loot");
    add("lootr.advancements.social.description", "Open one of every Lootr container type!");
    add("lootr.advancements.root.title", "Lootr");
    add("lootr.advancements.root.description", "It belongs in a museum! Bring instanced loot to your world.");
    add("lootr.advancements.all_gravel.title", "Brush It Off");
    add("lootr.advancements.all_gravel.description", "Brush all types of suspicious Lootr blocks!");
    add("lootr.advancements.a_pot.title", "Woe to the Pot");
    add("lootr.advancements.a_pot.description", "Loot your first Lootr decorated pot!");
    add("lootr.advancements.archaeologist.title", "That Was 90% Gravity");
    add("lootr.advancements.archaeologist.description", "Loot all of Lootr's archaeological blocks!");

    add("itemGroup.lootr", "Lootr");
    add("itemGroup.lootr.lootr", "Lootr");
    add("text.autoconfig.lootr.title", "Lootr");


    add("lootr.config.client.title", LootrConfigStrings.CLIENT_TITLE);
    add("lootr.config.client.desc", LootrConfigStrings.CLIENT_DESC);

    add("lootr.config.particles.title", LootrConfigStrings.PARTICLES_TITLE);
    add("lootr.config.particles.description", LootrConfigStrings.PARTICLES_DESC);
    add("lootr.config.particles.unopened_particles", LootrConfigStrings.UNOPENED_PARTICLES);
    add("lootr.config.particles.unopened_particles.desc", LootrConfigStrings.UNOPENED_PARTICLES_DESC);

    add("lootr.config.textures.title", LootrConfigStrings.TEXTURES_TITLE);
    add("lootr.config.textures.description", LootrConfigStrings.TEXTURES_DESC);
    add("lootr.config.textures.vanilla_textures", LootrConfigStrings.VANILLA_TEXTURES);
    add("lootr.config.textures.vanilla_textures.desc", LootrConfigStrings.VANILLA_TEXTURES_DESC);

    add("lootr.configuration.title", LootrConfigStrings.COMMON_TITLE);
    add("lootr.configuration.desc", LootrConfigStrings.COMMON_DESC);

    add("lootr.configuration.conversion", LootrConfigStrings.CONVERSION_TITLE);
    add("lootr.configuration.conversion.desc", LootrConfigStrings.CONVERSION_DESC);
    add("lootr.configuration.disable", LootrConfigStrings.DISABLE);
    add("lootr.configuration.disable.desc", LootrConfigStrings.DISABLE_DESC);
    add("lootr.configuration.save_mode", LootrConfigStrings.SAVE_MODE);
    add("lootr.configuration.save_mode.desc", LootrConfigStrings.SAVE_MODE_DESC);
    add("lootr.configuration.randomise_seed", LootrConfigStrings.RANDOMISE_SEED);
    add("lootr.configuration.randomise_seed.desc", LootrConfigStrings.RANDOMISE_SEED_DESC);
    add("lootr.configuration.convert_elytras_to_chests", LootrConfigStrings.CONVERT_ELYTRAS_TO_CHESTS);
    add("lootr.configuration.convert_elytras_to_chests.desc", LootrConfigStrings.CONVERT_ELYTRAS_TO_CHESTS_DESC);
    add("lootr.configuration.convert_elytras_to_item_frames", LootrConfigStrings.CONVERT_ELYTRAS_TO_ITEM_FRAMES);
    add("lootr.configuration.convert_elytras_to_item_frames.desc", LootrConfigStrings.CONVERT_ELYTRAS_TO_ITEM_FRAMES_DESC);
    add("lootr.configuration.convert_structure_item_frames", LootrConfigStrings.CONVERT_STRUCTURE_ITEM_FRAMES);
    add("lootr.configuration.convert_structure_item_frames.desc", LootrConfigStrings.CONVERT_STRUCTURE_ITEM_FRAMES_DESC);
    add("lootr.configuration.check_world_border", LootrConfigStrings.CHECK_WORLD_BORDER);
    add("lootr.configuration.check_world_border.desc", LootrConfigStrings.CHECK_WORLD_BORDER_DESC);
    add("lootr.configuration.perform_piecewise_check", LootrConfigStrings.PERFORM_PIECEWISE_CHECK);
    add("lootr.configuration.perform_piecewise_check.desc", LootrConfigStrings.PERFORM_PIECEWISE_CHECK_DESC);

    add("lootr.configuration.restrictions", LootrConfigStrings.RESTRICTIONS_TITLE);
    add("lootr.configuration.restrictions.desc", LootrConfigStrings.RESTRICTIONS_DESC);
    add("lootr.configuration.dimension_whitelist", LootrConfigStrings.DIMENSION_WHITELIST);
    add("lootr.configuration.dimension_whitelist.desc", LootrConfigStrings.DIMENSION_WHITELIST_DESC);
    add("lootr.configuration.dimension_blacklist", LootrConfigStrings.DIMENSION_BLACKLIST);
    add("lootr.configuration.dimension_blacklist.desc", LootrConfigStrings.DIMENSION_BLACKLIST_DESC);
    add("lootr.configuration.modid_dimension_whitelist", LootrConfigStrings.MODID_DIMENSION_WHITELIST);
    add("lootr.configuration.modid_dimension_whitelist.desc", LootrConfigStrings.MODID_DIMENSION_WHITELIST_DESC);
    add("lootr.configuration.modid_dimension_blacklist", LootrConfigStrings.MODID_DIMENSION_BLACKLIST);
    add("lootr.configuration.modid_dimension_blacklist.desc", LootrConfigStrings.MODID_DIMENSION_BLACKLIST_DESC);
    add("lootr.configuration.loot_table_blacklist", LootrConfigStrings.LOOT_TABLE_BLACKLIST);
    add("lootr.configuration.loot_table_blacklist.desc", LootrConfigStrings.LOOT_TABLE_BLACKLIST_DESC);
    add("lootr.configuration.loot_table_modid_blacklist", LootrConfigStrings.LOOT_TABLE_MODID_BLACKLIST);
    add("lootr.configuration.loot_table_modid_blacklist.desc", LootrConfigStrings.LOOT_TABLE_MODID_BLACKLIST_DESC);
    add("lootr.configuration.problematic_loot_tables", LootrConfigStrings.PROBLEMATIC_LOOT_TABLES);
    add("lootr.configuration.problematic_loot_tables.desc", LootrConfigStrings.PROBLEMATIC_LOOT_TABLES_DESC);

    add("lootr.configuration.breaking", LootrConfigStrings.BREAKING_TITLE);
    add("lootr.configuration.breaking.desc", LootrConfigStrings.BREAKING_DESC);
    add("lootr.configuration.break_mode", LootrConfigStrings.BREAK_MODE);
    add("lootr.configuration.break_mode.desc", LootrConfigStrings.BREAK_MODE_DESC);
    add("lootr.configuration.enable_fake_player_break", LootrConfigStrings.ENABLE_FAKE_PLAYER_BREAK);
    add("lootr.configuration.enable_fake_player_break.desc", LootrConfigStrings.ENABLE_FAKE_PLAYER_BREAK_DESC);
    add("lootr.configuration.blast_resistance", LootrConfigStrings.BLAST_RESISTANCE);
    add("lootr.configuration.blast_resistance.desc", LootrConfigStrings.BLAST_RESISTANCE_DESC);
    add("lootr.configuration.brushables_self_support", LootrConfigStrings.BRUSHABLES_SELF_SUPPORT);
    add("lootr.configuration.brushables_self_support.desc", LootrConfigStrings.BRUSHABLES_SELF_SUPPORT_DESC);
    add("lootr.configuration.item_frames_self_support", LootrConfigStrings.ITEM_FRAMES_SELF_SUPPORT);
    add("lootr.configuration.item_frames_self_support.desc", LootrConfigStrings.ITEM_FRAMES_SELF_SUPPORT_DESC);
    add("lootr.configuration.should_drop_player_loot", LootrConfigStrings.SHOULD_DROP_PLAYER_LOOT);
    add("lootr.configuration.should_drop_player_loot.desc", LootrConfigStrings.SHOULD_DROP_PLAYER_LOOT_DESC);

    add("lootr.configuration.redstone", LootrConfigStrings.REDSTONE_TITLE);
    add("lootr.configuration.redstone.desc", LootrConfigStrings.REDSTONE_DESC);
    add("lootr.configuration.power_comparators", LootrConfigStrings.POWER_COMPARATORS);
    add("lootr.configuration.power_comparators.desc", LootrConfigStrings.POWER_COMPARATORS_DESC);
    add("lootr.configuration.custom_trapped", LootrConfigStrings.CUSTOM_TRAPPED);
    add("lootr.configuration.custom_trapped.desc", LootrConfigStrings.CUSTOM_TRAPPED_DESC);

    add("lootr.configuration.notifications", LootrConfigStrings.NOTIFICATIONS_TITLE);
    add("lootr.configuration.notifications.desc", LootrConfigStrings.NOTIFICATIONS_DESC);
    add("lootr.configuration.report_unresolved_tables", LootrConfigStrings.REPORT_UNRESOLVED_TABLES);
    add("lootr.configuration.report_unresolved_tables.desc", LootrConfigStrings.REPORT_UNRESOLVED_TABLES_DESC);
    add("lootr.configuration.disable_notifications", LootrConfigStrings.DISABLE_NOTIFICATIONS);
    add("lootr.configuration.disable_notifications.desc", LootrConfigStrings.DISABLE_NOTIFICATIONS_DESC);
    add("lootr.configuration.maximum_notification_delay", LootrConfigStrings.MAXIMUM_NOTIFICATION_DELAY);
    add("lootr.configuration.maximum_notification_delay.desc", LootrConfigStrings.MAXIMUM_NOTIFICATION_DELAY_DESC);
    add("lootr.configuration.disable_message_styles", LootrConfigStrings.DISABLE_MESSAGE_STYLES);
    add("lootr.configuration.disable_message_styles.desc", LootrConfigStrings.DISABLE_MESSAGE_STYLES_DESC);

    add("lootr.configuration.interaction", LootrConfigStrings.INTERACTION_TITLE);
    add("lootr.configuration.interaction.desc", LootrConfigStrings.INTERACTION_DESC);
    add("lootr.configuration.bypass_spawn_protection", LootrConfigStrings.BYPASS_SPAWN_PROTECTION);
    add("lootr.configuration.bypass_spawn_protection.desc", LootrConfigStrings.BYPASS_SPAWN_PROTECTION_DESC);

    add("lootr.configuration.decay", LootrConfigStrings.DECAY_TITLE);
    add("lootr.configuration.decay.desc", LootrConfigStrings.DECAY_DESC);
    add("lootr.configuration.decay_value", LootrConfigStrings.DECAY_VALUE);
    add("lootr.configuration.decay_value.desc", LootrConfigStrings.DECAY_VALUE_DESC);
    add("lootr.configuration.decay_loot_tables", LootrConfigStrings.DECAY_LOOT_TABLES);
    add("lootr.configuration.decay_loot_tables.desc", LootrConfigStrings.DECAY_LOOT_TABLES_DESC);
    add("lootr.configuration.decay_loot_table_modids", LootrConfigStrings.DECAY_LOOT_TABLE_MODIDS);
    add("lootr.configuration.decay_loot_table_modids.desc", LootrConfigStrings.DECAY_LOOT_TABLE_MODIDS_DESC);
    add("lootr.configuration.decay_dimensions", LootrConfigStrings.DECAY_DIMENSIONS);
    add("lootr.configuration.decay_dimensions.desc", LootrConfigStrings.DECAY_DIMENSIONS_DESC);
    add("lootr.configuration.replace_when_decayed", LootrConfigStrings.REPLACE_WHEN_DECAYED);
    add("lootr.configuration.replace_when_decayed.desc", LootrConfigStrings.REPLACE_WHEN_DECAYED_DESC);
    add("lootr.configuration.perform_decay_while_ticking", LootrConfigStrings.PERFORM_DECAY_WHILE_TICKING);
    add("lootr.configuration.perform_decay_while_ticking.desc", LootrConfigStrings.PERFORM_DECAY_WHILE_TICKING_DESC);
    add("lootr.configuration.start_decay_while_ticking", LootrConfigStrings.START_DECAY_WHILE_TICKING);
    add("lootr.configuration.start_decay_while_ticking.desc", LootrConfigStrings.START_DECAY_WHILE_TICKING_DESC);
    add("lootr.configuration.decay_all", LootrConfigStrings.DECAY_ALL);
    add("lootr.configuration.decay_all.desc", LootrConfigStrings.DECAY_ALL_DESC);

    add("lootr.configuration.refresh", LootrConfigStrings.REFRESH_TITLE);
    add("lootr.configuration.refresh.desc", LootrConfigStrings.REFRESH_DESC);
    add("lootr.configuration.refresh_value", LootrConfigStrings.REFRESH_VALUE);
    add("lootr.configuration.refresh_value.desc", LootrConfigStrings.REFRESH_VALUE_DESC);
    add("lootr.configuration.refresh_loot_tables", LootrConfigStrings.REFRESH_LOOT_TABLES);
    add("lootr.configuration.refresh_loot_tables.desc", LootrConfigStrings.REFRESH_LOOT_TABLES_DESC);
    add("lootr.configuration.refresh_loot_table_modids", LootrConfigStrings.REFRESH_LOOT_TABLE_MODIDS);
    add("lootr.configuration.refresh_loot_table_modids.desc", LootrConfigStrings.REFRESH_LOOT_TABLE_MODIDS_DESC);
    add("lootr.configuration.refresh_dimensions", LootrConfigStrings.REFRESH_DIMENSIONS);
    add("lootr.configuration.refresh_dimensions.desc", LootrConfigStrings.REFRESH_DIMENSIONS_DESC);
    add("lootr.configuration.perform_refresh_while_ticking", LootrConfigStrings.PERFORM_REFRESH_WHILE_TICKING);
    add("lootr.configuration.perform_refresh_while_ticking.desc", LootrConfigStrings.PERFORM_REFRESH_WHILE_TICKING_DESC);
    add("lootr.configuration.start_refresh_while_ticking", LootrConfigStrings.START_REFRESH_WHILE_TICKING);
    add("lootr.configuration.start_refresh_while_ticking.desc", LootrConfigStrings.START_REFRESH_WHILE_TICKING_DESC);
    add("lootr.configuration.refresh_all", LootrConfigStrings.REFRESH_ALL);
    add("lootr.configuration.refresh_all.desc", LootrConfigStrings.REFRESH_ALL_DESC);

    // Tags
    add(LootrTags.Blocks.CONVERT_BARRELS, "Blocks that Convert to Lootr Barrels");
    add(LootrTags.Blocks.CONVERT_CHESTS, "Blocks that Convert to Lootr Chests");
    add(LootrTags.Blocks.CONVERT_SHULKERS, "Blocks that Convert to Lootr Shulkers");
    add(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS, "Blocks that Convert to Trapped Lootr Chests");
    add(LootrTags.Blocks.CONVERT_POTS, "Blocks that Convert to Lootr Pots");
    add(LootrTags.Blocks.CONVERT_GRAVELS, "Blocks that Convert to Suspicious Lootr Gravels");
    add(LootrTags.Blocks.CONVERT_SANDS, "Blocks that Convert to Suspicious Lootr Sands");
    add(LootrTags.Blocks.CONVERT_BLOCK, "All Blocks that Convert to Lootr Containers");
    add(LootrTags.Blocks.CONVERT_BLACKLIST, "Blocks that are blocked from being converted to Lootr Containers");
    add(LootrTags.Blocks.CONTAINERS, "Lootr Containers");
    add(LootrTags.Blocks.CHESTS, "Lootr Chests");
    add(LootrTags.Blocks.BARRELS, "Lootr Barrels");
    add(LootrTags.Blocks.SHULKERS, "Lootr Shulkers");
    add(LootrTags.Blocks.TRAPPED_CHESTS, "Trapped Lootr Chests");
    add(LootrTags.Blocks.SANDS, "Suspicious Lootr Sands");
    add(LootrTags.Blocks.GRAVELS, "Suspicious Lootr Gravels");
    add(LootrTags.Blocks.POTS, "Lootr Pots");
    add(LootrTags.Blocks.CUSTOM_ELIGIBLE, "Custom Inventory Eligible Target Blocks");
    add(LootrTags.Blocks.CATS_CAN_BLOCK, "Blocks Cats Can Sit On");
    add(LootrTags.Blocks.NON_BLOCKING, "Blocks That Do Not Prevent Opening Chests");
    add(LootrTags.Blocks.INTERACT_WHITELIST_BLOCKS, "Blocks That Bypass Spawn Protection Interaction Disabling");
    add(LootrTags.Items.CONTAINERS, "Lootr Containers");
    add(LootrTags.Items.CHESTS, "Lootr Chests");
    add(LootrTags.Items.BARRELS, "Lootr Barrels");
    add(LootrTags.Items.SHULKERS, "Lootr Shulkers");
    add(LootrTags.Items.TRAPPED_CHESTS, "Trapped Lootr Chests");
    add(LootrTags.Items.SANDS, "Suspicious Lootr Sands");
    add(LootrTags.Items.GRAVELS, "Suspicious Lootr Gravels");
    add(LootrTags.Items.POTS, "Lootr Pots");
    add(LootrTags.Items.ITEM_FRAME_CONVERT_BLACKLIST, "Item Frame Item Blacklist");
    add(LootrTags.Entity.CONTAINERS, "Lootr Containers");
    add(LootrTags.Entity.CONVERT_ENTITIES, "Entities that Convert to Lootr Containers");
    add(LootrTags.Entity.CONVERT_BLACKLIST, "Entities that Do Not Convert to Lootr Containers");
    add(LootrTags.Entity.CONVERT_CARTS, "Minecarts that can convert to Lootr carts");
    add(LootrTags.Entity.ITEM_FRAMES, "Lootr Item Frames");
    add(LootrTags.Entity.MINECARTS, "Lootr Minecarts");
  }
}
