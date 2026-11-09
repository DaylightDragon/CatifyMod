package org.daylight.util;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
//import net.minecraft.client.render.entity.state.EntityRenderState;
//import net.minecraft.client.texture.NativeImage;
//import net.minecraft.client.texture.NativeImageBackedTexture;
//import net.minecraft.client.level.ClientWorld;
//import net.minecraft.entity.Entity;
//import net.minecraft.entity.EntityType;
//import net.minecraft.entity.LivingEntity;
//import net.minecraft.registry.TagKey;
//import net.minecraft.registry.RegistryKeys;
//import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.feline.CatVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.daylight.Catify;
import org.daylight.interfaces.CustomCatTextureHolder;
import org.daylight.config.ConfigHandler;
//import org.daylight.mixin.client.CatAccessor;
import org.daylight.config.Data;
import org.daylight.mixin.client.CatEntityAccessor;
import org.daylight.mixin.client.LimbAnimatorAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

//@Environment(EnvType.CLIENT)
public class PlayerToCatReplacer {
    public static final Logger LOGGER = LoggerFactory.getLogger(Catify.MOD_ID);
    private static final Minecraft client = Minecraft.getInstance();
    private static final Map<UUID, LivingEntity> dummyModelMap = new HashMap<>();
    private static Level targetWorld;

    public static void initWorld() {
        if (client == null || client.level == null) return;
        targetWorld = client.level;

//        CatVariants.ALL_BLACK.getValue().getPath()
    }

    public static void replaceWithCat(AbstractClientPlayer player) {
        if (dummyModelMap.containsKey(player.getUUID())) return;

        Cat cat = new Cat(EntityType.CAT, targetWorld);
        if(ConfigHandler.catVariantVanilla.get()) changeCatVariant(cat, CatVariantUtils.deserializeVariant(targetWorld.registryAccess(), ConfigHandler.catVariant.get()));
        cat.setTame(false, false);
//        cat.setOwner(player);

        cat.setNoGravity(true);
        cat.noPhysics = true;
        cat.setNoAi(true);
        cat.setPos(player.getX(), player.getY(), player.getZ());

        dummyModelMap.put(player.getUUID(), cat);

        CatSkinManager.setupCustomSkin();
    }

    private static CatVariant getCatVariant(TagKey<CatVariant> variantKey) { // TODO may not work
        if(client.level == null) {
            LOGGER.warn("Client world is null");
            return null;
        }

        return client.level.registryAccess()
                .getOrThrow(Registries.CAT_VARIANT).value().getValue(variantKey.location());
    }

    public static void changeCatVariant(Cat cat, Holder<CatVariant> variant) {
//        Holder<CatVariant> holder = cat.registryAccess()
//                .lookupOrThrow(Registries.CAT_VARIANT)
//                .get(ResourceKey.create(Registries.CAT_VARIANT, variantKey.location())) // TODO not sure if correct
//                .orElse(null);

//        RegistryAccess.RegistryEntry<CatVariant> variant = getCatVariant(variantKey);
        ((CatEntityAccessor) cat).invokeSetVariant(variant);
    }

    public static void cleanup() {
        dummyModelMap.values().forEach(Entity::discard);
        dummyModelMap.clear();
    }

    public static boolean shouldReplace(Player player) {
        return dummyModelMap.containsKey(player.getUUID()) &&
                player == Minecraft.getInstance().player;
    }

    public static Player getPlayerById(UUID uuid) {
        ClientLevel world = Minecraft.getInstance().level;

        if (world != null) {
            Player player = world.getPlayerByUUID(uuid);
            if (player != null) {
                return player;
            }
        }
        return null;
    }

    public static LivingEntity getCatForPlayer(Player player) {
        return dummyModelMap.get(player.getUUID());
    }

    public static boolean isDummyCat(Cat Cat) {
        return dummyModelMap.containsValue(Cat); // && player == Minecraft.getInstance().player;
    }

    public static void syncEntity2(Player player, Cat existingCat) {
        existingCat.xo = player.xo;
        existingCat.yo = player.yo;
        existingCat.zo = player.zo;
        existingCat.setPos(player.getX(), player.getY(), player.getZ());

        existingCat.setYRot(player.getYRot());
        existingCat.setXRot(player.getXRot());

        existingCat.yRotO = player.yRotO;
        existingCat.xRotO = player.xRotO;

        existingCat.yBodyRot = player.yBodyRot;
        existingCat.yBodyRotO = player.yBodyRotO;
        existingCat.yHeadRot = player.yHeadRot;
        existingCat.yHeadRotO = player.yHeadRotO;

        syncSittingAndLimbs(player, existingCat);
    }

