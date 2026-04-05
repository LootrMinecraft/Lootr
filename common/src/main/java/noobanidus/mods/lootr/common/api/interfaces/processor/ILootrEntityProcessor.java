package noobanidus.mods.lootr.common.api.interfaces.processor;

import net.minecraft.world.entity.Entity;

public interface ILootrEntityProcessor {
  non-sealed interface Pre extends ILootrProcessor.Pre<Entity> {

  }

  non-sealed interface Post extends ILootrProcessor.Post<Entity> {

  }
}
