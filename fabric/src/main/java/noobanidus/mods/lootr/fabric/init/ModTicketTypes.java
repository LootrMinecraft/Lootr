package noobanidus.mods.lootr.fabric.init;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.TicketType;
import noobanidus.mods.lootr.common.api.LootrAPI;

public class ModTicketTypes {
    public static final TicketType ENTITY_TICKET_TYPE = new TicketType(300L, true, TicketType.TicketUse.LOADING_AND_SIMULATION);

    public static void registerTicketTypes() {
        Registry.register(BuiltInRegistries.TICKET_TYPE, LootrAPI.LOOTR_ENTITY_TICK_TICKET, ENTITY_TICKET_TYPE);
    }
}
