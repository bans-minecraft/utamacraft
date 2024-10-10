package net.banutama.utamacraft.item;

import net.minecraft.world.item.Item;

import org.jetbrains.annotations.NotNull;

public abstract class BaseItem extends Item {
    public BaseItem(@NotNull Properties properties) {
        super(properties);
    }
}
