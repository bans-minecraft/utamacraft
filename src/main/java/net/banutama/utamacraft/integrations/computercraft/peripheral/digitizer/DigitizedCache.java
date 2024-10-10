package net.banutama.utamacraft.integrations.computercraft.peripheral.digitizer;

import java.util.HashMap;
import java.util.UUID;
import java.util.function.Consumer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.banutama.utamacraft.Utamacraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

public class DigitizedCache extends SavedData {
    private final HashMap<UUID, DigitizedItem> cache = new HashMap<>();

    private static DigitizedCache instance;
    private static final Logger LOGGER = LogUtils.getLogger();

    @NotNull
    public static DigitizedCache getInstance(Level level) {
        if (!(level instanceof ServerLevel serverLevel)) {
            throw new RuntimeException("Cannot get DigitizedCache for non-server level");
        }

        if (instance != null) {
            return instance;
        }

        var storage = serverLevel.getServer().overworld().getDataStorage();
        instance = storage.computeIfAbsent(DigitizedCache::load, DigitizedCache::create,
                String.format("%s_DigitizedCache", Utamacraft.MOD_ID));
        instance.collect(serverLevel);

        return instance;
    }

    @NotNull
    public static DigitizedCache create() {
        return new DigitizedCache();
    }

    public void put(@NotNull DigitizedItem item) {
        cache.put(item.id, item);
        setDirty();
    }

    public DigitizedItem get(UUID id) {
        return cache.get(id);
    }

    public DigitizedItem take(UUID id) {
        var item = cache.remove(id);

        if (item != null) {
            setDirty();
        }

        return item;
    }

    public void forEach(Consumer<DigitizedItem> consumer) {
        cache.values().forEach(consumer::accept);
    }

    @NotNull
    public static DigitizedCache load(CompoundTag tag) {
        DigitizedCache cache = new DigitizedCache();
        if (tag.contains("items") && tag.get("items") instanceof ListTag) {
            ((ListTag) tag.get("items")).forEach(item -> {
                var digitizedItem = new DigitizedItem((CompoundTag) item);
                cache.cache.put(digitizedItem.id, digitizedItem);
            });
        }

        return cache;
    }

    @Override
    @NotNull
    public CompoundTag save(CompoundTag tag) {
        ListTag items = new ListTag();
        cache.values().forEach(digitizedItem -> {
            CompoundTag item = new CompoundTag();
            digitizedItem.serialize(item);
            items.add(item);
        });

        tag.put("items", items);
        return tag;
    }

    public void collect(Level level) {
        var gameTime = level.getGameTime();
        var count = cache.size();
        var iterator = cache.entrySet().iterator();

        iterator.forEachRemaining(item -> {
            if (item.getValue().isExpired(gameTime)) {
                iterator.remove();
            }
        });

        var remaining = cache.size();
        if (count != remaining) {
            LOGGER.info(
                    String.format("Collected %d expired digitized items, %d remaining", count - remaining, remaining));
        }

        setDirty();
    }
}
