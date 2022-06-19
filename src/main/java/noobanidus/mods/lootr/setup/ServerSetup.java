package noobanidus.mods.lootr.setup;

import net.minecraft.entity.player.EntityPlayer;
import noobanidus.mods.lootr.impl.ServerGetter;

public class ServerSetup extends CommonSetup {
    @Override
    public EntityPlayer getPlayer() {
        return ServerGetter.getPlayer();
    }

    @Override
    public void changeCartStatus(int entityId, boolean status) {
        throw new UnsupportedOperationException("Should only run on client side");
    }
}
