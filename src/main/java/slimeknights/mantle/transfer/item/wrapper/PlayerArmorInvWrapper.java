package slimeknights.mantle.transfer.item.wrapper;

import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class PlayerArmorInvWrapper extends RangedWrapper {
	private final Inventory inventoryPlayer;

	public PlayerArmorInvWrapper(Inventory inv) {
		super(new InvWrapper(inv), inv.items.size(), inv.items.size() + inv.armor.size());
		inventoryPlayer = inv;
	}

	@Override
	@Nonnull
	public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
		EquipmentSlot equ = null;
		for (EquipmentSlot s : EquipmentSlot.values()) {
			if (s.getType() == EquipmentSlot.Type.ARMOR && s.getIndex() == slot) {
				equ = s;
				break;
			}
		}
		// check if it's valid for the armor slot
		if (equ != null && slot < 4 && !stack.isEmpty() && Mob.getEquipmentSlotForItem(stack) == equ) {
			return super.insertItem(slot, stack, simulate);
		}
		return stack;
	}

	public Inventory getInventoryPlayer() {
		return inventoryPlayer;
	}
}
