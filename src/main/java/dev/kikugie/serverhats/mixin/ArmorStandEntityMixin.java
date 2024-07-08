package dev.kikugie.serverhats.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.serverhats.HatsMod;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ArmorStand.class)
public abstract class ArmorStandEntityMixin {
	@Shadow
	public abstract boolean isSmall();

	@ModifyExpressionValue(method = "interactAt", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/decoration/ArmorStand;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"))
	private EquipmentSlot equipHatsToArmorStands(EquipmentSlot original, @Local(argsOnly = true) Player player, @Local(argsOnly = true) Vec3 vec3, @Local(argsOnly = true) InteractionHand hand) {
		return HatsMod.isAllowed(player.getItemInHand(hand)) && isLookingAtAHead(vec3)
				? EquipmentSlot.HEAD
				: original;
	}

	@Unique
	private boolean isLookingAtAHead(Vec3 hitPos) {
		var y = isSmall() ? hitPos.y * 2 : hitPos.y;
		return y > 1.6;
	}
}
