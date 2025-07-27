package net.banutama.utamacraft.util;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.*;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;

public class SimpleFakePlayer extends FakePlayer {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static WeakReference<SimpleFakePlayer> INSTANCE;

    public SimpleFakePlayer(ServerLevel world, GameProfile profile) {
        super(world, profile);
    }

    public SimpleFakePlayer(ServerLevel world) {
        this(world, new FakeGameProfile());
    }

    public static <R> R withFakePlayer(ServerLevel world, Function<SimpleFakePlayer, R> consumer) {
        SimpleFakePlayer player = INSTANCE == null ? null : INSTANCE.get();
        if (player == null) {
            player = new SimpleFakePlayer(world);
            INSTANCE = new WeakReference<>(player);
        }

        return consumer.apply(player);
    }

    public HitResult findHit(int range, boolean skipEntity, boolean skipBlock, Predicate<Entity> entityFilter) {
        Vec3 origin = new Vec3(getX(), getY(), getZ());
        Vec3 look = getLookAngle();
        Vec3 target = new Vec3(origin.x + look.x * range, origin.y + look.y * range, origin.z + look.z * range);
        ClipContext traceContext = new ClipContext(origin, target, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE,
                this);
        Vec3 directionVec = traceContext.getFrom().subtract(traceContext.getTo());
        Direction traceDirection = Direction.getNearest(directionVec.x, directionVec.y, directionVec.z);

        HitResult blockHit = null;
        if (skipBlock) {
            Vec3 to = traceContext.getTo();
            Vec3i toi = new Vec3i((int) to.x, (int) to.y, (int) to.z);
            blockHit = BlockHitResult.miss(to, traceDirection, new BlockPos(toi));
        } else {
            blockHit = BlockGetter.traverseBlocks(traceContext.getFrom(), traceContext.getTo(), traceContext,
                    (clipContext, pos) -> {
                        if (level().isEmptyBlock(pos)) {
                            return null;
                        }

                        return new BlockHitResult(new Vec3(pos.getX(), pos.getY(), pos.getZ()), traceDirection, pos,
                                false);
                    }, clipContext -> {
                        Vec3 to = clipContext.getTo();
                        Vec3i toi = new Vec3i((int) to.x, (int) to.y, (int) to.z);
                        return BlockHitResult.miss(clipContext.getTo(), traceDirection, new BlockPos(toi));
                    });
        }

        if (skipEntity) {
            return blockHit;
        }

        List<Entity> entities = level().getEntities(
                this,
                this.getBoundingBox()
                        .expandTowards(look.x * range, look.y * range, look.z * range)
                        .inflate(1.0, 1.0, 1.0),
                EntitySelector.NO_SPECTATORS);

        LivingEntity closestEntity = null;
        Vec3 closestVec = null;
        double closestDistance = 0.0;

        for (Entity entityHit : entities) {
            if (!(entityHit instanceof LivingEntity living)) {
                continue;
            }

            if (entityFilter != null && !entityFilter.test(entityHit)) {
                continue;
            }

            AABB box = entityHit.getBoundingBox().inflate(entityHit.getPickRadius() + 0.5);
            Optional<Vec3> clipResult = box.clip(origin, target);

            if (box.contains(origin)) {
                if (closestDistance >= 0.0) {
                    closestEntity = living;
                    closestVec = clipResult.orElse(origin);
                    closestDistance = 0.0;
                }
            } else if (clipResult.isPresent()) {
                Vec3 clipVec = clipResult.get();
                double distance = origin.distanceTo(clipVec);

                if (distance < closestDistance || closestDistance == 0.0) {
                    if (entityHit == entityHit.getRootVehicle()) {
                        if (closestDistance == 0.0) {
                            closestEntity = living;
                            closestVec = clipVec;
                        }
                    } else {
                        closestEntity = living;
                        closestVec = clipVec;
                        closestDistance = distance;
                    }
                }
            }
        }

        if (closestEntity != null &&
                closestDistance <= range &&
                (blockHit.getType() == HitResult.Type.MISS ||
                        distanceToSqr(blockHit.getLocation()) > closestDistance * closestDistance)) {
            return new EntityHitResult(closestEntity, closestVec);
        } else {
            return blockHit;
        }
    }

