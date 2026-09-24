package noobanidus.mods.lootr.common.client.gui.components.toasts;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import noobanidus.mods.lootr.common.api.LootrAPI;

public class LootrToast implements Toast {
  private static final ResourceLocation BACKGROUND_SPRITE = LootrAPI.rl("toast/container_alert");
  private final LootrToast.Icons icon;
  private final Component title;
  private final Component message;

  public LootrToast(LootrToast.Icons icon, Component title, Component message) {
    this.icon = icon;
    this.title = title;
    this.message = message;
  }

  @Override
  public Visibility render(GuiGraphics guiGraphics, ToastComponent toastComponent, long timeSinceLastVisible) {
    guiGraphics.blitSprite(BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
    this.icon.render(guiGraphics, 6, 6);
    guiGraphics.drawString(toastComponent.getMinecraft().font, this.title, 30, 7, -11534256, false);
    guiGraphics.drawString(toastComponent.getMinecraft().font, this.message, 30, 18, -16777216, false);

    return (double) timeSinceLastVisible >= 1200.0 * toastComponent.getNotificationDisplayTimeMultiplier() ? Toast.Visibility.HIDE : Toast.Visibility.SHOW;
  }

  public enum Icons {
    DECAY(LootrAPI.rl("toast/decay")),
    REFRESH(LootrAPI.rl("toast/refresh"));

    private final ResourceLocation sprite;

    Icons(ResourceLocation sprite) {
      this.sprite = sprite;
    }

    public void render(GuiGraphics guiGraphics, int x, int y) {
      RenderSystem.enableBlend();
      guiGraphics.blitSprite(this.sprite, x, y, 20, 20);
    }
  }
}
