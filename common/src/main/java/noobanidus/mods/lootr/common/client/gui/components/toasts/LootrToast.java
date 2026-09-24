package noobanidus.mods.lootr.common.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.common.api.LootrAPI;

import java.util.function.IntFunction;

public class LootrToast implements Toast {
  private static final ResourceLocation BACKGROUND_SPRITE = LootrAPI.rl("toast/container_alert");
  private final LootrToast.Toasts toast;
  private final int value;
  private final ToastType type;

  public LootrToast(LootrToast.Toasts toast, LootrToast.ToastType type, int value) {
    this.toast = toast;
    this.value = value;
    this.type = type;
  }

  @Override
  public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible) {
    guiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
    this.toast.render(guiGraphics, 6, 6);
    guiGraphics.drawString(toastComponent.getMinecraft().font, type == ToastType.ONGOING ? toast.ongoing_title : toast.start_title, 30, 7, -11534256, false);
    guiGraphics.drawString(toastComponent.getMinecraft().font, this.toast.message_supplier.apply(value), 30, 18, -16777216, false);

    return (double) timeSinceLastVisible >= 1200.0 * toastComponent.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
  }

  public enum Toasts {
    DECAY(LootrAPI.rl("toast/decay"), Component.translatable("lootr.toast.decay.start"), Component.translatable("lootr.toast.decay.ongoing"), val -> Component.translatable("lootr.toast.decay.message", val)),
    REFRESH(LootrAPI.rl("toast/refresh"), Component.translatable("lootr.toast.refresh.start"), Component.translatable("lootr.toast.refresh.ongoing"), val -> Component.translatable("lootr.toast.refresh.message"));

    private final ResourceLocation sprite;
    private final Component start_title;
    private final Component ongoing_title;
    private final IntFunction<Component> message_supplier;

    Toasts(ResourceLocation sprite, Component start_title, Component ongoing_title, IntFunction<Component> message_supplier) {
      this.sprite = sprite;
      this.start_title = start_title;
      this.ongoing_title = ongoing_title;
      this.message_supplier = message_supplier;
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
      RenderSystem.enableBlend();
      guiGraphics.blitSprite(this.sprite, x, y, 20, 20);
    }
  }

  public enum ToastType {
    START,
    ONGOING;
  }
}
