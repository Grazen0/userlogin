package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.command.SubCommand;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload extends SubCommand {

    public Reload() {
        super("reload", false);
    }

    @Override
    public boolean run(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length > 0) return false;

        if (Utils.sqlMode())
            UserLogin.sql.saveData();

        UserLogin.getPlugin().pluginSetup();

        Utils.sendMessage(Path.RELOAD, sender);
        return true;
    }
}
