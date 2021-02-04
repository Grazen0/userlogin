package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.command.SubCommand;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.ULPlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {

    private final UserLogin plugin;

    public ReloadCommand() {
        super("reload");
        plugin = UserLogin.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.load();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            ULPlayer ulPlayer = plugin.getPlayer(player);
            if (!ulPlayer.isLoggedIn()) ulPlayer.activateTimeout();
        }

        sender.sendMessage(plugin.getMessage(Path.RELOAD));
        return true;
    }
}
