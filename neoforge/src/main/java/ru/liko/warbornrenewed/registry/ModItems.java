package ru.liko.warbornrenewed.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.ChatFormatting;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.liko.warbornrenewed.Warbornrenewed;
import ru.liko.warbornrenewed.content.item.BinocularItem;
import ru.liko.warbornrenewed.content.item.RebBackpackItem;
import ru.liko.warbornrenewed.packs.CustomPackArmorItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Warbornrenewed.MODID);
    private static final List<DeferredItem<? extends Item>> ARMOR_PIECES = new ArrayList<>();
    private static final List<DeferredItem<? extends Item>> ARMOR_PARTS = new ArrayList<>();
    private static final List<DeferredItem<RebBackpackItem>> REB_BACKPACKS = new ArrayList<>();

    // ==================== Equipment Items ====================
    public static final DeferredItem<BinocularItem> BINOCULAR = ITEMS.register("binocular",
            () -> new BinocularItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.UNCOMMON)));

    public static final DeferredItem<Item> BALLISTIC_STEEL_REPAIR_PLATE = ITEMS.register(
            "ballistic_steel_repair_plate",
                () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                        tooltip.add(Component.translatable("tooltip.warbornrenewed.ballistic_steel_repair_plate.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                });

    public static final DeferredItem<Item> KEVLAR_REPAIR_PATCH = ITEMS.register(
            "kevlar_repair_patch",
                () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)) {
                        @Override
                        public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                        tooltip.add(Component.translatable("tooltip.warbornrenewed.kevlar_repair_patch.tooltip").withStyle(ChatFormatting.GRAY));
                        }
                });

    public static final DeferredItem<Item> BALLISTIC_CERAMIC_REPAIR_PLATE = ITEMS.register(
            "ballistic_ceramic_repair_plate",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.COMMON)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    tooltip.add(Component.translatable("tooltip.warbornrenewed.ballistic_ceramic_repair_plate.tooltip").withStyle(ChatFormatting.GRAY));
                }
            });

    public static final DeferredItem<Item> BALLISTIC_COMPOSITE_REPAIR_PLATE = ITEMS.register(
            "ballistic_composite_repair_plate",
            () -> new Item(new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)) {
                @Override
                public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
                    tooltip.add(Component.translatable("tooltip.warbornrenewed.ballistic_composite_repair_plate.tooltip").withStyle(ChatFormatting.GRAY));
                }
            });

    // ==================== Pack Armor Items (base items for custom packs) ====================
    public static final DeferredItem<CustomPackArmorItem> PACK_HELMET = ITEMS.register("pack_helmet",
            () -> new CustomPackArmorItem(ModArmorMaterials.KEVLAR, ArmorItem.Type.HELMET,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredItem<CustomPackArmorItem> PACK_CHESTPLATE = ITEMS.register("pack_chestplate",
            () -> new CustomPackArmorItem(ModArmorMaterials.KEVLAR, ArmorItem.Type.CHESTPLATE,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredItem<CustomPackArmorItem> PACK_LEGGINGS = ITEMS.register("pack_leggings",
            () -> new CustomPackArmorItem(ModArmorMaterials.KEVLAR, ArmorItem.Type.LEGGINGS,
                    new Item.Properties().stacksTo(1)));

    public static final DeferredItem<CustomPackArmorItem> PACK_BOOTS = ITEMS.register("pack_boots",
            () -> new CustomPackArmorItem(ModArmorMaterials.KEVLAR, ArmorItem.Type.BOOTS,
                    new Item.Properties().stacksTo(1)));

    // ==================== REB Backpacks ====================
    public static final DeferredItem<RebBackpackItem> REB_BACKPACK_DESERT = ITEMS.register("reb-backpack-desert",
            () -> new RebBackpackItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE), "desert"));

    public static final DeferredItem<RebBackpackItem> REB_BACKPACK_EMR = ITEMS.register("reb-backpack-emr",
            () -> new RebBackpackItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE), "emr"));

    public static final DeferredItem<RebBackpackItem> REB_BACKPACK_GREEN = ITEMS.register("reb-backpack-green",
            () -> new RebBackpackItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE), "green"));

    public static final DeferredItem<RebBackpackItem> REB_BACKPACK_MULTICAM = ITEMS.register("reb-backpack-multicam",
            () -> new RebBackpackItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE), "multicam"));

    public static final DeferredItem<RebBackpackItem> REB_BACKPACK_WHITE = ITEMS.register("reb-backpack-white",
            () -> new RebBackpackItem(new Item.Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE), "white"));

    static {
        // Добавляем все рюкзаки в список для доступа
        REB_BACKPACKS.add(REB_BACKPACK_DESERT);
        REB_BACKPACKS.add(REB_BACKPACK_EMR);
        REB_BACKPACKS.add(REB_BACKPACK_GREEN);
        REB_BACKPACKS.add(REB_BACKPACK_MULTICAM);
        REB_BACKPACKS.add(REB_BACKPACK_WHITE);
    }

    private ModItems() {
    }

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

    public static void trackArmorPiece(DeferredItem<? extends Item> item) {
        ARMOR_PIECES.add(item);
    }

    public static void trackArmorPart(DeferredItem<? extends Item> item) {
        ARMOR_PARTS.add(item);
    }

    public static List<DeferredItem<? extends Item>> armorPieces() {
        return Collections.unmodifiableList(ARMOR_PIECES);
    }

    public static List<DeferredItem<? extends Item>> armorParts() {
        return Collections.unmodifiableList(ARMOR_PARTS);
    }

    public static List<DeferredItem<RebBackpackItem>> rebBackpacks() {
        return Collections.unmodifiableList(REB_BACKPACKS);
    }
}
