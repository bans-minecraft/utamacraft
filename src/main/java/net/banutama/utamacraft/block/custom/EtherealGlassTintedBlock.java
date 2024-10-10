package net.banutama.utamacraft.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public class EtherealGlassTintedBlock extends EtherealGlassBlock {
    @Override
    public int getLightBlock(BlockState state, BlockGetter world, BlockPos pos) {
        return world.getMaxLightLevel();
    }

    @Override
    protected String getTooltip() {
         return "tooltip.utamacraft.ethereal_glass_tinted";
    }
}
