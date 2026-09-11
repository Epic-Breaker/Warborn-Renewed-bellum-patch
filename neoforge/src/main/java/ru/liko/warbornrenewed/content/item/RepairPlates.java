package ru.liko.warbornrenewed.content.item;

import net.minecraft.world.item.Item;
import net.minecraft.resources.ResourceLocation;
import ru.liko.warbornrenewed.Warbornrenewed;
import net.minecraft.world.item.ItemStack;

public class RepairPlates extends Item {
    private final String pathname = new ItemStack(this).getDisplayName().getString();
    private final ResourceLocation itemTextureLocation;
    public RepairPlates(Properties properties) {
        super(properties);
        this.itemTextureLocation = Warbornrenewed.id("textures/item/" + pathname + ".png");
    }
    
}
