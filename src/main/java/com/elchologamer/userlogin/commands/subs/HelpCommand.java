package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class HelpCommand extends SubCommand {

    private final UserLogin plugin = UserLogin.getPlugin();

    public HelpCommand() {
        super("help", "ul.help");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        FileConfiguration messages = plugin.getMessages();
        if (messages == null) return true;

        for (String str : messages.getStringList("messages.help")) {
            sender.sendMessage(Utils.color(str));
        }
        return true;
    }
}
