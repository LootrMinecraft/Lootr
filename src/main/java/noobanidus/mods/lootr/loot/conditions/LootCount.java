package noobanidus.mods.lootr.loot.conditions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;
import net.minecraft.loot.*;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import noobanidus.mods.lootr.api.tile.ILootTile;
import noobanidus.mods.lootr.init.ModLoot;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class LootCount implements ILootCondition {
  private final List<Operation> operations;

  public LootCount(List<Operation> operations) {
    this.operations = operations;
  }

  @Override
  public LootConditionType getType() {
    return ModLoot.LOOT_COUNT;
  }

  @Override
  public boolean test(LootContext lootContext) {
    Vector3d pos = lootContext.getParamOrNull(LootParameters.ORIGIN);
    if (pos == null) {
      return false; // THIS SHOULD NEVER HAPPEN
    }
    BlockPos position = new BlockPos(pos);
    TileEntity tileentity = lootContext.getLevel().getBlockEntity(position);
    if (tileentity instanceof ILootTile) {
      int count = ((ILootTile) tileentity).getOpeners().size() + 1; // Additional opener to include the current opener
      for (Operation op : operations) {
        if (!op.test(count)) {
          return false;
        }
      }
    }
    return true;
  }

  @Override
  public Set<LootParameter<?>> getReferencedContextParams() {
    return ImmutableSet.of(LootParameters.ORIGIN);
  }

  public static class Serializer implements ILootSerializer<LootCount> {
    @Override
    public void serialize(JsonObject object, LootCount count, JsonSerializationContext context) {
      JsonArray operations = new JsonArray();
      for (Operation op : count.operations) {
        operations.add(op.serialize());
      }
      object.add("operations", operations);
    }

    @Override
    public LootCount deserialize(JsonObject object, JsonDeserializationContext context) {
      JsonArray objects = object.get("operations").getAsJsonArray();
      List<Operation> operations = new ArrayList<>();
      for (JsonElement element : objects) {
        if (!element.isJsonObject()) {
          throw new IllegalArgumentException("invalid operand for LootCount: " + element.toString());
        }
        operations.add(Operation.deserialize(element.getAsJsonObject()));
      }
      operations.sort(Comparator.comparingInt(Operation::getPrecedence));
      return new LootCount(operations);
    }
  }

  public enum Operand implements BiPredicate<Integer, Integer> {
    EQUALS(Integer::equals, 0),
    NOT_EQUALS((a, b) -> !a.equals(b), 0),
    LESS_THAN((a, b) -> (a < b), 1),
    GREATER_THAN((a, b) -> (a > b), 1),
    LESS_THAN_EQUALS((a, b) -> (a <= b), 1),
    GREATER_THAN_EQUALS((a, b) -> (a >= b), 1);

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

    @Nullable
    public static Operand fromString(String name) {
      name = name.toUpperCase(Locale.ROOT);
      for (Operand o : values()) {
        if (name.equals(o.name())) {
          return o;
        }
      }

      return null;
    }
  }

  public static class Operation implements Predicate<Integer> {
    private final Operand operand;
    private final int value;

    public Operation(Operand operand, int value) {
      this.operand = operand;
      this.value = value;
    }

    public int getPrecedence() {
      return operand.getPrecedence();
    }

    @Override
    public boolean test(Integer integer) {
      return operand.test(integer, value);
    }

    public JsonObject serialize() {
      JsonObject result = new JsonObject();
      result.addProperty("type", operand.name().toLowerCase(Locale.ROOT));
      result.addProperty("value", value);
      return result;
    }

    public static Operation deserialize(JsonObject object) {
      String operand = object.get("type").getAsString();
      Operand op = Operand.fromString(operand);
      if (op == null) {
        throw new IllegalArgumentException("invalid operand for operation: " + operand);
      }
      return new Operation(op, object.get("value").getAsInt());
    }
  }
}
