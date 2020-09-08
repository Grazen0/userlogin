package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.command.SubCommand;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SQL extends SubCommand {

    public SQL() {
        super("sql", false);
    }

    @Override
    public boolean run(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length > 0) return false;

        // Get current MySQL state
        String state;
        if (!Utils.sqlMode())
            state = getMsg(Path.SQL_DISABLED);
        else if (UserLogin.sql.isConnected)
            state = getMsg(Path.SQL_CONNECTED);
        else
            state = getMsg(Path.SQL_DISCONNECTED);

        Utils.sendMessage(Path.SQL_STATE, sender, new String[]{"state"}, new String[]{state});
        return true;
    }

    private @Nullable String getMsg(@NotNull String path) {
        return UserLogin.messagesFile.get().getString(path);
    }
}
