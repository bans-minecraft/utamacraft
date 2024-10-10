package net.banutama.utamacraft.mixin;

import net.banutama.utamacraft.item.ModItems;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotResult;

@Mixin(Entity.class)
public class EntityMixin {
    @Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
    private void checkInvulnerabilities(DamageSource source, CallbackInfoReturnable<Boolean> cir) {
        if (((Object) this) instanceof LivingEntity living) {
            if (source.is(DamageTypeTags.IS_FIRE)) {
                ItemStack res = CuriosApi.getCuriosInventory(living).map(inventory -> {
                    return inventory
                            .findFirstCurio(ModItems.FIRE_WARD.get())
                            .map(SlotResult::stack)
                            .orElse(ItemStack.EMPTY);
                }).orElse(ItemStack.EMPTY);

                if (!res.isEmpty()) {
                    cir.setReturnValue(true);
                }
            }
        }
    }
}
