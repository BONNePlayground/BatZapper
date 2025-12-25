package lv.id.bonne.batzapper.commands;


import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;

import lv.id.bonne.batzapper.BatZapper;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.permissions.PermissionCheck;
import net.minecraft.server.permissions.Permissions;


public class BatZapperCommands
{
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        LiteralArgumentBuilder<CommandSourceStack> baseLiteral = Commands.literal("bat_zapper").
            requires(Commands.hasPermission(LEVEL_MODERATORS));

        LiteralArgumentBuilder<CommandSourceStack> reload = Commands.literal("reload").
            executes(ctx ->
            {
                BatZapper.CONFIG_MANAGER.reloadConfig();

                ctx.getSource().sendSuccess(() -> Component.literal("Config file reloaded."), true);

                return 1;
            });

        LiteralArgumentBuilder<CommandSourceStack> reset = Commands.literal("reset").
            executes(ctx ->
            {
                BatZapper.CONFIG_MANAGER.generateConfig();
                ctx.getSource().sendSuccess(() -> Component.literal("Config file reset."), true);
                return 1;
            });

        dispatcher.register(baseLiteral.then(reset).then(reload));
    }


    public static final PermissionCheck LEVEL_MODERATORS = new PermissionCheck.Require(Permissions.COMMANDS_ADMIN);
}
