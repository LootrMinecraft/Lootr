package noobanidus.mods.lootr.common.api.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.resources.Identifier;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Config(value = "lootr-common",
    categories = {
        LootrCommonConfig.Conversion.class,
        LootrCommonConfig.Restrictions.class,
        LootrCommonConfig.Breaking.class,
        LootrCommonConfig.Redstone.class,
        LootrCommonConfig.Notifications.class,
        LootrCommonConfig.Interaction.class,
        LootrCommonConfig.Decay.class,
        LootrCommonConfig.Refresh.class
    })
@ConfigInfo(title = LootrConfigStrings.COMMON_TITLE, titleTranslation = "lootr.configuration.title", description = LootrConfigStrings.COMMON_DESC, descriptionTranslation = "lootr.configuration.desc")
public class LootrCommonConfig {
  @ConfigOption.Hidden
  private static final String IDENTIFIER_REGEX = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+:" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$";

  @Category("Conversion")
  @ConfigInfo(title = LootrConfigStrings.CONVERSION_TITLE, titleTranslation = "lootr.configuration.conversion", description = LootrConfigStrings.CONVERSION_DESC, descriptionTranslation = "lootr.configuration.conversion.desc")
  public static class Conversion {
    @ConfigEntry(id = "disable", translation = "lootr.configuration.disable")
    @Comment(value = LootrConfigStrings.DISABLE_DESC, translation = "lootr.configuration.disable.desc")
    public static boolean disable = false;

    @ConfigEntry(id = "save_mode", translation = "lootr.configuration.save_mode")
    @Comment(value = LootrConfigStrings.SAVE_MODE_DESC, translation = "lootr.configuration.save_mode.desc")
    @ConfigOption.Select
    public static SaveMode saveMode = SaveMode.SMART;

    @ConfigEntry(id = "randomise_seed", translation = "lootr.configuration.randomise_seed")
    @Comment(value = LootrConfigStrings.RANDOMISE_SEED_DESC, translation = "lootr.configuration.randomise_seed.desc")
    public static boolean randomiseSeed = true;

    @ConfigEntry(id = "convert_elytras_to_chests", translation = "lootr.configuration.convert_elytras_to_chests")
    @Comment(value = LootrConfigStrings.CONVERT_ELYTRAS_TO_CHESTS_DESC, translation = "lootr.configuration.convert_elytras_to_chests.desc")
    public static boolean convertElytrasToChests = false;

    @ConfigEntry(id = "convert_elytras_to_item_frames", translation = "lootr.configuration.convert_elytras_to_item_frames")
    @Comment(value = LootrConfigStrings.CONVERT_ELYTRAS_TO_ITEM_FRAMES_DESC, translation = "lootr.configuration.convert_elytras_to_item_frames.desc")
    public static boolean convertElytrasToItemFrames = true;

    @ConfigEntry(id = "convert_structure_item_frames", translation = "lootr.configuration.convert_structure_item_frames")
    @Comment(value = LootrConfigStrings.CONVERT_STRUCTURE_ITEM_FRAMES_DESC, translation = "lootr.configuration.convert_structure_item_frames.desc")
    public static boolean convertStructureItemFrames = true;

    @ConfigEntry(id = "check_world_border", translation = "lootr.configuration.check_world_border")
    @Comment(value = LootrConfigStrings.CHECK_WORLD_BORDER_DESC, translation = "lootr.configuration.check_world_border.desc")
    public static boolean checkWorldBorder = true;

    @ConfigEntry(id = "perform_piecewise_check", translation = "lootr.configuration.perform_piecewise_check")
    @Comment(value = LootrConfigStrings.PERFORM_PIECEWISE_CHECK_DESC, translation = "lootr.configuration.perform_piecewise_check.desc")
    public static boolean performPiecewiseCheck = true;
  }

