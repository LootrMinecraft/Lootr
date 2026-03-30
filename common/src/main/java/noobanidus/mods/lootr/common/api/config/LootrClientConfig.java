package noobanidus.mods.lootr.common.api.config;

import com.teamresourceful.resourcefulconfig.api.annotations.*;

@Config(value="lootr-client", categories = {
    LootrClientConfig.Particles.class,
    LootrClientConfig.Textures.class
})
@ConfigInfo(titleTranslation = "lootr.config.client.title", descriptionTranslation = "lootr.config.client.desc")
public class LootrClientConfig {
  @Category("Particles")
  @ConfigInfo(titleTranslation = "lootr.config.particles.title", descriptionTranslation = "lootr.config.particles.description")
  public static class Particles {
    @ConfigEntry(id="unopened_particles", translation="lootr.config.particles.unopened_particles")
    @Comment(value="", translation = "lootr.config.particles.unopened_particles.desc")
    public static boolean showUnopenedParticles = true;
  }

  @Category("Textures")
  @ConfigInfo(titleTranslation = "lootr.config.textures.title", descriptionTranslation = "lootr.config.textures.description")
  public static class Textures {
    @ConfigEntry(id="vanilla_textures", translation="lootr.config.textures.vanilla_textures")
    @Comment(value="", translation = "lootr.config.textures.vanilla_textures.desc")
    public static boolean useVanillaTextures = false;
  }
}
