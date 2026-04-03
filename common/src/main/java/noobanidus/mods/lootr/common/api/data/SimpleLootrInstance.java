package noobanidus.mods.lootr.common.api.data;

import com.google.common.collect.Sets;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
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
  protected NonNullList<ItemStack> referenceInventory = null;
  protected final Set<UUID> clientOpeners = new ObjectOpenHashSet<>();
  protected UUID infoId = null;
  protected boolean hasBeenOpened = false;
  protected int cachedKey;
  protected Identifier cachedIdentifier;
  protected boolean clientOpened = false;

  protected boolean providesOwnUuid = false;

  protected final Supplier<Set<UUID>> visualOpenersSupplier;

  public SimpleLootrInstance(Supplier<Set<UUID>> visualOpenersSupplier, int size) {
    this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    this.visualOpenersSupplier = visualOpenersSupplier;
  }

  public NonNullList<ItemStack> getItems() {
    return items;
  }

  public void setReferenceInventory(NonNullList<ItemStack> items) {
    this.referenceInventory = items;
  }

  public NonNullList<ItemStack> getReferenceInventory () {
    return this.referenceInventory;
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
      this.cachedKey = IKeyedData.generateInfoIntKey(this.infoId);
      this.cachedIdentifier = IKeyedData.generateInfoIdentifier(this.infoId);
    }
    return this.infoId;
  }

  public int getInfoKey() {
    if (!providesOwnUuid && this.infoId == null) {
      getInfoUUID();
    }
    return cachedKey;
  }

  public Identifier getInfoIdentifier () {
    if (cachedIdentifier == null) {
      getInfoUUID();
    }
    return cachedIdentifier;
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
    if (input.getBooleanOr(NBTConstants.IS_CUSTOM_INVENTORY, false)) {
      if (this.referenceInventory == null) {
        this.referenceInventory = NonNullList.withSize(getInfoContainerSize(), ItemStack.EMPTY);
      }
      ContainerHelper.loadAllItems(input, this.referenceInventory);
    }
  }

  public void saveAdditional(ValueOutput output, boolean isClientSide) {
    if (!LootrAPI.shouldDiscard() && !providesOwnUuid) {
      output.store(NBTConstants.INSTANCE_ID, UUIDUtil.CODEC, getInfoUUID());
    }
    output.putBoolean(NBTConstants.HAS_BEEN_OPENED, this.hasBeenOpened);
    if (isClientSide) { // level != null && level.isClientSide()) { ?????? This logic seems inverted.
      if (!clientOpeners.isEmpty()) {
        output.store(NBTConstants.OPENERS, UUIDUtil.CODEC_SET, clientOpeners);
      }
    }
    if (this.referenceInventory != null) {
      output.putBoolean(NBTConstants.IS_CUSTOM_INVENTORY, true);
      ContainerHelper.saveAllItems(output, this.referenceInventory);
    } else {
      output.putBoolean(NBTConstants.IS_CUSTOM_INVENTORY, false);
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

  public boolean isReferenceInventory() {
    return referenceInventory != null && !referenceInventory.isEmpty();
  }
}
