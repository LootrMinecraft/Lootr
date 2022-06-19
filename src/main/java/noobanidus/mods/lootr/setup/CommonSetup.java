package noobanidus.mods.lootr.setup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.networking.PacketHandler;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public abstract class CommonSetup {
    public void preInit() {

    }

    public abstract EntityPlayer getPlayer();
  public void init() {
      PacketHandler.registerMessages();
  }

  public abstract void changeCartStatus(int entityId, boolean status);
}
