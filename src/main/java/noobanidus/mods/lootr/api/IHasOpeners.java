package noobanidus.mods.lootr.api;

import java.util.Set;
import java.util.UUID;

public interface IHasOpeners {
   void clearOpeners ();
   Set<UUID> getOpeners();
}
