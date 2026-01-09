package org.daylight;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.MinecraftClient;
import net.minecraft.component.Component;
import net.minecraft.component.ComponentMap;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.passive.CatVariant;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.text.Text;
import org.daylight.config.ConfigHandler;
import org.daylight.config.Data;
import org.daylight.util.CatSkinManager;
import org.daylight.util.CatVariantUtils;
import org.daylight.util.PlayerToCatReplacer;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class ModCommands {


    private static final List<String> MODES_OF_OFF = List.of(
            "on", "off"
    );

    private static final List<String> INVISIBILITY_MODES = List.of(
            "never", "vanilla", "charged"
    );

    private static CompletableFuture<Suggestions> suggestVariants(SuggestionsBuilder builder, Collection<String> variants) {
        String input = builder.getRemaining().toLowerCase(Locale.ROOT);

        for (String v : variants) {
            if (v.toLowerCase(Locale.ROOT).startsWith(input)) {
                builder.suggest(v);
            }
        }

        return builder.buildFuture();
    }

    public static void register() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            dispatcher.register(ClientCommandManager.literal("catvariant")
                            .then(ClientCommandManager.argument("variant", StringArgumentType.word())
                                            .suggests((context, builder) -> suggestVariants(builder, CatSkinManager.ALL_VARIANTS))
                                            .executes(context -> {
                                                MinecraftClient client = MinecraftClient.getInstance();
                                                if (client.player == null) return 0;

                                                String variantName = StringArgumentType.getString(context, "variant");

                                                try {
                                                    if(CatSkinManager.isVanillaVariant(variantName)) {
                                                        RegistryKey<CatVariant> variant = CatVariantUtils.deserializeVariant(variantName);
                                                        String name = CatVariantUtils.serializeVariant(variant);
//                                      PlayerCatData.setCatVariant(client.player.getUuid(), variant);

                                                        ConfigHandler.catVariant.set(name);
                                                        ConfigHandler.catVariantVanilla.set(true);
                                                        PlayerToCatReplacer.setLocalCatVariant(variant);
                                                        ConfigHandler.CONFIG.save();

                                                        client.player.sendMessage(
                                                                Text.literal("§fNow set your cat skin to: §a§l" + name.toLowerCase(Locale.ROOT)),
                                                                false
                                                        );
                                                        return 1;
                                                    } else if(CatSkinManager.getActualCustomFileName(variantName) != null) {
                                                        MinecraftClient mc = MinecraftClient.getInstance();
                                                        String finalVariantName = CatSkinManager.getActualCustomFileName(variantName);

                                                        ConfigHandler.catVariant.set(finalVariantName);
                                                        ConfigHandler.catVariantVanilla.set(false);
                                                        ConfigHandler.CONFIG.save();

                                                        if(PlayerToCatReplacer.setCustomCatEntityTexture(MinecraftClient.getInstance().player, finalVariantName)) {
                                                            if(mc.player != null) mc.player.sendMessage(
                                                                    Text.literal("§fNow set your cat skin to: §a§l" + variantName),
                                                                    false
                                                            );
                                                            if(!PlayerToCatReplacer.setCustomCatHandTexture(finalVariantName) && ConfigHandler.catHandActive.get()) {
                                                                if(mc.player != null) mc.player.sendMessage(
                                                                        Text.literal("§eWarning: Couldn't find a texture for custom hand §l§f" + variantName),
                                                                        false
                                                                );
                                                            }
                                                            return 1;
                                                        } else {
                                                            if(mc.player != null) mc.player.sendMessage(
                                                                    Text.literal("§cCouldn't find §l§f" + variantName + ".png §r§c in §l§f/data/catify/cat_enitity_skins"),
                                                                    false
                                                            );
                                                            return 0;
                                                        }
                                                    } else {
                                                        client.player.sendMessage(
                                                                Text.literal("§cUnknown variant: §l§f" + ConfigHandler.catVariant.get()),
                                                                false
                                                        );
                                                        return 0;
                                                    }
                                                } catch (Exception e) {
                                                    client.player.sendMessage(
                                                            Text.literal("§cUnknown variant: §l§f" + ConfigHandler.catVariant.get()),
                                                            false
                                                    );
                                                    return 0;
                                                }
                                            })
                            )
            );

            dispatcher.register(ClientCommandManager.literal("catmode")
                    .executes(context -> performSetCatActive(!ConfigHandler.replacementActive.get()))
                    .then(ClientCommandManager.argument("state", StringArgumentType.word())
                            .suggests((context, builder) -> suggestVariants(builder, MODES_OF_OFF))
                            .executes(context -> {
                                Boolean state = getOnOffState(context, "state");
                                if(state != null) return performSetCatActive(state);
                                else return 0;
                            })
                    )
            );

            dispatcher.register(ClientCommandManager.literal("cathand")
                    .executes(context -> performSetCatHandActive(!ConfigHandler.catHandActive.get()))
                    .then(ClientCommandManager.argument("state", StringArgumentType.word())
                            .suggests((context, builder) -> suggestVariants(builder, MODES_OF_OFF))
                            .executes(context -> {
                                Boolean state = getOnOffState(context, "state");
                                if(state != null) {
                                    if(state && Data.catHandTexture == null && !ConfigHandler.catVariantVanilla.get()) {
                                        if(MinecraftClient.getInstance().player != null) MinecraftClient.getInstance().player.sendMessage(
                                                Text.literal("§eWarning: Couldn't find a texture for the custom hand!"),
                                                false
                                        );
                                    }
                                    return performSetCatHandActive(state);
                                }
                                else return 0;
                            })
                    )
            );

            dispatcher.register(ClientCommandManager.literal("catdamage")
                    .executes(context -> performSetCatDamageVisible(!ConfigHandler.catDamageVisible.get()))
                    .then(ClientCommandManager.argument("visible", StringArgumentType.word())
                            .suggests((context, builder) -> suggestVariants(builder, MODES_OF_OFF))
                            .executes(context -> {
                                Boolean state = getOnOffState(context, "visible");
                                if(state != null) return performSetCatDamageVisible(state);
                                else return 0;
                            })
                    )
            );

            dispatcher.register(ClientCommandManager.literal("catinvisibility")
                    .then(ClientCommandManager.argument("mode", StringArgumentType.word())
                            .suggests((context, builder) -> suggestVariants(builder, INVISIBILITY_MODES))
                            .executes(context -> {
                                MinecraftClient mc = MinecraftClient.getInstance();
                                String stateStr = StringArgumentType.getString(context, "mode");

                                InvisibilityBehaviour behaviour;
                                if(stateStr.equalsIgnoreCase("never")) {
                                    behaviour = InvisibilityBehaviour.NEVER;
                                } else if(stateStr.equals("vanilla")) {
                                    behaviour = InvisibilityBehaviour.VANILLA;
                                } else if(stateStr.equals("charged")) {
                                    behaviour = InvisibilityBehaviour.CHARGED;
                                } else {
                                    if(mc != null && mc.player != null) mc.player.sendMessage(
                                            Text.literal("§cUnexpected value: §l§f" + stateStr),
                                            false
                                    );
                                    return 0;
                                }

                                return performSetInvisibilityBehaviour(behaviour);
                            })
                    )
            );

//            dispatcher.register(ClientCommandManager.literal("cattest")
//                .executes(context -> {
//                    MinecraftClient mc = MinecraftClient.getInstance();
//
//                    assert mc.player != null;
//                    ItemStack stack = mc.player.getEquippedStack(EquipmentSlot.BODY);
//
//                    return 1;
//                })
//            );
        });
    }

    private static Boolean getOnOffState(CommandContext<FabricClientCommandSource> context, String argumentName) {
        MinecraftClient mc = MinecraftClient.getInstance();
        String stateStr = StringArgumentType.getString(context, argumentName);

        boolean state;
        if(stateStr.equalsIgnoreCase("on")) {
            state = true;
        } else if(stateStr.equalsIgnoreCase("off")) {
            state = false;
        } else {
            if(mc != null && mc.player != null) mc.player.sendMessage(
                    Text.literal("§cUnexpected value: §l§f" + stateStr),
                    false
            );
            return null;
        }
        return state;
    }

    private static int performSetCatActive(boolean state) {
        MinecraftClient mc = MinecraftClient.getInstance();

        ConfigHandler.replacementActive.set(state);
        ConfigHandler.replacementActive.save();

        if(mc != null && mc.player != null) mc.player.sendMessage(
                Text.literal(ConfigHandler.replacementActive.get() ?
                        "§fNow you §a§lare§r a cat!" :
                        "§fNow you are §6§lno longer§r a cat!"), false
        );
        return 1;
    }

    private static int performSetCatHandActive(boolean state) {
        MinecraftClient mc = MinecraftClient.getInstance();

        ConfigHandler.catHandActive.set(state);
        ConfigHandler.catHandActive.save();

        if(mc != null && mc.player != null) mc.player.sendMessage(
                Text.literal(ConfigHandler.catHandActive.get() ?
                        "§fNow you §a§lhave§r a cat hand!" :
                        "§fNow you §6§lno longer have§r a cat hand!"), false
        );
        return 1;
    }

    private static int performSetCatDamageVisible(boolean state) {
        MinecraftClient mc = MinecraftClient.getInstance();

        ConfigHandler.catDamageVisible.set(state);
        ConfigHandler.catDamageVisible.save();

        if(mc != null && mc.player != null) mc.player.sendMessage(
                Text.literal(ConfigHandler.catDamageVisible.get() ?
                        "§fNow you §a§lhave§r cat damage displayed!" :
                        "§fNow you §6§lno longer have§r cat damage displayed!"), false
        );
        return 1;
    }

    private static int performSetInvisibilityBehaviour(InvisibilityBehaviour behaviour) {
        MinecraftClient mc = MinecraftClient.getInstance();

        ConfigHandler.invisibilityBehaviour.set(behaviour);
        ConfigHandler.CONFIG.save();

        if(mc != null && mc.player != null) mc.player.sendMessage(
                Text.literal("§fSet invisibility mode to: §a§l" +
                        ConfigHandler.invisibilityBehaviour.get().toString().toLowerCase(Locale.ROOT)),
                false
        );
        return 1;
    }
}
