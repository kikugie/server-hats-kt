package dev.kikugie.serverhats.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.serverhats.HatsMod;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(InventoryMenu.class)
public abstract class PlayerScreenHandlerMixin extends AbstractContainerMenu {
	protected PlayerScreenHandlerMixin(@Nullable MenuType<?> menuType, int i) {
		super(menuType, i);
	}

	@ModifyExpressionValue(method = "quickMoveStack", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getEquipmentSlotForItem(Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/entity/EquipmentSlot;"))
	private EquipmentSlot allowQuickTransferEquip(EquipmentSlot original, @Local(argsOnly = true) Player player, @Local(argsOnly = true) int i) {
		return HatsMod.config.getShiftClickEquipping() && HatsMod.isAllowed(slots.get(i).getItem())
				? EquipmentSlot.HEAD
				: original;
	}
}
