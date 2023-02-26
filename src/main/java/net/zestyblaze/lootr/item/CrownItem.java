package net.zestyblaze.lootr.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.UUID;

public class CrownItem extends ArmorItem {
    private static final UUID HELM_UUID = UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B");
    private final Multimap<Attribute, AttributeModifier> modifiers;

    public CrownItem(Properties properties) {
        super(CrownArmourMaterial.INSTANCE, EquipmentSlot.HEAD, properties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ARMOR, new AttributeModifier(HELM_UUID, "Armour modifier", CrownArmourMaterial.INSTANCE.getDefenseForSlot(EquipmentSlot.HEAD), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(HELM_UUID, "Armour toughness", CrownArmourMaterial.INSTANCE.getToughness(), AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.LUCK, new AttributeModifier(HELM_UUID, "Crown luck", 2, AttributeModifier.Operation.ADDITION));
        this.modifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return pEquipmentSlot == this.slot ? this.modifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }
    
    public static class CrownArmourMaterial implements ArmorMaterial {
        public static final CrownArmourMaterial INSTANCE = new CrownArmourMaterial();

        @Override
        public int getDurabilityForSlot(EquipmentSlot slot) {
            return 0;
        }

        @Override
        public int getDefenseForSlot(EquipmentSlot slot) {
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
            return "crown";
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
