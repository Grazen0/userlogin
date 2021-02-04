package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.api.command.SubCommand;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {

    public ReloadCommand() {
        super("reload");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        UserLogin plugin = UserLogin.getPlugin();
        plugin.load();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (!UserLoginAPI.isLoggedIn(player)) {
                plugin.getPlayerManager().get(player.getUniqueId()).activateTimeout();
            }
        }

        Utils.sendMessage(Path.RELOAD, sender);
        return true;
    }
}
