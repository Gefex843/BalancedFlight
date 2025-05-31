package com.hoc.balancedflight.content.flightAnchor.entity;

import com.google.common.collect.Lists;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.createmod.catnip.animation.AnimationTickHolder;
import net.createmod.ponder.api.level.PonderLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BeaconBlockEntity;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;

import java.util.Arrays;
import java.util.List;

public class FlightAnchorBehaviour extends BlockEntityBehaviour {
    public static final BehaviourType<FlightAnchorBehaviour> TYPE = new BehaviourType<>();

    public FlightAnchorBehaviour(SmartBlockEntity be) {
        super(be);
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public void setActive(boolean active) {
        var level = getWorld();
        var pos = getPos();

        if (level instanceof PonderLevel)
            return;

        if (!active) {
            FlightAnchorEntity.ActiveAnchors.remove(pos);
            playSound(level, pos, SoundEvents.BEACON_DEACTIVATE);
            return;
        }

        if (!FlightAnchorEntity.ActiveAnchors.containsKey(pos)) {
            FlightAnchorEntity.ActiveAnchors.put(pos, (FlightAnchorEntity) blockEntity);
            playSound(level, pos, SoundEvents.BEACON_ACTIVATE);
        }
    }

    @Override
    public void initialize() {
        if (getWorld().isClientSide)
            ((FlightAnchorEntity) blockEntity).placedRenderTime = AnimationTickHolder.getRenderTime(getWorld());
    }

    @Override
    public void unload() {
        setActive(false);
    }

    @Override
    public void tick() {
        super.tick();

        var anchor = (FlightAnchorEntity) blockEntity;
        var previouslyActive = anchor.isActive;

        anchor.isActive = anchor.isSpeedRequirementFulfilled();

        if (anchor.isActive && !previouslyActive) {
            setActive(true);
        } else if (!anchor.isActive && previouslyActive) {
            setActive(false);
            anchor.beamSections.clear();
            anchor.checkingBeamSections.clear();
        }

        if (anchor.isActive)
            beaconTick(getWorld(), getPos(), anchor);
    }

    @Override
    public void lazyTick() {}

    public static void beaconTick(Level level, BlockPos pos, FlightAnchorEntity entity) {
        if (level instanceof PonderLevel) {
            if (entity.beamSections.isEmpty())
                entity.beamSections.add(new BeaconBlockEntity.BeaconBeamSection(DyeColor.WHITE.getTextureDiffuseColors()));
            return;
        }

        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int worldSurfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);

        BlockPos currentPos = (entity.lastCheckY < y)
                ? pos
                : new BlockPos(x, entity.lastCheckY + 1, z);

        if (entity.lastCheckY < y) {
            entity.checkingBeamSections = Lists.newArrayList();
            entity.lastCheckY = y - 1;
        }

        var lastSection = entity.checkingBeamSections.isEmpty() ? null :
                entity.checkingBeamSections.get(entity.checkingBeamSections.size() - 1);

        for (int i = 0; i < 10 && currentPos.getY() <= worldSurfaceY; i++) {
            var state = level.getBlockState(currentPos);
            var color = state.getBeaconColorMultiplier(level, currentPos, pos);

            if (color != null) {
                if (entity.checkingBeamSections.size() <= 1) {
                    lastSection = new BeaconBlockEntity.BeaconBeamSection(color);
                    entity.checkingBeamSections.add(lastSection);
                } else if (lastSection != null) {
                    if (Arrays.equals(color, lastSection.getColor())) {
                        lastSection.increaseHeight();
                    } else {
                        float[] blended = {
                                (lastSection.getColor()[0] + color[0]) / 2.0F,
                                (lastSection.getColor()[1] + color[1]) / 2.0F,
                                (lastSection.getColor()[2] + color[2]) / 2.0F
                        };
                        lastSection = new BeaconBlockEntity.BeaconBeamSection(blended);
                        entity.checkingBeamSections.add(lastSection);
                    }
                }
            } else {
                if (lastSection == null || (state.getLightBlock(level, currentPos) >= 15 && !state.is(Blocks.BEDROCK))) {
                    entity.checkingBeamSections.clear();
                    entity.lastCheckY = worldSurfaceY;
                    break;
                }
                lastSection.increaseHeight();
            }

            currentPos = currentPos.above();
            entity.lastCheckY++;
        }

        if (level.getGameTime() % 80L == 0 && !entity.beamSections.isEmpty()) {
            playSound(level, pos, SoundEvents.BEACON_AMBIENT);
        }

        if (entity.lastCheckY >= worldSurfaceY) {
            entity.lastCheckY = level.getMinBuildHeight() - 1;
            entity.beamSections = entity.checkingBeamSections;
        }
    }

    public static void playSound(Level level, BlockPos pos, SoundEvent sound) {
        level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
    }

    private static void applyEffects(Level level, BlockPos pos, int radius) {
        if (level.isClientSide)
            return;

        AABB area = new AABB(pos).inflate(radius * 10 + 10).expandTowards(0.0D, level.getHeight(), 0.0D);
        List<Player> players = level.getEntitiesOfClass(Player.class, area);

        for (Player player : players) {
        }
    }
}