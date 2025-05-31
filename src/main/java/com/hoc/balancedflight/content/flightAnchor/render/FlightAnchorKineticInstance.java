package com.hoc.balancedflight.content.flightAnchor.render;

import com.simibubi.create.AllPartialModels;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.hoc.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class FlightAnchorKineticInstance extends KineticBlockEntityVisual<FlightAnchorEntity> implements SimpleDynamicVisual {

    protected final EnumMap<Direction, RotatingInstance> rotatingInstances = new EnumMap<>(Direction.class);
    protected Direction sourceFacing;

    public FlightAnchorKineticInstance(VisualizationContext context, FlightAnchorEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        Direction.Axis mainAxis = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis();
        updateSourceFacing();

        for (Direction dir : Iterate.directions) {
            Direction.Axis axis = dir.getAxis();
            if (axis == Direction.Axis.Y || axis == mainAxis) continue;

            Instancer<RotatingInstance> instancer = instancerProvider()
                    .instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF, dir));

            RotatingInstance instance = instancer.createInstance();
            instance.setup(blockEntity);
            instance.setRotationAxis(Direction.get(Direction.AxisDirection.NEGATIVE, axis).step());
            instance.setRotationalSpeed(blockEntity.getSpeed());
            instance.setRotationOffset(KineticBlockEntityVisual.rotationOffset(blockState, axis, pos));
            instance.setPosition(new Vector3f(visualPos.getX(), visualPos.getY(), visualPos.getZ()));
            instance.setChanged();

            rotatingInstances.put(dir, instance);
        }
    }

    @Override
    public void beginFrame(Context context) {
        float renderTime = AnimationTickHolder.getRenderTime();
        float placedTime = blockEntity.placedRenderTime;

        if (renderTime - placedTime < 25f || renderTime - placedTime > 40f) return;

        float progress = Mth.clamp((renderTime - placedTime - 25f) / 5f, 0f, 1f);
        float scale = Mth.clampedLerp(0.01f, 1f, progress) / 2f;

        Vector3f animatedPos = new Vector3f(
                visualPos.getX(),
                visualPos.getY() + scale - 0.5f,
                visualPos.getZ()
        );

        for (Map.Entry<Direction, RotatingInstance> entry : rotatingInstances.entrySet()) {
            entry.getValue()
                    .setPosition(animatedPos)
                    .setup(blockEntity, entry.getKey().getAxis())
                    .setChanged();
        }
    }

    protected void updateSourceFacing() {
        if (blockEntity.hasSource()) {
            BlockPos offset = blockEntity.source.subtract(pos);
            sourceFacing = Direction.getNearest(offset.getX(), offset.getY(), offset.getZ());
        } else {
            sourceFacing = null;
        }
    }

    @Override
    public void update(float partialTick) {
        updateSourceFacing();
        rotatingInstances.forEach((dir, instance) ->
                instance.setup(blockEntity, dir.getAxis()).setChanged()
        );
    }

    @Override
    public void updateLight(float v) {
        int light = computePackedLight();
        rotatingInstances.values().forEach(instance -> instance.light(light));
    }

    @Override
    protected void _delete() {
        rotatingInstances.values().forEach(AbstractInstance::delete);
        rotatingInstances.clear();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        rotatingInstances.values().forEach(consumer);
    }
}