package com.elcholostudios.userlogin.commands.subs;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.api.command.SubCommand;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Reload extends SubCommand {

    private final Utils utils = new Utils();

    public Reload() {
        super("reload", false);
    }

    @Override
    public boolean run(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length > 0) return false;

        if (utils.sqlMode())
            UserLogin.sql.saveData();

        UserLogin.pluginSetup();

        utils.sendMessage(Path.RELOAD, sender);
        return true;
    }
}
