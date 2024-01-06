package noobanidus.mods.lootr.mixin;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import noobanidus.mods.lootr.item.CrownItem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.EnumMap;
import java.util.UUID;

// TODO: When the crown gets implemented
@Mixin(ArmorItem.class)
public class MixinArmorItem {
    @Shadow @Final private static EnumMap<ArmorItem.Type, UUID> ARMOR_MODIFIER_UUID_PER_TYPE;
    @Shadow @Final @Mutable private Multimap<Attribute, AttributeModifier> defaultModifiers;
    @Shadow @Final protected float knockbackResistance;

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void constructor(ArmorMaterial material, ArmorItem.Type type, Item.Properties properties, CallbackInfo ci) {
        UUID uuid = ARMOR_MODIFIER_UUID_PER_TYPE.get(type);

        if(material == CrownItem.CrownArmourMaterial.INSTANCE) {
            ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
            this.defaultModifiers.forEach(builder::put);
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(uuid, "Armor Knockback Resistance", this.knockbackResistance, AttributeModifier.Operation.ADDITION));
            this.defaultModifiers = builder.build();
        }
    }
}
