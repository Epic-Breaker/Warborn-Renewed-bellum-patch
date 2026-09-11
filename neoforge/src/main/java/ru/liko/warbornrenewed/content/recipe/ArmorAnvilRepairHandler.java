package ru.liko.warbornrenewed.content.recipe;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.AnvilUpdateEvent;
import net.neoforged.neoforge.common.NeoForge;
import ru.liko.warbornrenewed.registry.ModArmorMaterials;
import ru.liko.warbornrenewed.registry.ModItems;

public final class ArmorAnvilRepairHandler {
    private ArmorAnvilRepairHandler() {
    }

    public static void register() {
        NeoForge.EVENT_BUS.addListener(ArmorAnvilRepairHandler::onAnvilUpdate);
    }

    private static void onAnvilUpdate(AnvilUpdateEvent event) {
        ItemStack left = event.getLeft();
        ItemStack right = event.getRight();

        if (left.isEmpty() || right.isEmpty()) {
            return;
        }

        if (!(left.getItem() instanceof ArmorItem armorItem)) {
            return;
        }

        Item repairItem = right.getItem();
        if (!isMatchingRepairMaterial(armorItem, repairItem)) {
            return;
        }

        int maxDamage = left.getMaxDamage();
        int currentDamage = left.getDamageValue();
        if (currentDamage <= 0 || currentDamage >= maxDamage) {
            return;
        }

        int repairAmount = maxDamage / 2; // Repair 50% of the max durability
        int newDamage = Math.max(0, currentDamage - repairAmount);

        ItemStack output = left.copy();
        output.setDamageValue(newDamage);

        event.setOutput(output);
        event.setCost(1);
        event.setMaterialCost(1);
    }

    private static boolean isMatchingRepairMaterial(ArmorItem armor, Item repairItem) {
        if (isArmorMaterial(armor, ModArmorMaterials.KEVLAR)) {
            return repairItem == ModItems.KEVLAR_REPAIR_PATCH.get();
        }
        if (isArmorMaterial(armor, ModArmorMaterials.AR500_STEEL)) {
            return repairItem == ModItems.BALLISTIC_STEEL_REPAIR_PLATE.get();
        }
        if (isArmorMaterial(armor, ModArmorMaterials.CERAMIC)) {
            return repairItem == ModItems.BALLISTIC_CERAMIC_REPAIR_PLATE.get();
        }
        if (isArmorMaterial(armor, ModArmorMaterials.COMPOSITE) || isArmorMaterial(armor, ModArmorMaterials.UHMWPE)) {
            return repairItem == ModItems.BALLISTIC_COMPOSITE_REPAIR_PLATE.get();
        }
        return false;
    }

    private static boolean isArmorMaterial(ArmorItem armor, net.minecraft.core.Holder<net.minecraft.world.item.ArmorMaterial> material) {
        return armor.getMaterial().value() == material.value();
    }
}
