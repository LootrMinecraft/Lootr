package noobanidus.mods.lootr.common.impl;

public class LockMessageSuppression {
  private static boolean shouldSuppress = false;

  public static void setSuppressableLock(boolean suppress) {
    shouldSuppress = suppress;
  }

  @SuppressWarnings("BooleanMethodIsAlwaysInverted")
  public static boolean isSuppressed() {
    return shouldSuppress;
  }
}
