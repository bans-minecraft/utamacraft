package net.banutama.utamacraft.screen;

import java.util.Objects;

import net.banutama.utamacraft.block.custom.ModBlocks;
import net.banutama.utamacraft.block.entity.DigitizerBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

public class DigitizerMenu extends BaseAbstractContainerMenu {
    public final DigitizerBlockEntity blockEntity;
    private final ContainerLevelAccess levelAccess;

    // Client constructor
    public DigitizerMenu(int id, Inventory inventory, FriendlyByteBuf extraData) {
        this(id, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    // Server constructor
    public DigitizerMenu(int id, Inventory inventory, BlockEntity entity) {
        super(ModMenuTypes.DIGITIZER_MENU.get(), id);

        if (entity instanceof DigitizerBlockEntity digitizer) {
            checkContainerSize(inventory, 1);
            this.levelAccess = ContainerLevelAccess.create(Objects.requireNonNull(entity.getLevel()),
                    entity.getBlockPos());
            this.blockEntity = digitizer;

            addPlayerHotbar(inventory);
            addPlayerInventory(inventory);

            this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                this.addSlot(new SlotItemHandler(handler, 0, 26, 8));
            });
        } else {
            throw new IllegalArgumentException("Block entity must be a DigitizerBlockEntity");
        }
    }

    public DigitizerBlockEntity getBlockEntity() {
        return blockEntity;
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(levelAccess, player, ModBlocks.DIGITIZER.get());
    }

    @Override
    protected int getTileEntitySlotCount() {
        return 1;
    }
}
