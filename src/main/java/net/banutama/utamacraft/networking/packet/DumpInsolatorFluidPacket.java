package net.banutama.utamacraft.networking.packet;

import net.banutama.utamacraft.block.entity.InsolatorBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class DumpInsolatorFluidPacket {
    private final BlockPos pos;

    public DumpInsolatorFluidPacket(BlockPos pos) {
        this.pos = pos;
    }

    public void encode(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeBlockPos(pos);
    }

    public static DumpInsolatorFluidPacket decode(FriendlyByteBuf friendlyByteBuf) {
        BlockPos pos = friendlyByteBuf.readBlockPos();
        return new DumpInsolatorFluidPacket(pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            // Find the block entity at the given position
            // If it's an InsolatorBlockEntity, call its dumpFluids() method

            ServerPlayer player = context.getSender();
            if (player == null) return;

            BlockEntity blockEntity = player.level().getBlockEntity(pos);
            if (blockEntity instanceof InsolatorBlockEntity insolator) {
                insolator.dumpFluids();
            }
        });

        return true;
    }
}
