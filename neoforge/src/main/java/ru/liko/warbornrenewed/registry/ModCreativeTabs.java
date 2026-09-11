package ru.liko.warbornrenewed.registry;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import ru.liko.warbornrenewed.Warbornrenewed;

public final class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            Warbornrenewed.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ARMOR = TABS.register("armor",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.warbornrenewed.armor"))
                    .icon(() -> ModItems.armorPieces().stream()
                            .findFirst()
                            .map(entry -> new ItemStack(entry.get()))
                            .orElseGet(() -> new ItemStack(Items.NETHERITE_HELMET)))
                    .displayItems((parameters, output) -> {
                        // Добавляем всю броню
                        ModItems.armorPieces().forEach(entry -> output.accept(entry.get().getDefaultInstance()));
                        // Добавляем бинокль
                        output.accept(ModItems.BINOCULAR.get().getDefaultInstance());
                        output.accept(ModItems.BALLISTIC_STEEL_REPAIR_PLATE.get().getDefaultInstance());
                        output.accept(ModItems.BALLISTIC_CERAMIC_REPAIR_PLATE.get().getDefaultInstance());
                        output.accept(ModItems.BALLISTIC_COMPOSITE_REPAIR_PLATE.get().getDefaultInstance());
                        output.accept(ModItems.KEVLAR_REPAIR_PATCH.get().getDefaultInstance());
                        // Добавляем рюкзаки РЭБ
                        ModItems.rebBackpacks().forEach(entry -> output.accept(entry.get().getDefaultInstance()));
                    })
                    .build());

    private ModCreativeTabs() {
    }

    public static void register(IEventBus eventBus) {
        TABS.register(eventBus);
    }
}
