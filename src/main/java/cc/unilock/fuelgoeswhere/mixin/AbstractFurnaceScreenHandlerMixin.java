package cc.unilock.fuelgoeswhere.mixin;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AbstractFurnaceScreenHandler;
import net.minecraft.screen.AbstractRecipeScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value = AbstractFurnaceScreenHandler.class, priority = 9999)
public abstract class AbstractFurnaceScreenHandlerMixin extends AbstractRecipeScreenHandler<Inventory> {
	public AbstractFurnaceScreenHandlerMixin(ScreenHandlerType<?> screenHandlerType, int i) {
		super(screenHandlerType, i);
	}

	@Shadow
	protected abstract boolean isFuel(ItemStack itemStack);

	@Shadow
	protected abstract boolean isSmeltable(ItemStack itemStack);

	@Inject(method = "quickMove", at = @At(value = "INVOKE", target = "Lnet/minecraft/screen/AbstractFurnaceScreenHandler;isSmeltable(Lnet/minecraft/item/ItemStack;)Z"), cancellable = true, locals = LocalCapture.CAPTURE_FAILHARD)
	private void fuelgoeswhere$swap(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir, ItemStack itemStack, Slot slot2, ItemStack itemStack2) {
		if (this.isFuel(itemStack2)) {
			if (this.isSmeltable(itemStack2)) {
				if (!this.insertItem(itemStack2, 0, 2, true)) {
					cir.setReturnValue(ItemStack.EMPTY);
					return;
				}
			} else if (!this.insertItem(itemStack2, 1, 2, false)) {
				cir.setReturnValue(ItemStack.EMPTY);
				return;
			}
		} else if (this.isSmeltable(itemStack2)) {
			if (!this.insertItem(itemStack2, 0, 1, false)) {
				cir.setReturnValue(ItemStack.EMPTY);
				return;
			}
		}

		// ↓ vanilla code ↓

		if (itemStack2.isEmpty()) {
			slot2.setStack(ItemStack.EMPTY);
		} else {
			slot2.markDirty();
		}

		if (itemStack2.getCount() == itemStack.getCount()) {
			cir.setReturnValue(ItemStack.EMPTY);
			return;
		}

		slot2.onTakeItem(player, itemStack2);

		cir.setReturnValue(itemStack);
	}
}
