package dev.pizzawunsch.advancedhorses.race;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public final class RaceCommand {
    private RaceCommand() {
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("horserace")
            .then(Commands.literal("create")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.argument("name", StringArgumentType.word()).executes(RaceCommand::create)))
            .then(Commands.literal("finish")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(name().executes(RaceCommand::finish)))
            .then(Commands.literal("remove")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(name().executes(RaceCommand::remove)))
            .then(Commands.literal("list").executes(RaceCommand::list))
            .then(Commands.literal("top").then(name().executes(RaceCommand::top))));
    }

    private static com.mojang.brigadier.builder.RequiredArgumentBuilder<CommandSourceStack, String> name() {
        return Commands.argument("name", StringArgumentType.word())
            .suggests((context, builder) -> SharedSuggestionProvider.suggest(RaceManager.names(), builder));
    }

    private static int create(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String name = StringArgumentType.getString(context, "name");
        if (!RaceManager.create(name, player)) {
            context.getSource().sendFailure(Component.translatable("advancedhorses.race.exists", name));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable("advancedhorses.race.created", name), true);
        return 1;
    }

    private static int finish(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        String name = StringArgumentType.getString(context, "name");
        if (!RaceManager.setFinish(name, player)) {
            context.getSource().sendFailure(Component.translatable("advancedhorses.race.unknown", name));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable("advancedhorses.race.finish_set", name), true);
        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        if (RaceManager.get(name) == null) {
            context.getSource().sendFailure(Component.translatable("advancedhorses.race.unknown", name));
            return 0;
        }
        RaceManager.remove(name);
        context.getSource().sendSuccess(() -> Component.translatable("advancedhorses.race.removed", name), true);
        return 1;
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        if (RaceManager.names().isEmpty()) {
            context.getSource().sendSuccess(() -> Component.translatable("advancedhorses.race.none"), false);
            return 0;
        }
        for (String name : RaceManager.names()) {
            RaceManager.Race race = RaceManager.get(name);
            Component status = race.hasFinish
                ? Component.translatable("advancedhorses.race.status.ready").withStyle(ChatFormatting.GREEN)
                : Component.translatable("advancedhorses.race.status.no_finish").withStyle(ChatFormatting.RED);
            context.getSource().sendSuccess(() -> Component.literal(name + " ").withStyle(ChatFormatting.GOLD).append(status), false);
        }
        return RaceManager.names().size();
    }

    private static int top(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "name");
        RaceManager.Race race = RaceManager.get(name);
        if (race == null) {
            context.getSource().sendFailure(Component.translatable("advancedhorses.race.unknown", name));
            return 0;
        }
        context.getSource().sendSuccess(() -> Component.translatable("advancedhorses.race.top", name).withStyle(ChatFormatting.GOLD), false);
        for (int i = 0; i < race.top.size(); i++) {
            RaceManager.Result result = race.top.get(i);
            int place = i + 1;
            context.getSource().sendSuccess(() -> Component.literal(place + ". " + RaceManager.format(result.ticks())
                + "  " + result.player() + " / " + result.horse()), false);
        }
        return race.top.size();
    }
}
