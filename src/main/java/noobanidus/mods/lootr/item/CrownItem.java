package noobanidus.mods.lootr.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import noobanidus.mods.lootr.Lootr;

import java.util.UUID;

public class CrownItem extends ArmorItem {
  private static final UUID HELM_UUID = UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B");
  private final Multimap<Attribute, AttributeModifier> modifiers;

  public CrownItem(Properties pProperties) {
    super(CrownArmorMaterial.INSTANCE, EquipmentSlot.HEAD, pProperties);
    ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
    builder.put(Attributes.ARMOR, new AttributeModifier(HELM_UUID, "Armor modifier", CrownArmorMaterial.INSTANCE.getDefenseForSlot(EquipmentSlot.HEAD), AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(HELM_UUID, "Armor toughness", CrownArmorMaterial.INSTANCE.getToughness(), AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(HELM_UUID, "Armor knockback resistance", CrownArmorMaterial.INSTANCE.getKnockbackResistance(), AttributeModifier.Operation.ADDITION));
    builder.put(Attributes.LUCK, new AttributeModifier(HELM_UUID, "Crown luck", 2, AttributeModifier.Operation.ADDITION));
    this.modifiers = builder.build();
  }

  @Override
  public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
    return true;
  }

  @Override
  public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
    return pEquipmentSlot == this.slot ? this.modifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
  }

  private static class CrownArmorMaterial implements ArmorMaterial {
    private static final CrownArmorMaterial INSTANCE = new CrownArmorMaterial();

    @Override
    public int getDurabilityForSlot(EquipmentSlot pSlot) {
      return 0;
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot pSlot) {
      return 2;
    }

    @Override
    public int getEnchantmentValue() {
      return 25;
    }

    @Override
    public SoundEvent getEquipSound() {
      return SoundEvents.ARMOR_EQUIP_GOLD;
    }

    @Override
    public Ingredient getRepairIngredient() {
      return Ingredient.EMPTY;
    }

    @Override
    public String getName() {
      return new ResourceLocation(Lootr.MODID, "crown").toString();
    }

    @Override
    public float getToughness() {
      return 1;
    }

    @Override
    public float getKnockbackResistance() {
      return 0.1f;
    }
  }
}
