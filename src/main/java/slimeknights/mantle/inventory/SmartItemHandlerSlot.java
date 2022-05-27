package slimeknights.mantle.inventory;

import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerForge;
import io.github.fabricators_of_create.porting_lib.transfer.item.SlotItemHandlerForge;
import net.minecraft.world.item.ItemStack;

/** Forge still uses dumb vanilla logic for determining slot limits instead of their own method */
@SuppressWarnings("UnstableApiUsage")
public class SmartItemHandlerSlot extends SlotItemHandlerForge {
	public SmartItemHandlerSlot(ItemStackHandlerForge itemHandler, int index, int xPosition, int yPosition) {
		super(itemHandler, index, xPosition, yPosition);
	}

	@Override
	public int getMaxStackSize(ItemStack stack) {
		return getItemHandler().getSlotLimit(getSlotIndex());
	}
}
