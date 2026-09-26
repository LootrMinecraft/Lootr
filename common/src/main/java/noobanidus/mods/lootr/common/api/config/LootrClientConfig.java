package noobanidus.mods.lootr.common.api.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Config(value = "lootr-client", categories = {
    LootrClientConfig.Particles.class,
    LootrClientConfig.Textures.class
})
@ConfigInfo(title = LootrConfigStrings.CLIENT_TITLE, titleTranslation = "lootr.config.client.title", description = LootrConfigStrings.CLIENT_DESC, descriptionTranslation = "lootr.config.client.desc")
public class LootrClientConfig {

  @Category("Particles")
  @ConfigInfo(title = LootrConfigStrings.PARTICLES_TITLE, titleTranslation = "lootr.config.particles.title", description = LootrConfigStrings.PARTICLES_DESC, descriptionTranslation = "lootr.config.particles.description")
  public static class Particles {
    @ConfigEntry(id = "unopened_particles", translation = "lootr.config.particles.unopened_particles")
    @Comment(value = LootrConfigStrings.UNOPENED_PARTICLES_DESC, translation = "lootr.config.particles.unopened_particles.desc")
    public static boolean showUnopenedParticles = true;

    @ConfigEntry(id = "refresh_particles", translation = "lootr.config.particles.refresh_particles")
    @Comment(value = LootrConfigStrings.REFRESH_PARTICLES_DESC, translation = "lootr.config.particles.refresh_particles.desc")
    public static boolean showRefreshParticles = true;

    @ConfigEntry(id = "decay_particles", translation = "lootr.config.particles.decay_particles")
    @Comment(value = LootrConfigStrings.DECAY_PARTICLES_DESC, translation = "lootr.config.particles.decay_particles.desc")
    public static boolean showDecayParticles = true;
  }

  @Category("Textures")
  @ConfigInfo(title = LootrConfigStrings.TEXTURES_TITLE, titleTranslation = "lootr.config.textures.title", description = LootrConfigStrings.TEXTURES_DESC, descriptionTranslation = "lootr.config.textures.description")
  public static class Textures {
    @ConfigEntry(id = "vanilla_textures", translation = "lootr.config.textures.vanilla_textures")
    @Comment(value = LootrConfigStrings.VANILLA_TEXTURES_DESC, translation = "lootr.config.textures.vanilla_textures.desc")
    public static boolean useVanillaTextures = false;
  }

  @Category("Notifications")
  @ConfigInfo(title = LootrConfigStrings.NOTIFICATIONS_TITLE, titleTranslation = "lootr.config.textures.title", description = LootrConfigStrings.NOTIFICATIONS_CLIENT_DESC, descriptionTranslation = "lootr.config.textures.description")
  public static class Notifications {
    @ConfigEntry(id = "display_toast", translation = "lootr.config.notifications.display_toasts")
    @Comment(value = LootrConfigStrings.DISPLAY_TOASTS_DESC, translation = "lootr.config.notifications.display_toasts.desc")
    public static boolean displayToasts = true;

    @ConfigEntry(id = "disable_notifications", translation = "lootr.configuration.disable_notifications")
    @Comment(value = LootrConfigStrings.DISABLE_NOTIFICATIONS_DESC, translation = "lootr.configuration.disable_notifications.desc")
    public static boolean disableNotifications = false;

    @ConfigEntry(id = "maximum_notification_delay", translation = "lootr.configuration.maximum_notification_delay")
    @Comment(value = LootrConfigStrings.MAXIMUM_NOTIFICATION_DELAY_DESC, translation = "lootr.configuration.maximum_notification_delay.desc")
    @ConfigOption.Range(min = -1, max = Integer.MAX_VALUE)
    public static int maximumNotificationDelay = 30 * 20;
  }
}