    public static void syncSittingAndLimbs(Player player, Cat existingCat) {
        // Sitting
        double dx = player.getX() - player.xo;
        double dz = player.getZ() - player.zo;
        double horizontalSpeed = Math.sqrt(dx * dx + dz * dz);

        boolean sneaking = player.isCrouching(); // or isInSneakingPose()
        boolean slowEnough = horizontalSpeed < 0.01;

        boolean sitting = sneaking && slowEnough;
        existingCat.setInSittingPose(sitting);

//        float partialTick = Minecraft.getInstance().getDeltaTracker().getGameTimeDeltaPartialTick(false);

//        float amplifiedSpeed = Math.min((float) horizontalSpeed * 8.0F, 1.5F);
        float targetSpeed = (float) horizontalSpeed * 4.5F; // Math.min(, 1.0F);

        existingCat.walkAnimation.update(
                (float) targetSpeed,   // целевая скорость (на основе реального смещения)
                0.9F,               // multiplier: сглаживание (0.4-0.9)
                0.2F                // positionScale: масштаб фазы (1.0 для взрослых)
        );

//        existingCat.walkAnimation.update(
//                getPlayerMovementSpeed(player),
//                0.9f,
//                1.0f
//        );
//
        if (existingCat.walkAnimation instanceof LimbAnimatorAccessor acc) {
            acc.caitify$setPosition(player.walkAnimation.position() * 0.1f); // TODO maybe missing partialtick
        }
    }

    private static float getPlayerMovementSpeed(Player player) {
        // Вычисляем скорость движения игрока
        double dx = player.getX() - player.xo;
        double dz = player.getZ() - player.zo;
        double horizontalSpeed = Math.sqrt(dx * dx + dz * dz);

        // Нормализуем скорость для анимаций
        float speed = (float) horizontalSpeed * 4f; // 20.0f;

//        System.out.println(horizontalSpeed);

        // Ограничиваем максимальную скорость
        return Math.min(speed, 1.0f);
    }

    public static void setLocalCatVariant(Holder<CatVariant> variant) {
        LivingEntity catLivingEntity = getCatForPlayer(client.player);
        if(catLivingEntity instanceof Cat Cat) {
            changeCatVariant(Cat, variant);
            if(Cat instanceof CustomCatTextureHolder customCatTextureHolder) {
                customCatTextureHolder.catModel$setCustomTexture(null);
                customCatTextureHolder.catModel$requestCustomTextureUpdate();
            }
        }
    }

    public static Identifier getCustomCatTexture(Player player) {
        LivingEntity cat = getCatForPlayer(player);
        if(cat instanceof CustomCatTextureHolder customCatTextureHolder) {
            return customCatTextureHolder.catModel$getCustomTexture();
        }
        return null;
    }

    public static boolean setCustomCatTexture(Player player, String skinName) {
        LivingEntity cat = getCatForPlayer(player);
        if(cat instanceof Cat Cat) {
            Identifier identifier = loadCustomTexture("cat_enitity_skins", skinName);
            if(!GraphicsUtils.doesTextureExist(identifier)) return false;
            if(Cat instanceof CustomCatTextureHolder customCatTextureHolder) {
//                System.out.println("Setting custom texture");
                customCatTextureHolder.catModel$setCustomTexture(identifier);
                customCatTextureHolder.catModel$requestCustomTextureUpdate();
            }
            return true;
        }
        return false;
    }

    public static boolean setCustomCatHandTexture(String skinName) {
        Identifier identifier = loadCustomTexture("cat_hand_skins", skinName);
        if(!GraphicsUtils.doesTextureExist(identifier)) {
            Data.catHandTexture = null;
            return false;
        }
        Data.catHandTexture = identifier;
        return true;
    }

    public static Identifier loadCustomTexture(String subfolder, String skinName) {
        try {
            String basePath = Minecraft.getInstance().gameDirectory.getAbsolutePath() + "/config/catify/" + subfolder;
            File folder = new File(basePath);
            folder.mkdirs();
//            System.out.println(Arrays.toString(folder.listFiles()));
            File file = new File(folder, skinName + ".png");
            if (!file.exists()) {
                return null;
            }

            NativeImage image = NativeImage.read(new FileInputStream(file));
            Supplier<String> nameSupplier = () -> subfolder + "/" + skinName;
            DynamicTexture texture = new DynamicTexture(nameSupplier, image);

            Identifier id = Identifier.fromNamespaceAndPath("catmodel", nameSupplier.get());
            Minecraft.getInstance().getTextureManager().register(id, texture);
            return id;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Entity findAsCat(UUID entityId) {
        return dummyModelMap.values().stream()
                .filter(entity -> entityId.equals(entity.getUUID()))
                .findFirst()
                .orElse(null);
    }
}