    public InteractionResult use(int range, boolean skipEntity, boolean skipBlock, Predicate<Entity> entityFilter) {
        HitResult hit = findHit(range, skipEntity, skipBlock, entityFilter);
        if (hit instanceof BlockHitResult blockHit) {
            InteractionResult res = gameMode.useItemOn(this, level(), getMainHandItem(), InteractionHand.MAIN_HAND,
                    blockHit);
            if (res.consumesAction()) {
                return res;
            }

            return gameMode.useItem(this, level(), getMainHandItem(), InteractionHand.MAIN_HAND);
        } else if (hit instanceof EntityHitResult) {
            // TODO: Interact with an entity?
            return InteractionResult.FAIL;
        } else {
            return InteractionResult.FAIL;
        }
    }

    public MerchantOffers getTrades(int range) {
        HitResult hit = findHit(range, false, true, entity -> entity instanceof Villager);
        if (hit instanceof EntityHitResult entityHit) {
            if (entityHit.getEntity() instanceof Villager villager) {
                return villager.getOffers();
            } else {
                LOGGER.info("Entity hit is not a Villager: {}", entityHit.getEntity().getType());
            }
        } else {
            LOGGER.info("Hit result is not an EntityHitResult: {}", hit.getType());
        }

        return null;
    }

    public Entity getTargetedEntity(int range) {
        HitResult hit = findHit(range, false, true, entity -> entity instanceof LivingEntity);
        if (hit instanceof EntityHitResult entityHit) {
            return entityHit.getEntity();
        } else {
            LOGGER.info("Hit result is not an EntityHitResult: {}", hit.getType());
            return null;
        }
    }

    public ItemStack makeTrade(int range, int tradeIndex, ItemStack offerA, ItemStack offerB) {
        HitResult hit = findHit(range, false, true, entity -> entity instanceof Villager);
        if (!(hit instanceof EntityHitResult entityHit)) {
            LOGGER.info("Hit result is not an EntityHitResult: {}", hit.getType());
            return null;
        }

        if (!(entityHit.getEntity() instanceof Villager villager)) {
            LOGGER.info("Entity hit is not a Villager: {}", entityHit.getEntity().getType());
            return null;
        }

        var offers = villager.getOffers();
        if (offers == null || offers.isEmpty()) {
            LOGGER.info("No offers available for the Villager");
            return null;
        }

        if (tradeIndex < 0 || tradeIndex >= offers.size()) {
            LOGGER.info("Trade index {} is out of bounds for the Villager's offers", tradeIndex);
            return null;
        }

        var offer = offers.get(tradeIndex);
        if (!offer.take(offerA, offerB)) {
            LOGGER.info("Offer {} does not match the provided items: {} and {}", tradeIndex, offerA, offerB);
            return null;
        }

        var result = offer.assemble();
        villager.notifyTrade(offer);
        return result;
    }

    //
    // Overrides to compensate for FakePlayer
    //

    @Override
    public boolean canBeAffected(@NotNull MobEffectInstance effect) {
        return false;
    }

    @Override
    public boolean canHarmPlayer(Player player) {
        return true;
    }

    @Override
    public boolean startRiding(@NotNull Entity entity, boolean b) {
        return false;
    }

    @Override
    public @NotNull OptionalInt openMenu(@Nullable MenuProvider provider) {
        return OptionalInt.empty();
    }

    @Override
    public float getStandingEyeHeight(@NotNull Pose pose, @NotNull EntityDimensions dimensions) {
        return 0.0f;
    }

    @Override
    public double getEyeY() {
        return getY() + 0.2;
    }

    @Override
    public float getAttackStrengthScale(float f) {
        return 1.0f;
    }
}
