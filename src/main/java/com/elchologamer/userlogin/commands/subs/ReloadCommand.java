package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.pluginapi.util.command.SubCommand;
import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
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
        if (Utils.sqlMode())
            UserLogin.getPlugin().getSQL().saveData();

        UserLogin.getPlugin().pluginSetup();

        for (Player player : UserLogin.getPlugin().getServer().getOnlinePlayers()) {
            if (!UserLoginAPI.isLoggedIn(player))
                Utils.setTimeout(player);
        }

        Utils.sendMessage(Path.RELOAD, sender);
        return true;
    }
}
