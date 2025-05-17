package com.vice.balancedflight.content.flightAnchor.render;

//import com.jozufozu.flywheel.api.InstanceData;
//import com.jozufozu.flywheel.api.Instancer;
//import com.jozufozu.flywheel.api.Material;
//import com.jozufozu.flywheel.api.MaterialManager;
//import com.jozufozu.flywheel.api.instance.DynamicInstance;
//import com.jozufozu.flywheel.util.AnimationTickHolder;
import com.simibubi.create.AllPartialModels;
//import com.simibubi.create.content.kinetics.base.KineticBlockEntityInstance;
import com.simibubi.create.content.kinetics.base.KineticBlockEntityVisual;
//import com.simibubi.create.content.kinetics.base.flwdata.RotatingData;
//import com.simibubi.create.foundation.utility.Iterate;
import com.simibubi.create.content.kinetics.base.RotatingInstance;
import com.simibubi.create.foundation.render.AllInstanceTypes;
import com.vice.balancedflight.content.flightAnchor.entity.FlightAnchorEntity;
import dev.engine_room.flywheel.api.instance.Instance;
import dev.engine_room.flywheel.api.instance.Instancer;
import dev.engine_room.flywheel.api.visualization.VisualizationContext;
import dev.engine_room.flywheel.lib.instance.AbstractInstance;
import dev.engine_room.flywheel.lib.instance.InstanceTypes;
import dev.engine_room.flywheel.lib.model.Models;
import dev.engine_room.flywheel.lib.visual.SimpleDynamicVisual;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.catnip.data.Iterate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class FlightAnchorKineticInstance extends KineticBlockEntityVisual<FlightAnchorEntity> implements SimpleDynamicVisual
{
    protected final EnumMap<Direction, RotatingInstance> keys = new EnumMap(Direction.class);
    protected Direction sourceFacing;

    public FlightAnchorKineticInstance(VisualizationContext context, FlightAnchorEntity blockEntity, float partialTick) {
        super(context, blockEntity, partialTick);

        Direction.Axis boxAxis = this.blockState.getValue(BlockStateProperties.HORIZONTAL_FACING).getAxis();
        this.updateSourceFacing();

        for (Direction direction : Iterate.directions)
        {
            Direction.Axis axis = direction.getAxis();
            if (boxAxis != axis && axis != Direction.Axis.Y)
            {
                Instancer<RotatingInstance> shaft = instancerProvider().instancer(AllInstanceTypes.ROTATING, Models.partial(AllPartialModels.SHAFT_HALF, direction));
                RotatingInstance key = shaft.createInstance();
                key.setup(blockEntity)
                        .setRotationAxis(Direction.get(Direction.AxisDirection.NEGATIVE, axis).step())
                        .setRotationalSpeed(blockEntity.getSpeed())
                        .setRotationOffset(KineticBlockEntityVisual.rotationOffset(blockState, axis, pos))
                        .setPosition(new Vector3f(visualPos.getX(), visualPos.getY(), visualPos.getZ()))
                        .setChanged();

                this.keys.put(direction, key);
            }
        }
    }

    @Override
    public void beginFrame(Context context)
    {
        var time = AnimationTickHolder.getRenderTime();

        var placedTime = blockEntity.placedRenderTime;
        if (time - placedTime > 40f || time - placedTime < 25f)
            return;

        placedTime = placedTime + 25f;

        var pos = getVisualPosition();
        var scale = Mth.clampedLerp(0.01F, 1F, Mth.clamp(time - placedTime, 0f, 5f) / 5f) / 2;
        var posF = new Vector3f(pos.getX(), pos.getY() + scale - 0.5f, pos.getZ());

        for (Map.Entry<Direction, RotatingInstance> kvp : this.keys.entrySet())
        {
            kvp.getValue().setPosition(posF);
            kvp.getValue().setup(blockEntity, kvp.getKey().getAxis());
            kvp.getValue().setChanged();
        }
    }

    protected void updateSourceFacing() {
        if (this.blockEntity.hasSource()) {
            BlockPos source = this.blockEntity.source.subtract(this.pos);
            this.sourceFacing = Direction.getNearest((float)source.getX(), (float)source.getY(), (float)source.getZ());
        }
        else {
            this.sourceFacing = null;
        }
    }

    @Override
    public void update(float partialTick) {
        this.updateSourceFacing();
        for (Map.Entry<Direction, RotatingInstance> kvp : this.keys.entrySet())
        {
            kvp.getValue().setup(blockEntity, kvp.getKey().getAxis()).setChanged();
        }
    }

    @Override
    public void updateLight(float v) {
        this.keys.values().forEach(it -> it.light(computePackedLight()));
    }

    @Override
    protected void _delete() {
        this.keys.values().forEach(AbstractInstance::delete);
        this.keys.clear();
    }

    @Override
    public void collectCrumblingInstances(Consumer<@Nullable Instance> consumer) {
        this.keys.values().forEach(consumer);
    }
}