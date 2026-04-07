package noobanidus.mods.lootr.common.api.helper;

import com.google.common.collect.Sets;
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
import noobanidus.mods.lootr.common.api.data.IKeyedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;

public class SimpleLootrInstance {
  protected final NonNullList<ItemStack> items;
  protected final Supplier<Set<UUID>> visualOpenersSupplier;
  protected final Set<UUID> clientOpeners = new ObjectOpenHashSet<>();

  protected NonNullList<ItemStack> referenceInventory = null;
  protected UUID id = null;
  protected Identifier cachedIdentifier;
  protected boolean clientOpened = false;
  protected boolean hasBeenOpened = false;
  protected boolean providesOwnUuid = false;

  public SimpleLootrInstance(Supplier<Set<UUID>> visualOpenersSupplier, int size) {
    this.items = NonNullList.withSize(size, ItemStack.EMPTY);
    this.visualOpenersSupplier = visualOpenersSupplier;
  }

  @NotNull
  public NonNullList<ItemStack> getEmptyInventory() {
    return items;
  }

  public void setReferenceInventory(@Nullable NonNullList<ItemStack> items) {
    this.referenceInventory = items;
  }

  @Nullable
  public NonNullList<ItemStack> getReferenceInventory() {
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

  public @NotNull UUID getId() {
    if (providesOwnUuid) {
      throw new IllegalStateException("This instance provides its own UUID but hasn't overriden `getInfoUUID`: " + this);
    }
    if (this.id == null) {
      this.id = UUID.randomUUID();
    }
    return this.id;
  }

  public Identifier getIdentifier() {
    if (cachedIdentifier == null) {
      this.cachedIdentifier = IKeyedData.generateInfoIdentifier(getId());
    }
    return cachedIdentifier;
  }

  public int getContainerSize() {
    return items.size();
  }

  public boolean hasBeenOpened() {
    return hasBeenOpened;
  }

  public void setHasBeenOpened() {
    this.hasBeenOpened = true;
  }

  public void loadAdditional(ValueInput input) {
    if (!providesOwnUuid) {
      this.id = input.read(NBTConstants.INSTANCE_ID, UUIDUtil.CODEC).orElse(null);
    }
    this.hasBeenOpened = input.getBooleanOr(NBTConstants.HAS_BEEN_OPENED, false);
    if (this.id == null && !providesOwnUuid) {
      getId();
    }
    if (this.cachedIdentifier == null) {
      getIdentifier();
    }
    clientOpeners.clear();
    input.read(NBTConstants.OPENERS, UUIDUtil.CODEC_SET).map(clientOpeners::addAll);
    if (input.getBooleanOr(NBTConstants.IS_CUSTOM_INVENTORY, false)) {
      if (this.referenceInventory == null) {
        this.referenceInventory = NonNullList.withSize(getContainerSize(), ItemStack.EMPTY);
      }
      ContainerHelper.loadAllItems(input, this.referenceInventory);
    }
  }

  public void saveAdditional(ValueOutput output, boolean isClientSide) {
    if (!LootrAPI.shouldDiscard() && !providesOwnUuid) {
      output.store(NBTConstants.INSTANCE_ID, UUIDUtil.CODEC, getId());
    }
    output.putBoolean(NBTConstants.HAS_BEEN_OPENED, this.hasBeenOpened);
    if (isClientSide) {
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
    try (ProblemReporter.ScopedCollector p = new ProblemReporter.ScopedCollector(LootrAPI.LOG)) {
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
