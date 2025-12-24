package noobanidus.mods.lootr.neoforge.init;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.TicketType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import noobanidus.mods.lootr.common.api.LootrAPI;

public class ModTicketTypes {
    private static final DeferredRegister<TicketType> REGISTER = DeferredRegister.create(BuiltInRegistries.TICKET_TYPE, LootrAPI.MODID);

    public static final DeferredHolder<TicketType, TicketType> ENTITY_TICKET_TYPE = REGISTER.register(LootrAPI.LOOTR_ENTITY_TICK_TICKET.identifier().getPath(), () -> new TicketType(300L, TicketType.FLAG_LOADING | TicketType.FLAG_SIMULATION, true));

    public static void register(IEventBus bus) {
        REGISTER.register(bus);
    }
}
