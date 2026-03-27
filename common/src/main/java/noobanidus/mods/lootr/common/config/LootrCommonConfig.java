package noobanidus.mods.lootr.common.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;
import com.teamresourceful.resourcefulconfig.api.types.entries.Observable;
import net.minecraft.resources.Identifier;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.config.BreakMode;
import noobanidus.mods.lootr.common.api.config.ResistanceMode;
import noobanidus.mods.lootr.common.api.config.SaveMode;

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
@ConfigInfo(titleTranslation = "lootr.configuration.title", descriptionTranslation = "lootr.configuration.desc")
public class LootrCommonConfig {
  @ConfigOption.Hidden
  private static final String IDENTIFIER_REGEX = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+:" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$";

  @Category("Conversion")
  @ConfigInfo(titleTranslation = "lootr.configuration.conversion", descriptionTranslation = "lootr.configuration.conversion.desc")
  public static class Conversion {
    @ConfigEntry(id = "disable", translation = "lootr.configuration.disable")
    @Comment(value = "", translation = "lootr.configuration.disable.desc")
    public static boolean disable = false;

    @ConfigEntry(id = "save_mode", translation = "lootr.configuration.save_mode")
    @Comment(value = "", translation = "lootr.configuration.save_mode.desc")
    @ConfigOption.Select
    public static SaveMode saveMode = SaveMode.SMART;

    @ConfigEntry(id = "randomise_seed", translation = "lootr.configuration.randomise_seed")
    @Comment(value = "", translation = "lootr.configuration.randomise_seed.desc")
    public static boolean randomiseSeed = true;

    @ConfigEntry(id = "convert_elytras_to_chests", translation = "lootr.configuration.convert_elytras_to_chests")
    @Comment(value = "", translation = "lootr.configuration.convert_elytras_to_chests.desc")
    public static boolean convertElytrasToChests = false;

    @ConfigEntry(id = "convert_elytras_to_item_frames", translation = "lootr.configuration.convert_elytras_to_item_frames")
    @Comment(value = "", translation = "lootr.configuration.convert_elytras_to_item_frames.desc")
    public static boolean convertElytrasToItemFrames = true;

    @ConfigEntry(id = "convert_structure_item_frames", translation = "lootr.configuration.convert_structure_item_frames")
    @Comment(value = "", translation = "lootr.configuration.convert_structure_item_frames.desc")
    public static boolean convertStructureItemFrames = true;

    @ConfigEntry(id = "check_world_border", translation = "lootr.configuration.check_world_border")
    @Comment(value = "", translation = "lootr.configuration.check_world_border.desc")
    public static boolean checkWorldBorder = true;

    @ConfigEntry(id = "perform_piecewise_check", translation = "lootr.configuration.perform_piecewise_check")
    @Comment(value = "", translation = "lootr.configuration.perform_piecewise_check.desc")
    public static boolean performPiecewiseCheck = true;
  }

