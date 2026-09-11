package ru.liko.warbornrenewed;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import org.slf4j.Logger;
import ru.liko.warbornrenewed.config.WarbornConfig;
import ru.liko.warbornrenewed.content.recipe.ArmorAnvilRepairHandler;
import ru.liko.warbornrenewed.network.NetworkHandler;
import ru.liko.warbornrenewed.registry.ModArmorMaterials;
import ru.liko.warbornrenewed.registry.ModAttributes;
import ru.liko.warbornrenewed.registry.ModCreativeTabs;
import ru.liko.warbornrenewed.registry.ModDataComponents;
import ru.liko.warbornrenewed.registry.ModItems;
import ru.liko.warbornrenewed.registry.ModRecipes;
import ru.liko.warbornrenewed.registry.ModSoundEvents;
import ru.liko.warbornrenewed.packs.PackCreativeTabs;
import ru.liko.warbornrenewed.packs.WarbornPackManager;
import ru.liko.warbornrenewed.setup.WarbornArmorSets;

@Mod(Warbornrenewed.MODID)
public class Warbornrenewed {
    public static final String MODID = "warbornrenewed";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Warbornrenewed(IEventBus modEventBus, ModContainer modContainer) {
        // Register config
        modContainer.registerConfig(ModConfig.Type.COMMON, WarbornConfig.SPEC);

        // Register registries
        // IMPORTANT: ArmorMaterials must be registered BEFORE items that use them!
        ModArmorMaterials.register(modEventBus);
        ModDataComponents.register(modEventBus);
        ModAttributes.register(modEventBus);
        ModItems.register(modEventBus);
        ModSoundEvents.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModRecipes.RECIPE_SERIALIZERS.register(modEventBus);
        ArmorAnvilRepairHandler.register();

        // Register network
        NetworkHandler.register(modEventBus);

        // Initialize armor sets
        WarbornArmorSets.bootstrap();

        // Load custom packs from warbornrenewed/packs directory
        WarbornPackManager.loadPacks();

        // Register dynamic creative tabs for each loaded pack
        PackCreativeTabs.registerPackTabs();
        PackCreativeTabs.register(modEventBus);

        LOGGER.debug("WarBorn Renewed core initialised");
    }

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
