package noobanidus.mods.lootr.common.api.advancement;

import net.minecraft.advancements.CriterionTrigger;
import org.apache.commons.lang3.NotImplementedException;

public interface ITrigger {
  default CriterionTrigger<?> getTrigger() {
    if (this instanceof CriterionTrigger<?> trigger) {
      return trigger;
    }

    throw new NotImplementedException("This trigger does not have a CriterionTrigger associated with it. That's bad!");
  }
}
