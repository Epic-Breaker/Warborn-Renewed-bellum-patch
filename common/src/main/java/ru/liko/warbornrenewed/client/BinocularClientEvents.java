package ru.liko.warbornrenewed.client;

import org.jetbrains.annotations.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.CalculatePlayerTurnEvent;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import ru.liko.warbornrenewed.Warbornrenewed;
import ru.liko.warbornrenewed.content.item.BinocularItem;

/**
 * Клиентские события для бинокля - FOV модификатор (zoom)
 */
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(modid = Warbornrenewed.MODID, value = Dist.CLIENT)
public class BinocularClientEvents {

    // Коэффициент увеличения бинокля (0.1 = 10x zoom)
    private static final float BINOCULAR_FOV_MODIFIER = 0.1f;

    /** Множитель чувствительности мыши при зуме бинокля (меньше = спокойнее прицел). */
    public static final double BINOCULAR_LOOK_SENS_MULTIPLIER = 0.35;

    // Скорость интерполяции zoom (увеличено для быстрого зума)
    private static final float ZOOM_SPEED = 3.0f;

    private static float currentZoom = 1.0f;

    public static boolean isUsingBinocular(@Nullable Player player) {
        return player != null && player.isUsingItem()
                && player.getUseItem().getItem() instanceof BinocularItem;
    }

    @SubscribeEvent
    public static void onCalculatePlayerTurn(CalculatePlayerTurnEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (isUsingBinocular(mc.player)) {
            event.setMouseSensitivity(event.getMouseSensitivity() * BINOCULAR_LOOK_SENS_MULTIPLIER);
        }
    }

    @SubscribeEvent
    public static void onComputeFov(ComputeFovModifierEvent event) {
        Player player = event.getPlayer();

        if (player == null)
            return;

        // Проверяем использует ли игрок бинокль
        boolean usingBinocular = isUsingBinocular(player);

        float targetZoom = usingBinocular ? BINOCULAR_FOV_MODIFIER : 1.0f;

        // Плавная интерполяция zoom - используем фиксированный дельта-таймер
        Minecraft mc = Minecraft.getInstance();
        float delta = (mc.level != null ? mc.getTimer().getGameTimeDeltaPartialTick(false) : 0.05f) * ZOOM_SPEED;
        if (currentZoom < targetZoom) {
            currentZoom = Math.min(currentZoom + delta, targetZoom);
        } else if (currentZoom > targetZoom) {
            currentZoom = Math.max(currentZoom - delta, targetZoom);
        }

        if (usingBinocular) {
            // Применяем FOV модификатор
            event.setNewFovModifier(event.getNewFovModifier() * currentZoom);
        } else {
            // Сброс при отпускании
            currentZoom = 1.0f;
        }
    }
}
