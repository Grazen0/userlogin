package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.command.SubCommand;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class HelpCommand extends SubCommand {

    private final UserLogin plugin;

    public HelpCommand() {
        super("help");
        plugin = UserLogin.getPlugin();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) return false;

        FileConfiguration messages = plugin.getMessages();
        if(messages == null) return true;

        for (String str : messages.getStringList(Path.HELP)) {
            sender.sendMessage(Utils.color(str));
        }
        return true;
    }
}
