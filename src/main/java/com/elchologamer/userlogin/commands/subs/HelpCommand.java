package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.command.SubCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class HelpCommand extends SubCommand {

    private final UserLogin plugin = UserLogin.getPlugin();

    public HelpCommand() {
        super("help", "ul.help");
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (args.length > 0) return false;

        FileConfiguration messages = plugin.getMessages();
        if (messages == null) return true;

        for (String str : messages.getStringList(Path.HELP)) {
            sender.sendMessage(Utils.color(str));
        }
        return true;
    }
}
