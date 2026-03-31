package noobanidus.mods.lootr.common.api.config;

public final class LootrConfigStrings {
  private LootrConfigStrings() {
  }

  // -------------------------------------------------------------------------
  // Client config
  // -------------------------------------------------------------------------
  public static final String CLIENT_TITLE = "Client Options";
  public static final String CLIENT_DESC = "Configuration options for individual clients";

  // Particles
  public static final String PARTICLES_TITLE = "Particle Options";
  public static final String PARTICLES_DESC = "Configuration options for the display of particles and other effects";
  public static final String UNOPENED_PARTICLES = "Display Particles from Unopened Containers";
  public static final String UNOPENED_PARTICLES_DESC = "In addition to a visual difference, containers that are not marked as 'opened' by the current player generate particles. [default: true]";

  // Textures
  public static final String TEXTURES_TITLE = "Texture Options";
  public static final String TEXTURES_DESC = "Configuration options for the textures of containers";
  public static final String VANILLA_TEXTURES = "Vanilla Textures";
  public static final String VANILLA_TEXTURES_DESC = "When true, replaces the Lootr-specific textures with Vanilla ones where possible. [default: false]";

  // -------------------------------------------------------------------------
  // Common config — top level
  // -------------------------------------------------------------------------
  public static final String COMMON_TITLE = "Server Options";
  public static final String COMMON_DESC = "Configuration options for server-specific or single-player worlds.";

  // Conversion
  public static final String CONVERSION_TITLE = "Conversion";
  public static final String CONVERSION_DESC = "Configuration options related to the conversion of Vanilla (or modded) containers into Lootr containers.";
  public static final String DISABLE = "Disable Conversion";
  public static final String DISABLE_DESC = "Prevents any conversion from taking place. [default: false]";
  public static final String SAVE_MODE = "Data Save Mode";
  public static final String SAVE_MODE_DESC = "Determines how aggressively Lootr generates data files. When specified to 'SMART', Lootr will check for Exaroton and other hosts and, if detected, use 'WHEN_OPENED', otherwise 'ALWAYS'. [default: SMART]";
  public static final String RANDOMISE_SEED = "Randomize Seed";
  public static final String RANDOMISE_SEED_DESC = "When true, generates a new seed for each player when generating Loot, instead of using the seed encoded in the container. If a container doesn't provide a seed, a random one will be generated regardless of this setting. [default: true]";
  public static final String CONVERT_ELYTRAS_TO_CHESTS = "Convert Elytra Item Frames into Chests";
  public static final String CONVERT_ELYTRAS_TO_CHESTS_DESC = "If true, the Elytra item frame found in the End City ship will be converted to a Lootr chest container. [default: false]";
  public static final String CONVERT_ELYTRAS_TO_ITEM_FRAMES = "Convert Elytra Item Frames into Lootr Item Frames";
  public static final String CONVERT_ELYTRAS_TO_ITEM_FRAMES_DESC = "If true, the Eltytra item frame found in the End city ship will be converted to a Lootr item frame. [default: true]";
  public static final String CONVERT_STRUCTURE_ITEM_FRAMES = "Convert Structure Item Frames";
  public static final String CONVERT_STRUCTURE_ITEM_FRAMES_DESC = "If true, item frames found in structures will be converted to Lootr item frames. [default: true]";
  public static final String CHECK_WORLD_BORDER = "Disable Conversion Outside World Border";
  public static final String CHECK_WORLD_BORDER_DESC = "If true, containers that exist outside of the world border will be ignored and not considered for conversion. [default: true]";
  public static final String PERFORM_PIECEWISE_CHECK = "Perform Piecewise Check";
  public static final String PERFORM_PIECEWISE_CHECK_DESC = "If true, features that check structures, such as refresh or decay structure tags, will check each piece of a structure to determine if the container is located in that structure. [default: true]";

  // Restrictions
  public static final String RESTRICTIONS_TITLE = "Restrictions";
  public static final String RESTRICTIONS_DESC = "Configuration options relating to the restriction of conversion of containers into Lootr containers.";
  public static final String DIMENSION_WHITELIST = "Dimension Whitelist";
  public static final String DIMENSION_WHITELIST_DESC = "List of dimensions in which Lootr containers will be converted.";
  public static final String DIMENSION_BLACKLIST = "Dimension Blacklist";
  public static final String DIMENSION_BLACKLIST_DESC = "List of dimensions in which Lootr containers will not be converted.";
  public static final String MODID_DIMENSION_WHITELIST = "Dimension Mod Id Whitelist";
  public static final String MODID_DIMENSION_WHITELIST_DESC = "List of dimension paths in which Lootr containers will be converted. [For example, the dimension `twilightforest:twilight_forest` has the 'path' `twilightforest`; the dimension `minecraft:the_nether` has the 'path' `minecraft`.]";
  public static final String MODID_DIMENSION_BLACKLIST = "Dimension Mod Id Blacklist";
  public static final String MODID_DIMENSION_BLACKLIST_DESC = "List of dimension paths in which Lootr containers will not be converted. [For example, the dimension `twilightforest:twilight_forest` has the 'path' `twilightforest`; the dimension `minecraft:the_nether` has the 'path' `minecraft`.]";
  public static final String LOOT_TABLE_BLACKLIST = "Loot Table Blacklist";
  public static final String LOOT_TABLE_BLACKLIST_DESC = "List of loot tables that, if matching that of a container, will prevent that container from being converted into a Lootr container.";
  public static final String LOOT_TABLE_MODID_BLACKLIST = "Loot Table Mod Id Blacklist";
  public static final String LOOT_TABLE_MODID_BLACKLIST_DESC = "List of loot table paths that, if matching that of a container, will prevent that container from being converted into a Lootr container. [For example, the loot table `minecraft:chests/simple_dungeon` has the 'path' `minecraft`.]";
  public static final String PROBLEMATIC_LOOT_TABLES = "Problematic Loot Tables";
  public static final String PROBLEMATIC_LOOT_TABLES_DESC = "List of 'problematic' loot tables which are automatically added to the Loot Table Blacklist. This shouldn't be edited by users.";

