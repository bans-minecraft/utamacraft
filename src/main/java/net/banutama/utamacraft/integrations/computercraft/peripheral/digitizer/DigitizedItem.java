package net.banutama.utamacraft.integrations.computercraft.peripheral.digitizer;

import java.util.Map;
import java.util.UUID;

import net.banutama.utamacraft.util.LuaConverter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class DigitizedItem {
    // Lifetime of a digitized item (in ticks): 20 TPS, 60 minutes, 20
    // minutes-per-day, 5 days
    public final static long LIFETIME = 20 * 60 * 20 * 5;

    public UUID id;
    public ItemStack itemStack;
    public long createdAt;
    public long expiresAt;

    public DigitizedItem(ItemStack itemStack, long gameTime) {
        this.id = UUID.randomUUID();
        this.itemStack = itemStack;
        this.createdAt = gameTime;
        this.expiresAt = gameTime + LIFETIME;
    }

    public DigitizedItem(CompoundTag tag) {
        id = tag.getUUID("id");
        itemStack = ItemStack.of((CompoundTag) tag.get("itemStack"));
        createdAt = tag.getLong("createdAt");
        expiresAt = tag.getLong("expiresAt");
    }

    public void serialize(CompoundTag tag) {
        tag.putUUID("id", id);
        tag.put("itemStack", itemStack.serializeNBT());
        tag.putLong("createdAt", createdAt);
        tag.putLong("expiresAt", expiresAt);
    }

    public long age(long gameTime) {
        return gameTime - createdAt;
    }

    public long remaining(long gameTime) {
        return Math.max(0, expiresAt - gameTime);
    }

    public boolean isExpired(long gameTime) {
        return expiresAt <= gameTime;
    }

    public void refresh(long gameTime) {
        expiresAt = gameTime + LIFETIME;
    }

    public Map<String, Object> describeItem(long gameTime) {
        var map = LuaConverter.getStackDetails(itemStack);
        map.put("id", id.toString());
        map.put("createdAt", createdAt);
        map.put("expiresAt", expiresAt);
        map.put("age", age(gameTime));
        map.put("isExpired", isExpired(gameTime));
        map.put("remainingTime", remaining(gameTime));
        return map;
    }
}
