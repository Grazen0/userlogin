package com.elchologamer.userlogin.command.sub;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.command.base.SubCommand;
import com.elchologamer.userlogin.ULPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {

    private final UserLogin plugin = UserLogin.getPlugin();

    public ReloadCommand() {
        super("reload", "ul.reload");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        plugin.load();

        for (Player player : plugin.getServer().getOnlinePlayers()) {
            ULPlayer ulPlayer = plugin.getPlayer(player);
            if (!ulPlayer.isLoggedIn()) ulPlayer.activateTimeout();
        }

        sender.sendMessage(plugin.getLang().getMessage("commands.reload"));
        return true;
    }
}