  // Breaking
  public static final String BREAKING_TITLE = "Breaking";
  public static final String BREAKING_DESC = "Configuration options relating to the breaking of Lootr containers.";
  public static final String BREAK_MODE = "Break Mode";
  public static final String BREAK_MODE_DESC = "Determines how containers react to being 'broken'. 'DEFAULT' will display the usual message and prevent breaking if the player is not sneaking. 'NEVER' prevents containers from being broken, while 'ALWAYS' allows containers to be broken regardless of sneaking. [default: DEFAULT]";
  public static final String ENABLE_FAKE_PLAYER_BREAK = "Enable Fake Player Breaking";
  public static final String ENABLE_FAKE_PLAYER_BREAK_DESC = "Fake players (such as turtles, miners, etc) will be able to break containers if this is true, regardless of the chosen break mode. [default: false]";
  public static final String BLAST_RESISTANCE = "Blast Resistance Mode";
  public static final String BLAST_RESISTANCE_DESC = "Determines how Lootr containers react to explosions. 'NONE' uses the default blast resistance. 'RESISTANT' makes containers blast resistant, while 'IMMUNE' makes them blast immune. [default: DEFAULT]";
  public static final String BRUSHABLES_SELF_SUPPORT = "Brushable Blocks Don't Fall";
  public static final String BRUSHABLES_SELF_SUPPORT_DESC = "If true, Lootr's brushable blocks do not act as 'falling' blocks and instead stay stationary when their supporting block is broken. [default: false]";
  public static final String ITEM_FRAMES_SELF_SUPPORT = "Item Frames Don't Fall/Break";
  public static final String ITEM_FRAMES_SELF_SUPPORT_DESC = "If true, Lootr's item frames do not break when their supporting blocks are broken. [default: false]";
  public static final String SHOULD_DROP_PLAYER_LOOT = "Broken Containers Drop Player's Loot";
  public static final String SHOULD_DROP_PLAYER_LOOT_DESC = "If true, when a container is broken by a player, the contents of that player's inventory (if not generated, it will be generated) for the container will also be dropped on the ground. [default: false]";

  // Redstone
  public static final String REDSTONE_TITLE = "Redstone Options";
  public static final String REDSTONE_DESC = "Options related to redstone power and comparators.";
  public static final String POWER_COMPARATORS = "Power Comparators";
  public static final String POWER_COMPARATORS_DESC = "If true, comparators will emit a redstone signal. [default: true]";
  public static final String CUSTOM_TRAPPED = "Custom Inventories Are Trapped";
  public static final String CUSTOM_TRAPPED_DESC = "If true, containers that are 'custom' will emit a redstone signal when opened, as if they were a trapped chest. [default: false]";

  // Notifications
  public static final String NOTIFICATIONS_TITLE = "Notification Options";
  public static final String NOTIFICATIONS_DESC = "Configuration options related to notifications that are sent to players.";
  public static final String REPORT_UNRESOLVED_TABLES = "Report Unresolved Loot Tables to Players";
  public static final String REPORT_UNRESOLVED_TABLES_DESC = "If true, when opening a container with an unresolved loot tables (loot tables that resolve to empty), a message will be sent to the player opening to indicate the issue. [default: true]";
  public static final String DISABLE_NOTIFICATIONS = "Disable Ticking Notifications";
  public static final String DISABLE_NOTIFICATIONS_DESC = "If true, notifications related to ticking (such as decay or refresh) will not be sent to players. [default: false]";
  public static final String MAXIMUM_NOTIFICATION_DELAY = "Maximum Ticking Delay";
  public static final String MAXIMUM_NOTIFICATION_DELAY_DESC = "If the remaining duration of a ticking container (such as one refreshing or decaying) in ticks is more than this value, notifications will not be sent to the player. [default: 30 * 20 (30 seconds), -1 disables the limit and will always send notifications.]";
  public static final String DISABLE_MESSAGE_STYLES = "Disable Style And Formatting In Ticking Notifications";
  public static final String DISABLE_MESSAGE_STYLES_DESC = "If true, the style and formatting of messages related to ticking (such as decay or refresh) will be removed. [default: false]";

