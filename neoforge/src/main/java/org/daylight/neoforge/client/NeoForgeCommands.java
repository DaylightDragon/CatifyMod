package org.daylight.neoforge.client;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.animal.feline.CatVariant;
import org.daylight.InvisibilityBehaviour;
import org.daylight.config.ConfigHandler;
import org.daylight.config.Data;
import org.daylight.util.CatSkinManager;
import org.daylight.util.CatVariantUtils;
import org.daylight.util.PlayerToCatReplacer;

import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;

public class NeoForgeCommands {
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

    public static LiteralArgumentBuilder<CommandSourceStack> register() {
        return Commands.literal("catify")
                // /catify variant <variant>
                .then(Commands.literal("variant")
                        .then(Commands.argument("variant", StringArgumentType.word())
                                .suggests((suggestContext, builder) -> suggestVariants(builder, CatSkinManager.ALL_VARIANTS))
                                .executes(argContext -> {
                                    Minecraft client = Minecraft.getInstance();
                                    if (client.player == null) return 0;

                                    String variantName = StringArgumentType.getString(argContext, "variant");

                                    try {
                                        if (CatSkinManager.isVanillaVariant(variantName)) {
                                            Holder<CatVariant> variant = CatVariantUtils.deserializeVariant(
                                                    argContext.getSource().registryAccess(), variantName);
                                            String name = CatVariantUtils.serializeVariant(variant);

                                            ConfigHandler.catVariant.set(name);
                                            ConfigHandler.catVariantVanilla.set(true);
                                            PlayerToCatReplacer.setLocalCatVariant(variant);
                                            ConfigHandler.CONFIG.save();

                                            client.player.sendSystemMessage(Component.literal(
                                                    "§fNow set your cat skin to: §a§l" + name.toLowerCase(Locale.ROOT)));
                                            return 1;
                                        } else if (CatSkinManager.getActualCustomFileName(variantName) != null) {
                                            String finalVariantName = CatSkinManager.getActualCustomFileName(variantName);

                                            ConfigHandler.catVariant.set(finalVariantName);
                                            ConfigHandler.catVariantVanilla.set(false);
                                            ConfigHandler.CONFIG.save();

                                            if (PlayerToCatReplacer.setCustomCatTexture(client.player, finalVariantName)) {
                                                client.player.sendSystemMessage(Component.literal(
                                                        "§fNow set your cat skin to: §a§l" + variantName));
                                                if (!PlayerToCatReplacer.setCustomCatHandTexture(finalVariantName)
                                                        && ConfigHandler.catHandActive.get()) {
                                                    client.player.sendSystemMessage(Component.literal(
                                                            "§eWarning: Couldn't find a texture for custom hand §l§f" + variantName));
                                                }
                                                return 1;
                                            } else {
                                                client.player.sendSystemMessage(Component.literal(
                                                        "§cCouldn't find §l§f" + variantName
                                                                + ".png §r§c in §l§f/config/catify/cat_enitity_skins"));
                                                return 0;
                                            }
                                        } else {
                                            client.player.sendSystemMessage(Component.literal(
                                                    "§cUnknown variant: §l§f" + ConfigHandler.catVariant.get()));
                                            return 0;
                                        }
                                    } catch (Exception e) {
                                        client.player.sendSystemMessage(Component.literal(
                                                "§cUnknown variant: §l§f" + ConfigHandler.catVariant.get()));
                                        return 0;
                                    }
                                })
                        )
                )
                // /catify mode [state]
                .then(Commands.literal("enabled")
                        .executes(ctx -> performSetCatActive(!ConfigHandler.replacementActive.get()))
                        .then(Commands.argument("state", StringArgumentType.word())
                                .suggests((suggestionContext, builder) -> suggestVariants(builder, MODES_OF_OFF))
                                .executes(argContext -> {
                                    Boolean state = getOnOffState(argContext, "state");
                                    return state != null ? performSetCatActive(state) : 0;
                                })
                        )
                )
                // /catify hand [state]
                .then(Commands.literal("hand")
                        .executes(ctx -> performSetCatHandActive(!ConfigHandler.catHandActive.get()))
                        .then(Commands.argument("state", StringArgumentType.word())
                                .suggests((suggestionContext, builder) -> suggestVariants(builder, MODES_OF_OFF))
                                .executes(argContext -> {
                                    Boolean state = getOnOffState(argContext, "state");
                                    if (state != null) {
                                        if (state && Data.catHandTexture == null
                                                && !ConfigHandler.catVariantVanilla.get()) {
                                            if (Minecraft.getInstance().player != null) {
                                                Minecraft.getInstance().player.sendSystemMessage(Component.literal(
                                                        "§eWarning: Couldn't find a texture for the custom hand!"));
                                            }
                                        }
                                        return performSetCatHandActive(state);
                                    }
                                    return 0;
                                })
                        )
                )
                // /catify damage [visible]
                .then(Commands.literal("damage")
                        .executes(ctx -> performSetCatDamageVisible(!ConfigHandler.catDamageVisible.get()))
                        .then(Commands.argument("visible", StringArgumentType.word())
                                .suggests((suggestionContext, builder) -> suggestVariants(builder, MODES_OF_OFF))
                                .executes(argContext -> {
                                    Boolean state = getOnOffState(argContext, "visible");
                                    return state != null ? performSetCatDamageVisible(state) : 0;
                                })
                        )
                )
                // /catify invisibility <mode>
                .then(Commands.literal("invisibility")
                        .then(Commands.argument("mode", StringArgumentType.word())
                                .suggests((suggestionContext, builder) -> suggestVariants(builder, INVISIBILITY_MODES))
                                .executes(argContext -> {
                                    Minecraft mc = Minecraft.getInstance();
                                    String stateStr = StringArgumentType.getString(argContext, "mode");

                                    InvisibilityBehaviour behaviour;
                                    if (stateStr.equalsIgnoreCase("never")) {
                                        behaviour = InvisibilityBehaviour.NEVER;
                                    } else if (stateStr.equals("vanilla")) {
                                        behaviour = InvisibilityBehaviour.VANILLA;
                                    } else if (stateStr.equals("charged")) {
                                        behaviour = InvisibilityBehaviour.CHARGED;
                                    } else {
                                        if (mc != null && mc.player != null) {
                                            mc.player.sendSystemMessage(Component.literal(
                                                    "§cUnexpected value: §l§f" + stateStr));
                                        }
                                        return 0;
                                    }
                                    return performSetInvisibilityBehaviour(behaviour);
                                })
                        )
                );
    }

