package net.banutama.utamacraft.screen;

import org.jetbrains.annotations.NotNull;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public abstract class BaseAbstractContainerMenu extends AbstractContainerMenu {
    private static final int HOTBAR_SLOT_COUNT = 9;

    private static final int PLAYER_INVENTORY_ROW_COUNT = 3;
    private static final int PLAYER_INVENTORY_COLUMN_COUNT = 9;
    private static final int PLAYER_INVENTORY_SLOT_COUNT = PLAYER_INVENTORY_ROW_COUNT * PLAYER_INVENTORY_COLUMN_COUNT;

    private static final int VANILLA_SLOT_COUNT = HOTBAR_SLOT_COUNT + PLAYER_INVENTORY_SLOT_COUNT;
    private static final int TILE_ENTITY_START_SLOT = VANILLA_SLOT_COUNT;

    protected BaseAbstractContainerMenu(MenuType<?> type, int id) {
        super(type, id);
    }

    protected abstract int getTileEntitySlotCount();

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot source = slots.get(index);
        if (!source.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack sourceStack = source.getItem();
        ItemStack sourceCopy = sourceStack.copy();

        if (index < VANILLA_SLOT_COUNT) {
            // This is a vanilla container slot, so merge the stack into the tile inventory.
            if (!moveItemStackTo(sourceStack, TILE_ENTITY_START_SLOT, TILE_ENTITY_START_SLOT + getTileEntitySlotCount(),
                    false)) {
                return ItemStack.EMPTY;
            }
        } else if (index < TILE_ENTITY_START_SLOT + getTileEntitySlotCount()) {
            // This is a tile-entity slot, so merge the stack into the players inventory.
            if (!moveItemStackTo(sourceStack, 0, VANILLA_SLOT_COUNT, false)) {
                return ItemStack.EMPTY;
            }
        } else {
            return ItemStack.EMPTY;
        }

        // If the stack size is zero, the entire stack was moved, so set the slot
        // contents to null.
        if (sourceStack.getCount() == 0) {
            source.set(ItemStack.EMPTY);
        } else {
            source.setChanged();
        }

        source.onTake(player, sourceStack);
        return sourceCopy;
    }

    protected void addPlayerInventory(Inventory inventory) {
        for (int row = 0; row < PLAYER_INVENTORY_ROW_COUNT; ++row) {
            for (int col = 0; col < PLAYER_INVENTORY_COLUMN_COUNT; ++col) {
                this.addSlot(new Slot(inventory, col + row * 9 + 9, 8 + col * 18, 82 + row * 18));
            }
        }
    }

    protected void addPlayerHotbar(Inventory inventory) {
        for (int slot = 0; slot < HOTBAR_SLOT_COUNT; ++slot) {
            this.addSlot(new Slot(inventory, slot, 8 + slot * 18, 140));
        }
    }
}