  // Interaction
  public static final String INTERACTION_TITLE = "Interaction Options";
  public static final String INTERACTION_DESC = "Configuration options related to interacting with containers.";
  public static final String BYPASS_SPAWN_PROTECTION = "Bypass Spawn Protection";
  public static final String BYPASS_SPAWN_PROTECTION_DESC = "If true, Lootr containers that are within the spawn protection area of a world will still be interactable if the player interacting with it does not have permission. [default: true]";

  // Decay
  public static final String DECAY_TITLE = "Decay Options";
  public static final String DECAY_DESC = "Configuration options related to the decay of containers. [By default, no decay occurs.]";
  public static final String DECAY_VALUE = "Decay Duration value";
  public static final String DECAY_VALUE_DESC = "The length of time in ticks (20 per second) that it takes for a container marked to decay to actually decay. [default: 5 * 60 * 20 (5 minutes)]";
  public static final String DECAY_LOOT_TABLES = "Decay Loot Tables";
  public static final String DECAY_LOOT_TABLES_DESC = "List of loot tables that, if matching that of a container, will cause that container to decay after being opened.";
  public static final String DECAY_LOOT_TABLE_MODIDS = "Decay Loot Table Mod Ids";
  public static final String DECAY_LOOT_TABLE_MODIDS_DESC = "List of loot table paths that, if matching that of a container, will cause that container to decay after being opened. [For example, the loot table `minecraft:chests/simple_dungeon` has the 'path' `minecraft`.]";
  public static final String DECAY_DIMENSIONS = "Decay Dimensions";
  public static final String DECAY_DIMENSIONS_DESC = "List of dimensions in which containers will decay after being opened.";
  public static final String REPLACE_WHEN_DECAYED = "Replace When Decayed";
  public static final String REPLACE_WHEN_DECAYED_DESC = "If true, when a container is fully decayed, it will be replaced with the equivalent Vanilla container. [default: false]";
  public static final String PERFORM_DECAY_WHILE_TICKING = "Perform Decay While Ticking";
  public static final String PERFORM_DECAY_WHILE_TICKING_DESC = "If true, decay occurs during the world tick, meaning that containers will break if they are loaded. If false, containers will only be checked for decay when they are next opened. [default: true]";
  public static final String START_DECAY_WHILE_TICKING = "Start Decay While Ticking";
  public static final String START_DECAY_WHILE_TICKING_DESC = "If true, containers that are eligible to decay and have previously been opened by another player, will start decaying during the world tick. [default: false]";
  public static final String DECAY_ALL = "Decay All Containers";
  public static final String DECAY_ALL_DESC = "If true, all containers will decay after being opened. [default: false]";

  // Refresh
  public static final String REFRESH_TITLE = "Refresh Options";
  public static final String REFRESH_DESC = "Configuration options related to the refreshing of containers. [By default, no refreshing occurs.]";
  public static final String REFRESH_VALUE = "Refresh Duration Value";
  public static final String REFRESH_VALUE_DESC = "The length of time in ticks (20 per second) that it takes for a container marked to refresh to actually refresh. [default: 5 * 60 * 20 (5 minutes)]";
  public static final String REFRESH_LOOT_TABLES = "Refresh Loot Tables";
  public static final String REFRESH_LOOT_TABLES_DESC = "List of loot tables that, if matching that of a container, will cause that container to refresh after being opened.";
  public static final String REFRESH_LOOT_TABLE_MODIDS = "Refresh Loot Table Mod Ids";
  public static final String REFRESH_LOOT_TABLE_MODIDS_DESC = "List of loot table paths that, if matching that of a container, will cause that container to refresh after being opened. [For example, the loot table `minecraft:chests/simple_dungeon` has the 'path' `minecraft`.]";
  public static final String REFRESH_DIMENSIONS = "Refresh Dimensions";
  public static final String REFRESH_DIMENSIONS_DESC = "List of dimensions in which containers will refresh after being opened.";
  public static final String PERFORM_REFRESH_WHILE_TICKING = "Perform Refresh While Ticking";
  public static final String PERFORM_REFRESH_WHILE_TICKING_DESC = "If true, refreshing occurs during the world tick, meaning that containers will refresh if they are loaded. If false, containers will only be checked for refreshing when they are next opened. [default: true]";
  public static final String START_REFRESH_WHILE_TICKING = "Start Refresh While Ticking";
  public static final String START_REFRESH_WHILE_TICKING_DESC = "If true, containers that are eligible to refresh and have previously been opened by another player, will start refreshing during the world tick. [default: true]";
  public static final String REFRESH_ALL = "Refresh All Containers";
  public static final String REFRESH_ALL_DESC = "If true, all containers will refresh after being opened for the first time. [default: false]";
}
