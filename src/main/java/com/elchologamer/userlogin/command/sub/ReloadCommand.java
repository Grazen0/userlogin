package com.elchologamer.userlogin.command.sub;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.command.base.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ReloadCommand extends SubCommand {
    public ReloadCommand() {
        super("reload", "ul.reload");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        getPlugin().reloadPlugin();

        for (Player player : getPlugin().getServer().getOnlinePlayers()) {
            ULPlayer ulPlayer = ULPlayer.get(player);
            if (!ulPlayer.isLoggedIn()) {
                ulPlayer.schedulePreLoginTasks();
            }
        }

        sender.sendMessage(getPlugin().getLang().getMessage("commands.reload"));
        return true;
    }
}