package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.pluginapi.util.command.SubCommand;
import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class SqlCommand extends SubCommand {

    public SqlCommand() {
        super("sql");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) return false;

        FileConfiguration messages = UserLogin.getPlugin().getMessages();
        if(messages == null) return true;

        // Get current MySQL state
        String state;
        if (!Utils.sqlMode())
            state = messages.getString(Path.SQL_DISABLED);
        else if (UserLogin.getPlugin().getSQL().isConnected())
            state = messages.getString(Path.SQL_CONNECTED);
        else
            state = messages.getString(Path.SQL_DISCONNECTED);

        Utils.sendMessage(Path.SQL_STATE, sender, new String[]{"state"}, new String[]{state});
        return true;
    }
}
