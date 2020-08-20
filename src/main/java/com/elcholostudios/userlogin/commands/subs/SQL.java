package com.elcholostudios.userlogin.commands.subs;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.api.command.SubCommand;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SQL extends SubCommand {

    private final Utils utils = new Utils();

    public SQL() {
        super("sql", false);
    }

    @Override
    public boolean run(@NotNull CommandSender sender, String @NotNull [] args) {
        if(args.length > 0) return false;

        // Get current MySQL state
        String state;
        if(!utils.sqlMode())
            state = getMsg(Path.SQL_DISABLED);
        else if(UserLogin.sql.isConnected)
            state = getMsg(Path.SQL_CONNECTED);
        else
            state = getMsg(Path.SQL_DISCONNECTED);

        utils.sendMessage(Path.SQL_STATE, sender, new String[]{"state"}, new String[]{state});
        return true;
    }

    private @Nullable String getMsg(@NotNull String path) {
        return UserLogin.messagesFile.get().getString(path);
    }
}
