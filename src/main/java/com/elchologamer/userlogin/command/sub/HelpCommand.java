package com.elchologamer.userlogin.command.sub;

import com.elchologamer.userlogin.command.base.SubCommand;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class HelpCommand extends SubCommand {

    public HelpCommand() {
        super("help", "ul.help");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        List<String> lines = new ArrayList<>();
        getPlugin().getLang().getEntries().getStringList("messages.help").forEach(l -> lines.add(Utils.color(l)));

        sender.sendMessage(String.join("\n", lines.toArray(new String[0])));
        return true;
    }
}