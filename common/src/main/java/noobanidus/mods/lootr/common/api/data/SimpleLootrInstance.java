package noobanidus.mods.lootr.common.api.data;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.NBTConstants;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SimpleLootrInstance {
  private static final Logger LOGGER = LogUtils.getLogger();

  protected NonNullList<ItemStack> items;
  protected final Set<UUID> clientOpeners = new ObjectOpenHashSet<>();
  protected UUID infoId = null;
  protected boolean hasBeenOpened = false;
  protected String cachedId;
  protected boolean clientOpened = false;
  protected boolean savingToItem = false;

  protected boolean providesOwnUuid = false;

  protected final Supplier<Set<UUID>> visualOpenersSupplier;

  // What in the recursive, self-referential is this
  public SimpleLootrInstance(Supplier<Set<UUID>> visualOpenersSupplier, int size) {
    this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    this.visualOpenersSupplier = visualOpenersSupplier;
  }

  public NonNullList<ItemStack> getItems() {
    return items;
  }

  // TODO:
  public void setItems(NonNullList<ItemStack> items) {
    this.items = items;
  }

  public Set<UUID> getClientOpeners() {
    return clientOpeners;
  }

  public boolean isClientOpened() {
    return clientOpened;
  }

  public void setClientOpened(boolean opened) {
    this.clientOpened = opened;
  }

  public @NotNull UUID getInfoUUID() {
    if (providesOwnUuid) {
      throw new IllegalStateException("This instance provides its own UUID but hasn't overriden `getInfoUUID`: " + this);
    }
    if (this.infoId == null) {
      this.infoId = UUID.randomUUID();
    }
    return this.infoId;
  }

  public String getInfoKey() {
    if (cachedId == null) {
      cachedId = ILootrInfo.generateInfoKey(getInfoUUID());
    }
    return cachedId;
  }

  public boolean hasBeenOpened() {
    return hasBeenOpened;
  }

  public int getInfoContainerSize() {
    return items.size();
  }

  public void setHasBeenOpened() {
    this.hasBeenOpened = true;
  }

  public boolean isSavingToItem() {
    return savingToItem;
  }

  public void setSavingToItem(boolean saving) {
    this.savingToItem = saving;
  }

  public void loadAdditional(ValueInput input) {
    if (!providesOwnUuid) {
      this.infoId = input.read(NBTConstants.INSTANCE_ID, UUIDUtil.CODEC).orElse(null);
    }
    this.hasBeenOpened = input.getBooleanOr(NBTConstants.HAS_BEEN_OPENED, false);
    if (this.infoId == null && !providesOwnUuid) {
      getInfoUUID();
    }
    clientOpeners.clear();
    input.read(NBTConstants.OPENERS, UUIDUtil.CODEC_SET).map(clientOpeners::addAll);
  }

  public void saveAdditional(ValueOutput output, boolean isClientSide) {
    if (!LootrAPI.shouldDiscard() && !isSavingToItem() && !providesOwnUuid) {
      output.store(NBTConstants.INSTANCE_ID, UUIDUtil.CODEC, getInfoUUID());
    }
    output.putBoolean(NBTConstants.HAS_BEEN_OPENED, this.hasBeenOpened);
    if (isClientSide) { // level != null && level.isClientSide()) { ?????? This logic seems inverted.
      if (!clientOpeners.isEmpty()) {
        output.store(NBTConstants.OPENERS, UUIDUtil.CODEC_SET, clientOpeners);
      }
    }
  }

  public CompoundTag fillUpdateTag(HolderLookup.Provider provider, boolean isClientSide, BlockEntity parent) {
    try (ProblemReporter.ScopedCollector p = new ProblemReporter.ScopedCollector(LOGGER)) {
      ProblemReporter p2 = p.forChild(parent.problemPath());
      TagValueOutput output = TagValueOutput.createWithContext(p2, provider);

      saveAdditional(output, isClientSide);
      if (!isClientSide) {
        Set<UUID> currentOpeners = Sets.intersection(visualOpenersSupplier.get(), LootrAPI.getPlayerIds());
        output.store(NBTConstants.OPENERS, UUIDUtil.CODEC_SET, currentOpeners);
      } else {
        LootrAPI.LOG.error("Tried to fillUpdateTag on the client side for SimpleLootrInstance: {}", this);
      }

      return output.buildResult();
    }
  }
}
