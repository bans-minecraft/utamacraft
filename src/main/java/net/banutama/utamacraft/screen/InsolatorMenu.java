package net.banutama.utamacraft.screen;

import net.banutama.utamacraft.block.custom.ModBlocks;
import net.banutama.utamacraft.block.entity.InsolatorBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InsolatorMenu extends BaseAbstractContainerMenu {
    public final InsolatorBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    // Client Constructor
    public InsolatorMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(id, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    // Server Constructor
    public InsolatorMenu(int id, Inventory inventory, BlockEntity entity) {
        super(ModMenuTypes.INSOLATOR_MENU.get(), id);

        if (entity instanceof InsolatorBlockEntity insolator) {
            checkContainerSize(inventory, 3);
            this.levelAccess = ContainerLevelAccess.create(Objects.requireNonNull(entity.getLevel()),
                    entity.getBlockPos());
            this.blockEntity = insolator;

            addPlayerHotbar(inventory);
            addPlayerInventory(inventory);

            this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                this.addSlot(new SlotItemHandler(handler, 0, 78, 10));
                this.addSlot(new SlotItemHandler(handler, 1, 152, 10));
                this.addSlot(new SlotItemHandler(handler, 2, 152, 55));
            });
        } else {
            throw new IllegalArgumentException("Block entity must be an InsolatorBlockEntity");
        }
    }

    public InsolatorBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(levelAccess, player, ModBlocks.INSOLATOR.get());
    }

    @Override
    protected int getTileEntitySlotCount() {
        return 3;
    }
}
