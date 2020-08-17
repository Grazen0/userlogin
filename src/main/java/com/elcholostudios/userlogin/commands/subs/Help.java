package com.elcholostudios.userlogin.commands.subs;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.command.SubCommand;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.command.CommandSender;

public class Help extends SubCommand {

    private final Utils utils = new Utils();

    public Help() {
        super("help", false);
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        if (args.length > 0) return false;

        for (String str : UserLogin.messagesFile.get().getStringList(Path.HELP)) {
            sender.sendMessage(utils.color(str));
        }
        return true;
    }
}
