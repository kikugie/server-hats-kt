package dev.kikugie.serverhats.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.serverhats.HatsMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Mob.class)
public class MobEntityMixin {
	@ModifyExpressionValue(method = "equipItemIfPossible", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Mob;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"))
	private EquipmentSlot allowMobsToEquipHats(EquipmentSlot original, @Local(argsOnly = true) ItemStack stack) {
		return HatsMod.config.getMobsCanEquipHats() && HatsMod.isAllowed(stack)
				? EquipmentSlot.HEAD
				: original;
	}
}
