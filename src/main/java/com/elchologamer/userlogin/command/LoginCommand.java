package com.elchologamer.userlogin.command;

import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.database.Database;
import com.elchologamer.userlogin.ULPlayer;

import java.util.UUID;

public class LoginCommand extends AuthCommand {

    public LoginCommand() {
        super("login", AuthType.LOGIN, 1);
    }

    @Override
    protected boolean authenticate(ULPlayer ulPlayer, String[] args) {
        UUID uuid = ulPlayer.getPlayer().getUniqueId();
        Database db = getPlugin().getDB();

        // Check if player is registered
        if (!db.isRegistered(uuid)) {
            ulPlayer.sendMessage("messages.not_registered");
            return false;
        }

        // Authenticate passwords
        if (!db.comparePasswords(uuid, args[0])) {
            ulPlayer.sendMessage("messages.incorrect_password");
            return false;
        }

        return true;
    }
}
