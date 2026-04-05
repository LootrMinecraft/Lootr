package noobanidus.mods.lootr.common.api.data;

import net.minecraft.resources.Identifier;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.UUID;

public interface IKeyedData {
  UUID getDataId();

  Identifier getDataIdentifier();

  int getDataKey();

  static int generateInfoIntKey (UUID id) {
    // >>> 'cos signedednessingthing
    return (int) (id.getMostSignificantBits() >>> 56);
  }

  static Identifier generateInfoIdentifier (UUID id) {
    return generateInfoIdentifier(generateInfoIntKey(id));
  }

  static Identifier generateInfoIdentifier (int key) {
    String hex = Integer.toHexString(key);
    return LootrAPI.rl(hex.charAt(0) + "/" + hex);
  }
}
