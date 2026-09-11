package ru.liko.warbornrenewed.registry;

import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.liko.warbornrenewed.Warbornrenewed;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Собственные материалы брони для Warborn-Renewed.
 * Полностью независимая система без зависимостей от других модов.
 * 
 * Материалы основаны на реальных стандартах:
 * - NIJ Standard 0101.06 (США)
 * - ГОСТ Р 50744-95 (Россия)
 * - VPAM (Германия)
 * 
 * Реалистичные характеристики материалов и классов защиты.
 */
public class ModArmorMaterials {

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS = DeferredRegister
            .create(Registries.ARMOR_MATERIAL, Warbornrenewed.MODID);

    /**
     * КОЖА (LEATHER) - базовый уровень
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> LEATHER = registerMaterial(
            "leather",
            createDefense(0, 0, 0, 0),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.0F,
            0.0F,
            () -> Ingredient.EMPTY);

    /**
     * КЕВЛАР (KEVLAR) - NIJ Level IIA/II
     * Мягкая баллистическая защита из арамидных волокон
     * Используется в: легких бронежилетах, полицейской броне
     * Реальные аналоги: DuPont Kevlar 29, Twaron
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> KEVLAR = registerMaterial(
            "kevlar",
            createDefense(1, 3, 5, 2),
            15,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            0.5F,
            0.0F,
            () -> Ingredient.of(ModItems.KEVLAR_REPAIR_PATCH.get()));

    /**
     * КЕРАМИКА (CERAMIC) - NIJ Level III
     * Керамические пластины (Al2O3, SiC, B4C)
     * Используется в: тактических бронеплитах, военных жилетах
     * Реальные аналоги: alumina plates, silicon carbide
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> CERAMIC = registerMaterial(
            "ceramic",
            createDefense(2, 5, 7, 3),
            12,
            SoundEvents.ARMOR_EQUIP_IRON,
            2.5F,
            0.05F,
            () -> Ingredient.of(ModItems.BALLISTIC_CERAMIC_REPAIR_PLATE.get()));

    /**
     * СТАЛЬ AR500 - NIJ Level III
     * Высокопрочная баллистическая сталь
     * Используется в: дешевых бронеплитах, тренировочных жилетах
     * Реальные аналоги: AR500, AR550 steel plates
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> AR500_STEEL = registerMaterial(
            "ar500_steel",
            createDefense(2, 4, 6, 2),
            10,
            SoundEvents.ARMOR_EQUIP_IRON,
            2.0F,
            0.1F,
            () -> Ingredient.of(ModItems.BALLISTIC_STEEL_REPAIR_PLATE.get()));

    /**
     * ПОЛИЭТИЛЕН UHMWPE - NIJ Level III/IV
     * Ультравысокомолекулярный полиэтилен
     * Используется в: современных легких плитах, элитной броне
     * Реальные аналоги: Dyneema, Spectra Shield
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> UHMWPE = registerMaterial(
            "uhmwpe",
            createDefense(3, 6, 8, 3),
            18,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            3.0F,
            0.08F,
            () -> Ingredient.of(ModItems.BALLISTIC_COMPOSITE_REPAIR_PLATE.get()));

    /**
     * КОМПОЗИТ (COMPOSITE) - NIJ Level IV
     * Многослойная композитная броня (керамика + UHMWPE + сталь)
     * Используется в: военных плитах ESAPI/XSAPI, спецназовской броне
     * Реальные аналоги: ESAPI plates, XSAPI plates
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> COMPOSITE = registerMaterial(
            "composite",
            createDefense(3, 7, 9, 4),
            15,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.0F,
            0.15F,
            () -> Ingredient.of(ModItems.BALLISTIC_COMPOSITE_REPAIR_PLATE.get()));

    /**
     * ТИТАН (TITANIUM) - NIJ Level III+
     * Титановые сплавы (Ti-6Al-4V)
     * Используется в: авиационной броне, элитных шлемах, спецназе
     * Реальные аналоги: titanium alloy plates
     */
    public static final DeferredHolder<ArmorMaterial, ArmorMaterial> TITANIUM = registerMaterial(
            "titanium",
            createDefense(4, 7, 9, 4),
            20,
            SoundEvents.ARMOR_EQUIP_NETHERITE,
            4.5F,
            0.2F,
            () -> Ingredient.EMPTY);

    public static void register(IEventBus eventBus) {
        ARMOR_MATERIALS.register(eventBus);
    }

    private static Map<ArmorItem.Type, Integer> createDefense(int boots, int leggings, int chestplate, int helmet) {
        return Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
            map.put(ArmorItem.Type.BOOTS, boots);
            map.put(ArmorItem.Type.LEGGINGS, leggings);
            map.put(ArmorItem.Type.CHESTPLATE, chestplate);
            map.put(ArmorItem.Type.HELMET, helmet);
            map.put(ArmorItem.Type.BODY, chestplate); // For animal armor etc
        });
    }

    private static DeferredHolder<ArmorMaterial, ArmorMaterial> registerMaterial(
            String name,
            Map<ArmorItem.Type, Integer> defense,
            int enchantability,
            Holder<SoundEvent> equipSound,
            float toughness,
            float knockbackResistance,
            Supplier<Ingredient> repairIngredient) {
        return ARMOR_MATERIALS.register(name, () -> new ArmorMaterial(
                defense,
                enchantability,
                equipSound,
                repairIngredient,
                List.of(new ArmorMaterial.Layer(Warbornrenewed.id(name))),
                toughness,
                knockbackResistance));
    }
}
