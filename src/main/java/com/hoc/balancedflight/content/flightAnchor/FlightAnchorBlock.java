package com.hoc.balancedflight.content.flightAnchor;

import com.simibubi.create.content.kinetics.base.HorizontalKineticBlock;
import com.simibubi.create.content.kinetics.base.IRotate;
import com.simibubi.create.foundation.block.IBE;
import com.hoc.balancedflight.BalancedFlight;
import com.hoc.balancedflight.foundation.RegistrateExtensions;
import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import lombok.experimental.ExtensionMethod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BeaconBeamBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

@ExtensionMethod(RegistrateExtensions.class)
@Mod.EventBusSubscriber(modid = BalancedFlight.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FlightAnchorBlock extends HorizontalKineticBlock implements IBE<FlightAnchorEntity>, BeaconBeamBlock, IRotate {

    public FlightAnchorBlock(Properties props) {
        super(props);
    }

    @Override
    public boolean hasShaftTowards(LevelReader world, BlockPos pos, BlockState state, Direction face) {
        Direction facing = state.getValue(HORIZONTAL_FACING);
        Direction clockwise = facing.getClockWise();
        return face == clockwise || face == clockwise.getOpposite();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction direction = context.getHorizontalDirection();
        if (context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
            direction = direction.getOpposite();
        }
        return defaultBlockState().setValue(HORIZONTAL_FACING, direction);
    }

    @NotNull
    public DyeColor getColor() {
        return DyeColor.WHITE;
    }

    @Override
    public Class<FlightAnchorEntity> getBlockEntityClass() {
        return FlightAnchorEntity.class;
    }

    @Override
    public BlockEntityType<? extends FlightAnchorEntity> getBlockEntityType() {
        return BalancedFlight.FLIGHT_ANCHOR_BLOCK_ENTITY.get();
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public Direction.Axis getRotationAxis(BlockState state) {
        return state.getValue(HORIZONTAL_FACING).getAxis();
    }

    @Override
    public IRotate.SpeedLevel getMinimumRequiredSpeedLevel() {
        return SpeedLevel.MEDIUM;
    }
}