package noobanidus.mods.lootr.common.client.gui.components.toasts;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.client.ContainerStatus;

import java.util.ArrayList;
import java.util.List;

public class LootrToast implements Toast {
  private static final Identifier BACKGROUND_SPRITE = LootrAPI.rl("toast/container_alert");
  private final Toasts toast;
  private final List<FormattedCharSequence> lines;
  private Toast.Visibility wantedVisibility = Toast.Visibility.HIDE;

  public LootrToast(Font font, Toasts toast, Component title, Component message) {
    this.toast = toast;
    this.lines = new ArrayList<>(2);
    this.lines.addAll(font.split(title.copy().withColor(-11534256), 126));
    if (message != null) {
      this.lines.addAll(font.split(message, 126));
    }
  }

  public LootrToast(Font font, ContainerStatus status, ContainerStatus.Message message) {
    this(font, status == ContainerStatus.REFRESH ? Toasts.REFRESH : Toasts.DECAY, message.title(), message.message());
  }

  @Override
  public Visibility getWantedVisibility() {
    return wantedVisibility;
  }

  @Override
  public void update(ToastManager manager, long fullyVisibleForMs) {
    this.wantedVisibility = fullyVisibleForMs >= 3200.0 * manager.getNotificationDisplayTimeMultiplier() ? Visibility.HIDE : Visibility.SHOW;
  }

  @Override
  public int height() {
    return 7 + this.contentHeight() + 3;
  }

  private int contentHeight() {
    return Math.max(this.lines.size(), 2) * 11;
  }

  @Override
  public void extractRenderState(GuiGraphicsExtractor graphics, Font font, long fullyVisibleForMs) {
    int height = this.height();
    graphics.blitSprite(RenderPipelines.GUI_TEXT, BACKGROUND_SPRITE, 0, 0, this.width(), height);
    this.toast.extractRenderState(graphics, 6, 6);
    int textHeight = this.lines.size() * 11;
    int textTop = 7 + (this.contentHeight() - textHeight) / 2;

    for (int i = 0; i < this.lines.size(); i++) {
      graphics.text(font, this.lines.get(i), 30, textTop + i * 11, -16777216, false);
    }
  }

  public enum Toasts {
    DECAY(LootrAPI.rl("toast/decay")),
    REFRESH(LootrAPI.rl("toast/refresh"));

    private final Identifier sprite;

    Toasts(Identifier sprite) {
      this.sprite = sprite;
    }

    public void extractRenderState(GuiGraphicsExtractor graphics, int x, int y) {
      graphics.blitSprite(RenderPipelines.GUI_TEXT, this.sprite, x, y, 20, 20);
    }
  }
}
