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
  }

  @Category("Textures")
  @ConfigInfo(title = LootrConfigStrings.TEXTURES_TITLE, titleTranslation = "lootr.config.textures.title", description = LootrConfigStrings.TEXTURES_DESC, descriptionTranslation = "lootr.config.textures.description")
  public static class Textures {
    @ConfigEntry(id = "vanilla_textures", translation = "lootr.config.textures.vanilla_textures")
    @Comment(value = LootrConfigStrings.VANILLA_TEXTURES_DESC, translation = "lootr.config.textures.vanilla_textures.desc")
    public static boolean useVanillaTextures = false;
  }
}