  @Category("Restrictions")
  @ConfigInfo(titleTranslation = "lootr.configuration.restrictions", descriptionTranslation = "lootr.configuration.restrictions.desc")
  public static class Restrictions {
    @ConfigEntry(id = "dimension_whitelist", translation = "lootr.configuration.dimension_whitelist")
    @Comment(value = "", translation = "lootr.configuration.dimension_whitelist.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static Observable<String[]> dimensionWhitelist = Observable.of(new String[]{});

    @ConfigEntry(id = "dimension_blacklist", translation = "lootr.configuration.dimension_blacklist")
    @Comment(value = "", translation = "lootr.configuration.dimension_blacklist.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static Observable<String[]> dimensionBlacklist = Observable.of(new String[]{});

    @ConfigEntry(id = "modid_dimension_whitelist", translation = "lootr.configuration.modid_dimension_whitelist")
    @Comment(value = "", translation = "lootr.configuration.modid_dimension_whitelist.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static Observable<String[]> modidDimensionWhitelist = Observable.of(new String[]{});

    @ConfigEntry(id = "modid_dimension_blacklist", translation = "lootr.configuration.modid_dimension_blacklist")
    @Comment(value = "", translation = "lootr.configuration.modid_dimension_blacklist.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static Observable<String[]> modidDimensionBlacklist = Observable.of(new String[]{});

    @ConfigEntry(id = "loot_table_blacklist", translation = "lootr.configuration.loot_table_blacklist")
    @Comment(value = "", translation = "lootr.configuration.loot_table_blacklist.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static Observable<String[]> lootTableBlacklist = Observable.of(new String[]{});

    @ConfigEntry(id = "loot_table_modid_blacklist", translation = "lootr.configuration.loot_table_modid_blacklist")
    @Comment(value = "", translation = "lootr.configuration.loot_table_modid_blacklist.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static Observable<String[]> lootTableModidBlacklist = Observable.of(new String[]{});

    @ConfigEntry(id = "problematic_loot_tables", translation = "lootr.configuration.problematic_loot_tables")
    @Comment(value = "", translation = "lootr.configuration.problematic_loot_tables.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static Observable<String[]> problematicLootTables = Observable.of(LootrAPI.PROBLEMATIC_CHESTS.stream()
        .map(Identifier::toString).toArray(String[]::new));
  }

  @Category("Breaking")
  @ConfigInfo(titleTranslation = "lootr.configuration.breaking", descriptionTranslation = "lootr.configuration.breaking.desc")
  public static class Breaking {
    @ConfigEntry(id = "break_mode", translation = "lootr.configuration.break_mode")
    @Comment(value = "", translation = "lootr.configuration.break_mode.desc")
    @ConfigOption.Select
    public static BreakMode breakMode = BreakMode.DEFAULT;

    @ConfigEntry(id = "enable_fake_player_break", translation = "lootr.configuration.enable_fake_player_break")
    @Comment(value = "", translation = "lootr.configuration.enable_fake_player_break.desc")
    public static boolean enableFakePlayerBreak = false;

    @ConfigEntry(id = "blast_resistance", translation = "lootr.configuration.blast_resistance")
    @Comment(value = "", translation = "lootr.configuration.blast_resistance.desc")
    @ConfigOption.Select
    public static ResistanceMode blastResistance = ResistanceMode.NONE;

    @ConfigEntry(id = "brushables_self_support", translation = "lootr.configuration.brushables_self_support")
    @Comment(value = "", translation = "lootr.configuration.brushables_self_support.desc")
    public static boolean brushablesSelfSupport = true;

    @ConfigEntry(id = "item_frames_self_support", translation = "lootr.configuration.item_frames_self_support")
    @Comment(value = "", translation = "lootr.configuration.item_frames_self_support.desc")
    public static boolean itemFramesSelfSupport = true;

    @ConfigEntry(id = "should_drop_player_loot", translation = "lootr.configuration.should_drop_player_loot")
    @Comment(value = "", translation = "lootr.configuration.should_drop_player_loot.desc")
    public static boolean shouldDropPlayerLoot = false;
  }

  @Category("Redstone")
  @ConfigInfo(titleTranslation = "lootr.configuration.redstone", descriptionTranslation = "lootr.configuration.redstone.desc")
  public static class Redstone {
    @ConfigEntry(id = "power_comparators", translation = "lootr.configuration.power_comparators")
    @Comment(value = "", translation = "lootr.configuration.power_comparators.desc")
    public static boolean powerComparators = true;

    @ConfigEntry(id = "custom_trapped", translation = "lootr.configuration.custom_trapped")
    @Comment(value = "", translation = "lootr.configuration.custom_trapped.desc")
    public static boolean customTrapped = false;
  }

  @Category("Notifications")
  @ConfigInfo(titleTranslation = "lootr.configuration.notifications", descriptionTranslation = "lootr.configuration.notifications.desc")
  public static class Notifications {
    @ConfigEntry(id = "report_unresolved_tables", translation = "lootr.configuration.report_unresolved_tables")
    @Comment(value = "", translation = "lootr.configuration.report_unresolved_tables.desc")
    public static boolean reportUnresolvedTables = true;

    @ConfigEntry(id = "disable_notifications", translation = "lootr.configuration.disable_notifications")
    @Comment(value = "", translation = "lootr.configuration.disable_notifications.desc")
    public static boolean disableNotifications = false;

    @ConfigEntry(id = "maximum_notification_delay", translation = "lootr.configuration.maximum_notification_delay")
    @Comment(value = "", translation = "lootr.configuration.maximum_notification_delay.desc")
    @ConfigOption.Range(min = -1, max = Integer.MAX_VALUE)
    public static int maximumNotificationDelay = 30 * 20;

    @ConfigEntry(id = "disable_message_styles", translation = "lootr.configuration.disable_message_styles")
    @Comment(value = "", translation = "lootr.configuration.disable_message_styles.desc")
    public static boolean disableMessageStyles = false;
  }

  @Category("Interaction")
  @ConfigInfo(titleTranslation = "lootr.configuration.interaction", descriptionTranslation = "lootr.configuration.interaction.desc")
  public static class Interaction {
    @ConfigEntry(id = "bypass_spawn_protection", translation = "lootr.configuration.bypass_spawn_protection")
    @Comment(value = "", translation = "lootr.configuration.bypass_spawn_protection.desc")
    public static boolean bypassSpawnProtection = true;
  }

  @Category("Decay")
  @ConfigInfo(titleTranslation = "lootr.configuration.decay", descriptionTranslation = "lootr.configuration.decay.desc")
  public static class Decay {
    @ConfigEntry(id = "decay_value", translation = "lootr.configuration.decay_value")
    @Comment(value = "", translation = "lootr.configuration.decay_value.desc")
    @ConfigOption.Range(min = 0, max = Integer.MAX_VALUE)
    public static int decayValue = 5 * 60 * 20;

    @ConfigEntry(id = "decay_loot_tables", translation = "lootr.configuration.decay_loot_tables")
    @Comment(value = "", translation = "lootr.configuration.decay_loot_tables.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static Observable<String[]> decayLootTables = Observable.of(new String[]{});

    @ConfigEntry(id="decay_loot_table_modids", translation = "lootr.configuration.decay_loot_table_modids")
    @Comment(value = "", translation = "lootr.configuration.decay_loot_table_modids.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static Observable<String[]> decayLootTableModids = Observable.of(new String[]{});

    @ConfigEntry(id="decay_dimensions", translation = "lootr.configuration.decay_dimensions")
    @Comment(value = "", translation = "lootr.configuration.decay_dimensions.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static Observable<String[]> decayDimensions = Observable.of(new String[]{});

    @ConfigEntry(id="replace_when_decayed", translation = "lootr.configuration.replace_when_decayed")
    @Comment(value = "", translation = "lootr.configuration.replace_when_decayed.desc")
    public static boolean replaceWhenDecayed = false;

    @ConfigEntry(id="perform_decay_while_ticking", translation = "lootr.configuration.perform_decay_while_ticking")
    @Comment(value = "", translation = "lootr.configuration.perform_decay_while_ticking.desc")
    public static boolean performDecayWhileTicking = true;

    @ConfigEntry(id="start_decay_while_ticking", translation = "lootr.configuration.start_decay_while_ticking")
    @Comment(value = "", translation = "lootr.configuration.start_decay_while_ticking.desc")
    public static boolean startDecayWhileTicking = false;

    @ConfigEntry(id="decay_all", translation = "lootr.configuration.decay_all")
    @Comment(value = "", translation = "lootr.configuration.decay_all.desc")
    public static boolean decayAll = false;
  }

  @Category("Refresh")
  @ConfigInfo(titleTranslation = "lootr.configuration.refresh", descriptionTranslation = "lootr.configuration.refresh.desc")
  public static class Refresh {
    @ConfigEntry(id = "refresh_value", translation = "lootr.configuration.refresh_value")
    @Comment(value = "", translation = "lootr.configuration.refresh_value.desc")
    @ConfigOption.Range(min = 0, max = Integer.MAX_VALUE)
    public static int refreshValue = 5 * 60 * 20;

    @ConfigEntry(id = "refresh_loot_tables", translation = "lootr.configuration.refresh_loot_tables")
    @Comment(value = "", translation = "lootr.configuration.refresh_loot_tables.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static Observable<String[]> refreshLootTables = Observable.of(new String[]{});

    @ConfigEntry(id="refresh_loot_table_modids", translation = "lootr.configuration.refresh_loot_table_modids")
    @Comment(value = "", translation = "lootr.configuration.refresh_loot_table_modids.desc")
    @ConfigOption.Regex(value = "^" + Identifier.ALLOWED_NAMESPACE_CHARACTERS + "+$")
    public static Observable<String[]> refreshLootTableModids = Observable.of(new String[]{});

    @ConfigEntry(id="refresh_dimensions", translation = "lootr.configuration.refresh_dimensions")
    @Comment(value = "", translation = "lootr.configuration.refresh_dimensions.desc")
    @ConfigOption.Regex(value = IDENTIFIER_REGEX)
    public static Observable<String[]> refreshDimensions = Observable.of(new String[]{});

    @ConfigEntry(id="perform_refresh_while_ticking", translation = "lootr.configuration.perform_refresh_while_ticking")
    @Comment(value = "", translation = "lootr.configuration.perform_refresh_while_ticking.desc")
    public static boolean performRefreshWhileTicking = true;

    @ConfigEntry(id="start_refresh_while_ticking", translation = "lootr.configuration.start_refresh_while_ticking")
    @Comment(value = "", translation = "lootr.configuration.start_refresh_while_ticking.desc")
    public static boolean startRefreshWhileTicking = false;

    @ConfigEntry(id="refresh_all", translation = "lootr.configuration.refresh_all")
    @Comment(value = "", translation = "lootr.configuration.refresh_all.desc")
    public static boolean refreshAll = false;
  }
}
