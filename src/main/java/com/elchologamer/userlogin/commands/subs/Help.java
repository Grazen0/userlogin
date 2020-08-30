package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.command.SubCommand;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class Help extends SubCommand {

    private final Utils utils = new Utils();

    public Help() {
        super("help", false);
    }

    @Override
    public boolean run(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length > 0) return false;

        for (String str : UserLogin.messagesFile.get().getStringList(Path.HELP)) {
            sender.sendMessage(utils.color(str));
        }
        return true;
    }
}
