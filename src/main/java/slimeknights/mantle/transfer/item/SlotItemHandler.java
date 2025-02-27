package slimeknights.mantle.transfer.item;

import javax.annotation.Nonnull;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class SlotItemHandler extends Slot {
  private static final Container emptyInventory = new SimpleContainer(0);
  private final IItemHandler itemHandler;
  private final int index;

  public SlotItemHandler(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
    super(emptyInventory, index, xPosition, yPosition);
    this.itemHandler = itemHandler;
    this.index = index;
  }

  @Override
  public boolean mayPlace(@Nonnull ItemStack stack) {
    if (stack.isEmpty())
      return false;
    return itemHandler.isItemValid(index, stack);
  }

  @Override
  @Nonnull
  public ItemStack getItem() {
    if (getItemHandler().getSlots() <= index) return ItemStack.EMPTY;
    return this.getItemHandler().getStackInSlot(index);
  }

  @Override
  public void set(@Nonnull ItemStack stack) {
    ((IItemHandlerModifiable) this.getItemHandler()).setStackInSlot(index, stack);
    this.setChanged();
  }

  @Override
  public void onQuickCraft(@Nonnull ItemStack oldStackIn, @Nonnull ItemStack newStackIn) {

  }

  @Override
  public int getMaxStackSize() {
    return this.itemHandler.getSlotLimit(this.index);
  }

  @Override
  public int getMaxStackSize(@Nonnull ItemStack stack) {
    ItemStack maxAdd = stack.copy();
    int maxInput = stack.getMaxStackSize();
    maxAdd.setCount(maxInput);

    IItemHandler handler = this.getItemHandler();
    ItemStack currentStack = handler.getStackInSlot(index);
    if (handler instanceof IItemHandlerModifiable) {
      IItemHandlerModifiable handlerModifiable = (IItemHandlerModifiable) handler;

      handlerModifiable.setStackInSlot(index, ItemStack.EMPTY);

      ItemStack remainder = handlerModifiable.insertItem(index, maxAdd, true);

      handlerModifiable.setStackInSlot(index, currentStack);

      return maxInput - remainder.getCount();
    } else {
      ItemStack remainder = handler.insertItem(index, maxAdd, true);

      int current = currentStack.getCount();
      int added = maxInput - remainder.getCount();
      return current + added;
    }
  }

  @Override
  public boolean mayPickup(Player playerIn) {
    return !this.getItemHandler().extractItem(index, 1, true).isEmpty();
  }

  @Override
  @Nonnull
  public ItemStack remove(int amount) {
    return this.getItemHandler().extractItem(index, amount, false);
  }

  public IItemHandler getItemHandler() {
    return itemHandler;
  }
}
