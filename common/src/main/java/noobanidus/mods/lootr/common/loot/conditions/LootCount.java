package noobanidus.mods.lootr.common.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.PrimitiveCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.phys.Vec3;
import noobanidus.mods.lootr.common.api.LootrAPI;
import noobanidus.mods.lootr.common.api.data.blockentity.ILootrBlockEntity;
import noobanidus.mods.lootr.common.api.registry.LootrRegistry;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

public record LootCount(List<Operation> operations) implements LootItemCondition {

  public static final MapCodec<LootCount> CODEC = RecordCodecBuilder.mapCodec(
      builder -> builder.group(
              Operation.CODEC.listOf().xmap(operationList -> operationList.stream().sorted(Comparator.comparingInt(Operation::getPrecedence)).toList(), Function.identity()).fieldOf("operations").forGetter(LootCount::operations)
          )
          .apply(builder, LootCount::new)
  );

  @Override
  public LootItemConditionType getType() {
    return LootrRegistry.getLootCount();
  }

  @Override
  public boolean test(LootContext lootContext) {
    Vec3 incomingPos = lootContext.getParamOrNull(LootContextParams.ORIGIN);
    if (incomingPos == null) {
      return false; // THIS SHOULD NEVER HAPPEN
    }
    BlockPos position = new BlockPos((int) incomingPos.x, (int) incomingPos.y, (int) incomingPos.z);
    BlockEntity blockEntity = lootContext.getLevel().getBlockEntity(position);
    ILootrBlockEntity ibe = LootrAPI.resolveBlockEntity(blockEntity);
    if (ibe != null && ibe.hasLootTable()) {
      Set<UUID> actualOpeners = ibe.getActualOpeners();
      if (actualOpeners == null) {
        return false;
      }
      int count = ibe.getActualOpeners().size() + 1; // Additional opener to include the current opener
      for (Operation op : operations) {
        if (!op.test(count)) {
          return false;
        }
      }
      return true;
    }

    return false;
  }

  @Override
  public Set<LootContextParam<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootContextParams.ORIGIN);
  }

  public enum Operand implements BiPredicate<Integer, Integer>, StringRepresentable {
    EQUALS(Integer::equals, 0),
    NOT_EQUALS((a, b) -> !a.equals(b), 0),
    LESS_THAN((a, b) -> (a < b), 1),
    GREATER_THAN((a, b) -> (a > b), 1),
    LESS_THAN_EQUALS((a, b) -> (a <= b), 1),
    GREATER_THAN_EQUALS((a, b) -> (a >= b), 1);

    @SuppressWarnings("deprecation")
    public static final StringRepresentable.EnumCodec<Operand> CODEC = StringRepresentable.fromEnum(Operand::values);

    private final BiPredicate<Integer, Integer> predicate;
    private final int precedence;

    Operand(BiPredicate<Integer, Integer> predicate, int precedence) {
      this.predicate = predicate;
      this.precedence = precedence;
    }

    @Override
    public boolean test(Integer integer, Integer integer2) {
      return predicate.test(integer, integer2);
    }

    public int getPrecedence() {
      return precedence;
    }

    @Override
    public String getSerializedName() {
      return this.name().toLowerCase(Locale.ROOT);
    }
  }

  public record Operation(Operand operand, int value) implements Predicate<Integer> {

    public static final Codec<Operation> CODEC = RecordCodecBuilder.create(
        instance -> instance.group(
            Operand.CODEC.fieldOf("type").forGetter(Operation::operand),
            PrimitiveCodec.INT.fieldOf("value").forGetter(Operation::value)
        ).apply(instance, Operation::new)
    );

    public int getPrecedence() {
      return operand.getPrecedence();
    }

    @Override
    public boolean test(Integer integer) {
      return operand.test(integer, value);
    }
  }
}
