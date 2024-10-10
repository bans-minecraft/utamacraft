package net.banutama.utamacraft.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.jetbrains.annotations.NotNull;

import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.ForgeRegistries;

public class LuaConverter {
    public static <T> List<String> tagsToList(@NotNull Supplier<Stream<TagKey<T>>> tags) {
        return tags.get().map(LuaConverter::tagToString).toList();
    }

    public static <T> String tagToString(@NotNull TagKey<T> tag) {
        return tag.registry().location() + "/" + tag.location();
    }

    public static String getItemName(Item item) {
        ResourceLocation itemName = ForgeRegistries.ITEMS.getKey(item);
        return itemName == null ? "unknown" : itemName.toString();
    }

    public static Map<String, Object> getStackDetails(ItemStack stack) {
        var result = new HashMap<String, Object>();

        result.put("name", getItemName(stack.getItem()));
        result.put("count", stack.getCount());
        result.put("maxCount", stack.getMaxStackSize());
        result.put("displayName", stack.getHoverName().getString());

        if (stack.isDamageableItem()) {
            result.put("damage", stack.getDamageValue());
            result.put("maxDamage", stack.getMaxDamage());
        }

        if (stack.getItem().isBarVisible(stack)) {
            result.put("durability", stack.getItem().getBarWidth(stack) / 13.0);
        }

        result.put("tags", tagsToList(() -> stack.getTags()));

        var enchantments = getEnchantments(stack);
        if (!enchantments.isEmpty()) {
            result.put("enchantments", enchantments);
        }

        return result;
    }

    private static ArrayList<HashMap<String, Object>> getEnchantments(ItemStack stack) {
        var enchantments = new ArrayList<HashMap<String, Object>>();

        if (stack.getItem() instanceof EnchantedBookItem) {
            addEnchantments(EnchantedBookItem.getEnchantments(stack), enchantments);
        }

        if (stack.isEnchanted()) {
            addEnchantments(stack.getEnchantmentTags(), enchantments);
        }

        return enchantments;
    }

    private static void addEnchantments(ListTag enchantments, ArrayList<HashMap<String, Object>> target) {
        if (enchantments.isEmpty()) {
            return;
        }

        target.ensureCapacity(target.size() + enchantments.size());
        for (var entry : EnchantmentHelper.deserializeEnchantments(enchantments).entrySet()) {
            var info = new HashMap<String, Object>(3);
            var name = ForgeRegistries.ENCHANTMENTS.getKey(entry.getKey());
            info.put("name", name == null ? "unknown" : name.toString());
            info.put("level", entry.getValue());
            info.put("displayName", entry.getKey().getFullname(entry.getValue()).getString());
            target.add(info);
        }
    }
}
