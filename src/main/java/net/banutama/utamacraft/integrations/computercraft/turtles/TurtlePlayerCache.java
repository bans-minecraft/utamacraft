package net.banutama.utamacraft.integrations.computercraft.turtles;

import com.mojang.authlib.GameProfile;
import dan200.computercraft.api.turtle.ITurtleAccess;
import net.banutama.utamacraft.util.FakeGameProfile;
import net.banutama.utamacraft.util.SimpleFakePlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.server.level.ServerLevel;

import java.util.WeakHashMap;
import java.util.function.Function;

public class TurtlePlayerCache {
    private static final WeakHashMap<ITurtleAccess, SimpleFakePlayer> PLAYERS = new WeakHashMap<>();

    private static SimpleFakePlayer getPlayerFor(ITurtleAccess turtle, GameProfile profile) {
        SimpleFakePlayer player = PLAYERS.get(turtle);
        if (player == null) {
            player = new SimpleFakePlayer((ServerLevel) turtle.getLevel(), profile);
            PLAYERS.put(turtle, player);
        }

        return player;
    }

    public static <T> T withPlayer(ITurtleAccess turtle, Function<SimpleFakePlayer, T> callback) {
        GameProfile profile = turtle.getOwningPlayer();
        if (profile == null) {
            profile = new FakeGameProfile();
        }

        SimpleFakePlayer player = getPlayerFor(turtle, profile);
        BlockPos position = turtle.getPosition();
        Direction direction = turtle.getDirection();

        float pitch = direction == Direction.UP ? -90.0f : direction == Direction.DOWN ? 90.0f : 0.0f;
        float yaw = direction == Direction.SOUTH ? 0.0f
                : direction == Direction.WEST ? 90.0f : direction == Direction.NORTH ? 180.0f : -90.0f;

        Vec3i normal = direction.getNormal();
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axis_dir = direction.getAxisDirection();

        double x = (axis == Direction.Axis.X && axis_dir == Direction.AxisDirection.NEGATIVE) ? -0.5
                : (0.5 + normal.getX() / 1.9);
        double y = 0.5 + normal.getY() / 1.9;
        double z = (axis == Direction.Axis.Z && axis_dir == Direction.AxisDirection.NEGATIVE) ? -0.5
                : (0.5 + normal.getZ() / 1.9);

        player.moveTo(position.getX() + x, position.getY() + y, position.getZ() + z, yaw, pitch);
        return callback.apply(player);
    }
}