  @Category("Restrictions")
  @ConfigInfo(title = LootrConfigStrings.RESTRICTIONS_TITLE, titleTranslation = "lootr.configuration.restrictions", description = LootrConfigStrings.RESTRICTIONS_DESC, descriptionTranslation = "lootr.configuration.restrictions.desc")
  public static class Restrictions {
    @ConfigEntry(id = "dimension_whitelist", translation = "lootr.configuration.dimension_whitelist")
    @Comment(value = LootrConfigStrings.DIMENSION_WHITELIST_DESC, translation = "lootr.configuration.dimension_whitelist.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static final List<String> dimensionWhitelist = new ArrayList<>();

    @ConfigEntry(id = "dimension_blacklist", translation = "lootr.configuration.dimension_blacklist")
    @Comment(value = LootrConfigStrings.DIMENSION_BLACKLIST_DESC, translation = "lootr.configuration.dimension_blacklist.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static final List<String> dimensionBlacklist = new ArrayList<>();

    @ConfigEntry(id = "modid_dimension_whitelist", translation = "lootr.configuration.modid_dimension_whitelist")
    @Comment(value = LootrConfigStrings.MODID_DIMENSION_WHITELIST_DESC, translation = "lootr.configuration.modid_dimension_whitelist.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static final List<String> modidDimensionWhitelist = new ArrayList<>();

    @ConfigEntry(id = "modid_dimension_blacklist", translation = "lootr.configuration.modid_dimension_blacklist")
    @Comment(value = LootrConfigStrings.MODID_DIMENSION_BLACKLIST_DESC, translation = "lootr.configuration.modid_dimension_blacklist.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static final List<String> modidDimensionBlacklist = new ArrayList<>();

    @ConfigEntry(id = "loot_table_blacklist", translation = "lootr.configuration.loot_table_blacklist")
    @Comment(value = LootrConfigStrings.LOOT_TABLE_BLACKLIST_DESC, translation = "lootr.configuration.loot_table_blacklist.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static final List<String> lootTableBlacklist = new ArrayList<>();

    @ConfigEntry(id = "loot_table_modid_blacklist", translation = "lootr.configuration.loot_table_modid_blacklist")
    @Comment(value = LootrConfigStrings.LOOT_TABLE_MODID_BLACKLIST_DESC, translation = "lootr.configuration.loot_table_modid_blacklist.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static final List<String> lootTableModidBlacklist = new ArrayList<>();

    @ConfigEntry(id = "problematic_loot_tables", translation = "lootr.configuration.problematic_loot_tables")
    @Comment(value = LootrConfigStrings.PROBLEMATIC_LOOT_TABLES_DESC, translation = "lootr.configuration.problematic_loot_tables.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static final List<String> problematicLootTables = LootrAPI.PROBLEMATIC_CHESTS.stream()
        .map(Identifier::toString).collect(Collectors.toCollection(ArrayList::new));
  }

  @Category("Breaking")
  @ConfigInfo(title = LootrConfigStrings.BREAKING_TITLE, titleTranslation = "lootr.configuration.breaking", description = LootrConfigStrings.BREAKING_DESC, descriptionTranslation = "lootr.configuration.breaking.desc")
  public static class Breaking {
    @ConfigEntry(id = "break_mode", translation = "lootr.configuration.break_mode")
    @Comment(value = LootrConfigStrings.BREAK_MODE_DESC, translation = "lootr.configuration.break_mode.desc")
    @ConfigOption.Select
    public static BreakMode breakMode = BreakMode.DEFAULT;

    @ConfigEntry(id = "enable_fake_player_break", translation = "lootr.configuration.enable_fake_player_break")
    @Comment(value = LootrConfigStrings.ENABLE_FAKE_PLAYER_BREAK_DESC, translation = "lootr.configuration.enable_fake_player_break.desc")
    public static boolean enableFakePlayerBreak = false;

    @ConfigEntry(id = "blast_resistance", translation = "lootr.configuration.blast_resistance")
    @Comment(value = LootrConfigStrings.BLAST_RESISTANCE_DESC, translation = "lootr.configuration.blast_resistance.desc")
    @ConfigOption.Select
    public static ResistanceMode blastResistance = ResistanceMode.NONE;

    @ConfigEntry(id = "brushables_self_support", translation = "lootr.configuration.brushables_self_support")
    @Comment(value = LootrConfigStrings.BRUSHABLES_SELF_SUPPORT_DESC, translation = "lootr.configuration.brushables_self_support.desc")
    public static boolean brushablesSelfSupport = false;

    @ConfigEntry(id = "item_frames_self_support", translation = "lootr.configuration.item_frames_self_support")
    @Comment(value = LootrConfigStrings.ITEM_FRAMES_SELF_SUPPORT_DESC, translation = "lootr.configuration.item_frames_self_support.desc")
    public static boolean itemFramesSelfSupport = false;

    @ConfigEntry(id = "should_drop_player_loot", translation = "lootr.configuration.should_drop_player_loot")
    @Comment(value = LootrConfigStrings.SHOULD_DROP_PLAYER_LOOT_DESC, translation = "lootr.configuration.should_drop_player_loot.desc")
    public static boolean shouldDropPlayerLoot = false;
  }

  @Category("Redstone")
  @ConfigInfo(title = LootrConfigStrings.REDSTONE_TITLE, titleTranslation = "lootr.configuration.redstone", description = LootrConfigStrings.REDSTONE_DESC, descriptionTranslation = "lootr.configuration.redstone.desc")
  public static class Redstone {
    @ConfigEntry(id = "power_comparators", translation = "lootr.configuration.power_comparators")
    @Comment(value = LootrConfigStrings.POWER_COMPARATORS_DESC, translation = "lootr.configuration.power_comparators.desc")
    public static boolean powerComparators = true;

    @ConfigEntry(id = "custom_trapped", translation = "lootr.configuration.custom_trapped")
    @Comment(value = LootrConfigStrings.CUSTOM_TRAPPED_DESC, translation = "lootr.configuration.custom_trapped.desc")
    public static boolean customTrapped = false;
  }

  @Category("Notifications")
  @ConfigInfo(title = LootrConfigStrings.NOTIFICATIONS_TITLE, titleTranslation = "lootr.configuration.notifications", description = LootrConfigStrings.NOTIFICATIONS_DESC, descriptionTranslation = "lootr.configuration.notifications.desc")
  public static class Notifications {
    @ConfigEntry(id = "report_unresolved_tables", translation = "lootr.configuration.report_unresolved_tables")
    @Comment(value = LootrConfigStrings.REPORT_UNRESOLVED_TABLES_DESC, translation = "lootr.configuration.report_unresolved_tables.desc")
    public static boolean reportUnresolvedTables = true;

    @ConfigEntry(id = "disable_notifications", translation = "lootr.configuration.disable_notifications")
    @Comment(value = LootrConfigStrings.DISABLE_NOTIFICATIONS_DESC, translation = "lootr.configuration.disable_notifications.desc")
    public static boolean disableNotifications = false;

    @ConfigEntry(id = "maximum_notification_delay", translation = "lootr.configuration.maximum_notification_delay")
    @Comment(value = LootrConfigStrings.MAXIMUM_NOTIFICATION_DELAY_DESC, translation = "lootr.configuration.maximum_notification_delay.desc")
    @ConfigOption.Range(min = -1, max = Integer.MAX_VALUE)
    public static int maximumNotificationDelay = 30 * 20;

    @ConfigEntry(id = "disable_message_styles", translation = "lootr.configuration.disable_message_styles")
    @Comment(value = LootrConfigStrings.DISABLE_MESSAGE_STYLES_DESC, translation = "lootr.configuration.disable_message_styles.desc")
    public static boolean disableMessageStyles = false;
  }

  @Category("Interaction")
  @ConfigInfo(title = LootrConfigStrings.INTERACTION_TITLE, titleTranslation = "lootr.configuration.interaction", description = LootrConfigStrings.INTERACTION_DESC, descriptionTranslation = "lootr.configuration.interaction.desc")
  public static class Interaction {
    @ConfigEntry(id = "bypass_spawn_protection", translation = "lootr.configuration.bypass_spawn_protection")
    @Comment(value = LootrConfigStrings.BYPASS_SPAWN_PROTECTION_DESC, translation = "lootr.configuration.bypass_spawn_protection.desc")
    public static boolean bypassSpawnProtection = true;
  }

  @Category("Decay")
  @ConfigInfo(title = LootrConfigStrings.DECAY_TITLE, titleTranslation = "lootr.configuration.decay", description = LootrConfigStrings.DECAY_DESC, descriptionTranslation = "lootr.configuration.decay.desc")
  public static class Decay {
    @ConfigEntry(id = "decay_value", translation = "lootr.configuration.decay_value")
    @Comment(value = LootrConfigStrings.DECAY_VALUE_DESC, translation = "lootr.configuration.decay_value.desc")
    @ConfigOption.Range(min = 0, max = Integer.MAX_VALUE)
    public static int decayValue = 5 * 60 * 20;

    @ConfigEntry(id = "decay_loot_tables", translation = "lootr.configuration.decay_loot_tables")
    @Comment(value = LootrConfigStrings.DECAY_LOOT_TABLES_DESC, translation = "lootr.configuration.decay_loot_tables.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static final List<String> decayLootTables = new ArrayList<>();

    @ConfigEntry(id = "decay_loot_table_modids", translation = "lootr.configuration.decay_loot_table_modids")
    @Comment(value = LootrConfigStrings.DECAY_LOOT_TABLE_MODIDS_DESC, translation = "lootr.configuration.decay_loot_table_modids.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static final List<String> decayLootTableModids = new ArrayList<>();

    @ConfigEntry(id = "decay_dimensions", translation = "lootr.configuration.decay_dimensions")
    @Comment(value = LootrConfigStrings.DECAY_DIMENSIONS_DESC, translation = "lootr.configuration.decay_dimensions.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static final List<String> decayDimensions = new ArrayList<>();

    @ConfigEntry(id = "replace_when_decayed", translation = "lootr.configuration.replace_when_decayed")
    @Comment(value = LootrConfigStrings.REPLACE_WHEN_DECAYED_DESC, translation = "lootr.configuration.replace_when_decayed.desc")
    public static boolean replaceWhenDecayed = false;

    @ConfigEntry(id = "perform_decay_while_ticking", translation = "lootr.configuration.perform_decay_while_ticking")
    @Comment(value = LootrConfigStrings.PERFORM_DECAY_WHILE_TICKING_DESC, translation = "lootr.configuration.perform_decay_while_ticking.desc")
    public static boolean performDecayWhileTicking = true;

    @ConfigEntry(id = "start_decay_while_ticking", translation = "lootr.configuration.start_decay_while_ticking")
    @Comment(value = LootrConfigStrings.START_DECAY_WHILE_TICKING_DESC, translation = "lootr.configuration.start_decay_while_ticking.desc")
    public static boolean startDecayWhileTicking = false;

    @ConfigEntry(id = "decay_all", translation = "lootr.configuration.decay_all")
    @Comment(value = LootrConfigStrings.DECAY_ALL_DESC, translation = "lootr.configuration.decay_all.desc")
    public static boolean decayAll = false;
  }

  @Category("Refresh")
  @ConfigInfo(title = LootrConfigStrings.REFRESH_TITLE, titleTranslation = "lootr.configuration.refresh", description = LootrConfigStrings.REFRESH_DESC, descriptionTranslation = "lootr.configuration.refresh.desc")
  public static class Refresh {
    @ConfigEntry(id = "refresh_value", translation = "lootr.configuration.refresh_value")
    @Comment(value = LootrConfigStrings.REFRESH_VALUE_DESC, translation = "lootr.configuration.refresh_value.desc")
    @ConfigOption.Range(min = 0, max = Integer.MAX_VALUE)
    public static int refreshValue = 5 * 60 * 20;

    @ConfigEntry(id = "refresh_loot_tables", translation = "lootr.configuration.refresh_loot_tables")
    @Comment(value = LootrConfigStrings.REFRESH_LOOT_TABLES_DESC, translation = "lootr.configuration.refresh_loot_tables.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static final List<String> refreshLootTables = new ArrayList<>();

    @ConfigEntry(id = "refresh_loot_table_modids", translation = "lootr.configuration.refresh_loot_table_modids")
    @Comment(value = LootrConfigStrings.REFRESH_LOOT_TABLE_MODIDS_DESC, translation = "lootr.configuration.refresh_loot_table_modids.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static final List<String> refreshLootTableModids = new ArrayList<>();

    @ConfigEntry(id = "refresh_dimensions", translation = "lootr.configuration.refresh_dimensions")
    @Comment(value = LootrConfigStrings.REFRESH_DIMENSIONS_DESC, translation = "lootr.configuration.refresh_dimensions.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static final List<String> refreshDimensions = new ArrayList<>();

    @ConfigEntry(id = "perform_refresh_while_ticking", translation = "lootr.configuration.perform_refresh_while_ticking")
    @Comment(value = LootrConfigStrings.PERFORM_REFRESH_WHILE_TICKING_DESC, translation = "lootr.configuration.perform_refresh_while_ticking.desc")
    public static boolean performRefreshWhileTicking = true;

    @ConfigEntry(id = "start_refresh_while_ticking", translation = "lootr.configuration.start_refresh_while_ticking")
    @Comment(value = LootrConfigStrings.START_REFRESH_WHILE_TICKING_DESC, translation = "lootr.configuration.start_refresh_while_ticking.desc")
    public static boolean startRefreshWhileTicking = false;

    @ConfigEntry(id = "refresh_all", translation = "lootr.configuration.refresh_all")
    @Comment(value = LootrConfigStrings.REFRESH_ALL_DESC, translation = "lootr.configuration.refresh_all.desc")
    public static boolean refreshAll = false;
  }
}
