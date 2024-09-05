package noobanidus.mods.lootr.common.debug;

import net.minecraft.core.registries.BuiltInRegistries;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.LootrTags;

public class TagChecker {
  private static void standardError() {
    LootrAPI.LOG.error("[Lootr Tag Error] Please check your server logs for more information. If you are using additional data packs, please ensure they don't reference blocks from mods that aren't loaded in this world.");
  }

  public static void checkTags() {
    BuiltInRegistries.BLOCK.getTag(LootrTags.Blocks.CONVERT_BARRELS).ifPresentOrElse(tag -> {
      if (tag.size() == 0) {
        LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/barrels` is empty. Barrels will not be converted to Lootr barrels. If this is intentional, you can disregard this message.");
        standardError();
      }
    }, () -> {
      LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/barrels` is missing. Barrels will not be converted to Lootr barrels.");
      standardError();
    });
    BuiltInRegistries.BLOCK.getTag(LootrTags.Blocks.CONVERT_CHESTS).ifPresentOrElse(tag -> {
      if (tag.size() == 0) {
        LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/chests` is empty. Chests will not be converted to Lootr chests. If this is intentional, you can disregard this message.");
        standardError();
      }
    }, () -> {
      LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/chests` is missing. Barrels will not be converted to Lootr chests.");
      standardError();
    });
    BuiltInRegistries.BLOCK.getTag(LootrTags.Blocks.CONVERT_SHULKERS).ifPresentOrElse(tag -> {
      if (tag.size() == 0) {
        LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/shulkers` is empty. Shulkers will not be converted to Lootr shulkers. If this is intentional, you can disregard this message.");
        standardError();
      }
    }, () -> {
      LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/shulkers` is missing. Shulkers will not be converted to Lootr shulkers.");
      standardError();
    });
    BuiltInRegistries.BLOCK.getTag(LootrTags.Blocks.CONVERT_TRAPPED_CHESTS).ifPresentOrElse(tag -> {
      if (tag.size() == 0) {
        LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/trapped_chests` is empty. Trapped chests will not be converted to Lootr trapped chests. If this is intentional, you can disregard this message.");
        standardError();
      }
    }, () -> {
      LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/trapped_chests` is missing. Trapped chests will not be converted to Lootr trapped chests.");
      standardError();
    });
    BuiltInRegistries.BLOCK.getTag(LootrTags.Blocks.CONVERT_BLOCK).ifPresentOrElse(tag -> {
      if (tag.size() == 0) {
        LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/blocks` is empty. This may prevent any block from being converted to a Lootr equivalent. If this is intentional, you may ignore this message.");
        standardError();
      }
    }, () -> {
      LootrAPI.LOG.error("[Lootr Tag Error] Block tag `lootr:convert/blocks` is missing. This may prevent any block from being converted to a Lootr equivalent. If this is intentional, you may ignore this message.");
      standardError();
    });
  }
}
