package noobanidus.mods.lootr.setup;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import noobanidus.mods.lootr.Lootr;
import noobanidus.mods.lootr.data.DataStorage;
import noobanidus.mods.lootr.networking.PacketHandler;

@Mod.EventBusSubscriber(modid = Lootr.MODID)
public abstract class CommonSetup {
    public void preInit() {

    }

    public abstract EntityPlayer getPlayer();
  public void init() {
      PacketHandler.registerMessages();
  }

  @SubscribeEvent
  public static void serverTick(TickEvent.ServerTickEvent e) {
      if(e.phase == TickEvent.Phase.END) {
          DataStorage.doDecay();
          DataStorage.doRefresh();
      }
  }

  public abstract void changeCartStatus(int entityId, boolean status);
}
