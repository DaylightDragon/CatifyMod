package org.daylight.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.entity.CatRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.client.renderer.entity.state.CatRenderState;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.state.gui.GuiRenderState;
import net.minecraft.client.renderer.state.gui.pip.GuiEntityRenderState;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.player.Player;
import org.daylight.config.ConfigHandler;
import org.daylight.features.CatChargeFeatureRenderer;
import org.daylight.util.PlayerToCatReplacer;
import org.daylight.util.StateStorage;
import org.daylight.util.flags.TemporaryFlags;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(GuiGraphicsExtractor.class)
public class GuiGraphicsExtractorMixin {
//    @ModifyVariable(
//            method = "entity",
//            at = @At("HEAD"),
//            argsOnly = true
//    )
//    private EntityRenderState replaceWithCatState(EntityRenderState original) {

    @Shadow
    @Final
    public GuiRenderState guiRenderState;

    @Shadow
    @Final
    public GuiGraphicsExtractor.ScissorStack scissorStack;

    @Inject(
            method = "entity",
            at = @At("HEAD"),
            cancellable = true
    )
    public void entity(final EntityRenderState renderState, final float scale, final Vector3f translation, final Quaternionf rotation, final @Nullable Quaternionf overrideCameraAngle, final int x0, final int y0, final int x1, final int y1, CallbackInfo ci) {
        EntityRenderState stateToRender = getNewState(renderState, x0, x1, y0, y1);
        if(stateToRender == null) stateToRender = renderState;
        else {
            ci.cancel();
        }

        renderState.lightCoords = 15728880;
        this.guiRenderState.addPicturesInPictureState(new GuiEntityRenderState(stateToRender, translation, rotation, overrideCameraAngle, x0, y0, x1, y1, scale, this.scissorStack.peek()));
    }

    private EntityRenderState getNewState(EntityRenderState original, int x0, int x1, int y0, int y1) {
        if (!(original instanceof AvatarRenderState playerState)) return original;
//        System.out.println(((AvatarRenderState) original).bodyRot);

        // Проверяем, нужно ли заменить (используя вашу логику StateStorage)
        UUID uuid = StateStorage.inventoryPlayerUUID; // currentStates.get(playerState);
        if (uuid == null) return original;

        Player player = PlayerToCatReplacer.getPlayerById(uuid);
        if (player == null) return original;

        Cat cat = (Cat) PlayerToCatReplacer.getCatForPlayer(player);
        if (cat == null) return original;

        // Синхронизируем данные кошки (используя ваш syncEntity2 и т.д.)
        PlayerToCatReplacer.syncEntity2(player, cat);

        // Создаём CatRenderState
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        CatRenderer catRenderer = (CatRenderer) dispatcher.getRenderer(cat);
        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(true);
        CatRenderState catState = catRenderer.createRenderState(cat, partialTick);
        catRenderer.extractRenderState(cat, catState, partialTick);

        // Синхронизируем некоторые визуальные данные
        catState.lightCoords = playerState.lightCoords;
        if (ConfigHandler.catDamageVisible.getCached()) {
            catState.hasRedOverlay = playerState.hasRedOverlay;
        }

        // Обрабатываем специальную логику GUI (например, повороты в инвентаре)
        if (TemporaryFlags.CURRENTLY_RENDERING_INVENTORY.isSet() && Boolean.TRUE.equals(TemporaryFlags.CURRENTLY_RENDERING_INVENTORY.get())) {
//            System.out.println("special");
//            if (StateStorage.inventoryRelativeHeadYaw != null) catState.yRot = StateStorage.inventoryRelativeHeadYaw;
//            if (StateStorage.inventoryBodyYaw != null) catState.bodyRot = StateStorage.inventoryBodyYaw;
//            if (StateStorage.inventoryPitch != null) catState.xRot = StateStorage.inventoryPitch;

            float mouseX = (float) (Minecraft.getInstance().mouseHandler.xpos()
                    * Minecraft.getInstance().getWindow().getGuiScaledWidth()
                    / Minecraft.getInstance().getWindow().getScreenWidth());
            float mouseY = (float) (Minecraft.getInstance().mouseHandler.ypos()
                    * Minecraft.getInstance().getWindow().getGuiScaledHeight()
                    / Minecraft.getInstance().getWindow().getScreenHeight());

            float centerX = (x0 + x1) / 2.0F;
            float centerY = (y0 + y1) / 2.0F;
            float xAngle = (float) Math.atan((centerX - mouseX) / 40.0F);
            float yAngle = (float) Math.atan((centerY - mouseY) / 40.0F);

            float bodyRot = 180.0F + xAngle * 20.0F;
            float headYaw = xAngle * 20.0F;          // относительно тела
            float pitch   = -yAngle * 20.0F;         // если pose != FALL_FLYING, иначе 0

            // Тело поворачивается в противоположную сторону от мыши + базовые 180°
            catState.bodyRot = 180.0F + xAngle * 20.0F;

            // Голова "смотрит" на мышь (относительно тела)
            catState.yRot = xAngle * 20.0F;

            // Наклон головы вверх/вниз
            if (catState.pose != Pose.FALL_FLYING) {
                catState.xRot = -yAngle * 20.0F;
            } else {
                catState.xRot = 0.0F;
            }
        }

        // 2. Переносим данные о получении урона (для красной вспышки)
        if (ConfigHandler.catDamageVisible.getCached()) {
            catState.hasRedOverlay = playerState.hasRedOverlay;
        }

        // 3. Копируем информацию об освещении
        catState.lightCoords = playerState.lightCoords;

        // 4. Отключаем эффект "заряда" в GUI (если у вас есть такая функция)
        //    В старом коде вы вызывали getChargeData(cat).chargeActive = false
        //    Здесь также нужно сбросить
        CatChargeFeatureRenderer.getChargeData(cat).chargeActive = false;

        // 5. Возвращаем заменённый CatRenderState (как возвращаемое значение @ModifyVariable)

//        System.out.println("bodyRot=" + catState.bodyRot + " yRot=" + catState.yRot + " xRot=" + catState.xRot);

        return catState;
    }
}
