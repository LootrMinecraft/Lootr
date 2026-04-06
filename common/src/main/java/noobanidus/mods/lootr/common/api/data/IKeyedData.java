package noobanidus.mods.lootr.common.api.data;

import net.minecraft.resources.Identifier;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.UUID;

public interface IKeyedData {
  UUID getDataId();

  Identifier getDataIdentifier();

  static Identifier generateInfoIdentifier (IKeyedData data) {
    return generateInfoIdentifier(data.getDataId());
  }

  static Identifier generateInfoIdentifier (UUID dataId) {
    // >>> 'cos signedness
    String hex = Integer.toHexString((int) (dataId.getMostSignificantBits() >>> 56));
    return LootrAPI.rl(hex.charAt(0) + "/" + hex);
  }
}