    private static Boolean getOnOffState(CommandContext<CommandSourceStack> context, String argumentName) {
        Minecraft mc = Minecraft.getInstance();
        String stateStr = StringArgumentType.getString(context, argumentName);

        boolean state;
        if(stateStr.equalsIgnoreCase("on")) {
            state = true;
        } else if(stateStr.equalsIgnoreCase("off")) {
            state = false;
        } else {
            if(mc != null && mc.player != null) mc.player.sendSystemMessage(
                    Component.literal("§cUnexpected value: §l§f" + stateStr)
            );
            return null;
        }
        return state;
    }

    private static int performSetCatActive(boolean state) {
        Minecraft mc = Minecraft.getInstance();

        ConfigHandler.replacementActive.set(state);
        ConfigHandler.replacementActive.save();

        if(mc != null && mc.player != null) mc.player.sendSystemMessage(
                Component.literal(ConfigHandler.replacementActive.get() ?
                                "§fNow you §a§lare§r a cat!" :
                                "§fNow you are §6§lno longer§r a cat!")
        );
        return 1;
    }

    private static int performSetCatHandActive(boolean state) {
        Minecraft mc = Minecraft.getInstance();

        ConfigHandler.catHandActive.set(state);
        ConfigHandler.catHandActive.save();

        if(mc != null && mc.player != null) mc.player.sendSystemMessage(
                Component.literal(ConfigHandler.catHandActive.get() ?
                        "§fNow you §a§lhave§r a cat hand!" :
                        "§fNow you §6§lno longer have§r a cat hand!")
        );
        return 1;
    }

    private static int performSetCatDamageVisible(boolean state) {
        Minecraft mc = Minecraft.getInstance();

        ConfigHandler.catDamageVisible.set(state);
        ConfigHandler.catDamageVisible.save();

        if(mc != null && mc.player != null) mc.player.sendSystemMessage(
                Component.literal(ConfigHandler.catDamageVisible.get() ?
                        "§fNow you §a§lhave§r cat damage displayed!" :
                        "§fNow you §6§lno longer have§r cat damage displayed!")
        );
        return 1;
    }

    private static int performSetInvisibilityBehaviour(InvisibilityBehaviour behaviour) {
        Minecraft mc = Minecraft.getInstance();

        ConfigHandler.invisibilityBehaviour.set(behaviour);
        ConfigHandler.CONFIG.save();

        if(mc != null && mc.player != null) mc.player.sendSystemMessage(
                Component.literal("§fSet invisibility mode to: §a§l" +
                        ConfigHandler.invisibilityBehaviour.get().toString().toLowerCase(Locale.ROOT))
        );
        return 1;
    }
}
