package noobanidus.mods.lootr.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.handling.*;
import net.neoforged.neoforge.network.registration.IDirectionAwarePayloadHandlerBuilder;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

/* Shamelessly crib from Mekanism until it works
 * Original source: https://github.com/mekanism/Mekanism/blob/1.20.4/src/main/java/mekanism/common/network/BasePacketHandler.java
 * */

public abstract class BasePacketHandler {
  protected BasePacketHandler(IEventBus modEventBus, String modid, String version) {
    modEventBus.addListener(RegisterPayloadHandlerEvent.class, event -> {
      IPayloadRegistrar registrar = event.registrar(modid)
          .versioned(version);
      registerClientToServer(new PacketRegistrar(registrar, IDirectionAwarePayloadHandlerBuilder::server));
      registerServerToClient(new PacketRegistrar(registrar, IDirectionAwarePayloadHandlerBuilder::client));
    });
  }

  protected abstract void registerClientToServer(PacketRegistrar registrar);

  protected abstract void registerServerToClient(PacketRegistrar registrar);

  @FunctionalInterface
  private interface ContextAwareHandler {

    <PAYLOAD extends CustomPacketPayload, HANDLER> IDirectionAwarePayloadHandlerBuilder<PAYLOAD, HANDLER> accept(IDirectionAwarePayloadHandlerBuilder<PAYLOAD, HANDLER> builder, HANDLER handler);
  }

  protected record PacketRegistrar(IPayloadRegistrar registrar, ContextAwareHandler contextAwareHandler) {

    private <MSG extends ILootrPacket<IPayloadContext>> void common(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader, IPayloadHandler<MSG> handler) {
      registrar.common(id, reader, builder -> contextAwareHandler.accept(builder, handler));
    }

    public <MSG extends ILootrPacket<IPayloadContext>> void common(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader) {
      common(id, reader, ILootrPacket::handleMainThread);
    }

    public <MSG extends ILootrPacket<IPayloadContext>> void commonNetworkThread(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader) {
      common(id, reader, ILootrPacket::handle);
    }

    public ILootrPacket<IPayloadContext> commonInstanced(ResourceLocation id, Consumer<IPayloadContext> handler) {
      return instanced(id, handler, this::common);
    }

    private <MSG extends ILootrPacket<ConfigurationPayloadContext>> void configuration(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader, IConfigurationPayloadHandler<MSG> handler) {
      registrar.configuration(id, reader, builder -> contextAwareHandler.accept(builder, handler));
    }

    public void configuration(ResourceLocation id, FriendlyByteBuf.Reader<? extends ILootrPacket<ConfigurationPayloadContext>> reader) {
      configuration(id, reader, ILootrPacket::handleMainThread);
    }

    public void configurationNetworkThread(ResourceLocation id, FriendlyByteBuf.Reader<? extends ILootrPacket<ConfigurationPayloadContext>> reader) {
      configuration(id, reader, ILootrPacket::handle);
    }

    public ILootrPacket<ConfigurationPayloadContext> configurationInstanced(ResourceLocation id, Consumer<ConfigurationPayloadContext> handler) {
      return instanced(id, handler, this::configuration);
    }

    private <MSG extends ILootrPacket<PlayPayloadContext>> void play(ResourceLocation id, FriendlyByteBuf.Reader<MSG> reader, IPlayPayloadHandler<MSG> handler) {
      registrar.play(id, reader, builder -> contextAwareHandler.accept(builder, handler));
    }

    public void play(ResourceLocation id, FriendlyByteBuf.Reader<? extends ILootrPacket<PlayPayloadContext>> reader) {
      play(id, reader, ILootrPacket::handleMainThread);
    }

    public void playNetworkThread(ResourceLocation id, FriendlyByteBuf.Reader<? extends ILootrPacket<PlayPayloadContext>> reader) {
      play(id, reader, ILootrPacket::handle);
    }

    public ILootrPacket<PlayPayloadContext> playInstanced(ResourceLocation id, Consumer<PlayPayloadContext> handler) {
      return instanced(id, handler, this::play);
    }

    private <CONTEXT extends IPayloadContext> ILootrPacket<CONTEXT> instanced(ResourceLocation id, Consumer<CONTEXT> handler,
                                                                              BiConsumer<ResourceLocation, FriendlyByteBuf.Reader<ILootrPacket<CONTEXT>>> registerMethod) {
      ILootrPacket<CONTEXT> instance = new ILootrPacket<>() {
        @Override
        public void write(@NotNull FriendlyByteBuf buf) {
        }

        @NotNull
        @Override
        public ResourceLocation id() {
          return id;
        }

        @Override
        public void handle(CONTEXT context) {
          handler.accept(context);
        }
      };
      registerMethod.accept(id, buf -> instance);
      return instance;
    }
  }
}
