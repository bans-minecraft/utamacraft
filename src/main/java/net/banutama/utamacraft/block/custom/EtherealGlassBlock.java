package net.banutama.utamacraft.block.custom;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EtherealGlassBlock extends GlassBlock {
    public EtherealGlassBlock() {
        super(getProperties());
    }

    private static Block.Properties getProperties() {
        return Block.Properties.copy(Blocks.GLASS)
                .isValidSpawn(EtherealGlassBlock::blockSpawning)
                .isRedstoneConductor(EtherealGlassBlock::notSolid)
                .isSuffocating(EtherealGlassBlock::notSolid)
                .isViewBlocking(EtherealGlassBlock::notSolid);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext && ((EntityCollisionContext)context).getEntity() instanceof Player) {
            return Shapes.empty();
        } else {
            return state.getShape(world, pos);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> components, TooltipFlag flag) {
        super.appendHoverText(stack, getter, components, flag);
        components.add(Component.translatable(getTooltip()).withStyle(ChatFormatting.GRAY));
    }

    protected String getTooltip() {
        return "tooltip.utamacraft.ethereal_glass";
    }

    private static Boolean blockSpawning(BlockState state, BlockGetter reader, BlockPos pos, EntityType<?> entity) {
        return false;
    }

    private static Boolean notSolid(BlockState state, BlockGetter reader, BlockPos pos) {
        return false;
    }
}
