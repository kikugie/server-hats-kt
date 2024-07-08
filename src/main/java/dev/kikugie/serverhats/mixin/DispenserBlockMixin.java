package dev.kikugie.serverhats.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import dev.kikugie.serverhats.HatsMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;

import static net.minecraft.world.level.block.DispenserBlock.DISPENSER_REGISTRY;

@Mixin(DispenserBlock.class)
public class DispenserBlockMixin {
	@Unique
	private static final DispenseItemBehavior HAT_DISPENSER_BEHAVIOR = new DefaultDispenseItemBehavior() {
		@Override
		protected @NotNull ItemStack execute(BlockSource pointer, ItemStack stack) {
			return dispenseHat(pointer, stack) ? stack : super.execute(pointer, stack);
		}
	};

	@Unique
	private static boolean dispenseHat(BlockSource pointer, ItemStack armor) {
		BlockPos blockPos = pointer.pos().relative(pointer.state().getValue(DispenserBlock.FACING));
		List<LivingEntity> list = pointer.level().getEntitiesOfClass(
				LivingEntity.class,
				new AABB(blockPos),
				EntitySelector.NO_SPECTATORS.and(new EntitySelector.MobCanWearArmorEntitySelector(Items.CREEPER_HEAD.getDefaultInstance())));
		// Use a fake item that can be equipped to a head slot. Can't use the item itself because
		// entities might do a getPreferredEquipmentSlot() call to test if that slot is empty.

		if (list.isEmpty()) return false;

		LivingEntity livingEntity = list.getFirst();
		ItemStack itemStack = armor.split(1);
		livingEntity.setItemSlot(EquipmentSlot.HEAD, itemStack);

		if (livingEntity instanceof Mob mob) {
			mob.setDropChance(EquipmentSlot.HEAD, 2.0F);
			mob.setPersistenceRequired();
		}
		return true;
	}


	@ModifyReturnValue(method = "getDispenseMethod", at = @At("RETURN"))
	protected DispenseItemBehavior allowDispenserEquipping(DispenseItemBehavior original, @Local(argsOnly = true) ItemStack stack) {
		return HatsMod.config.getDispenserEquipping() && HatsMod.isAllowed(stack) && !DISPENSER_REGISTRY.containsValue(original)
				? HAT_DISPENSER_BEHAVIOR
				: original;
	}
}
