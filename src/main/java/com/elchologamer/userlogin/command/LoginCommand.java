package com.elchologamer.userlogin.command;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.api.types.AuthType;

import java.util.UUID;

public class LoginCommand extends AuthCommand {

    public LoginCommand() {
        super("login", AuthType.LOGIN, 1);
    }

    public boolean authenticate(ULPlayer ulPlayer, String[] args) {
        UUID uuid = ulPlayer.getPlayer().getUniqueId();

        // Check if player is registered
        if (!getPlugin().getDB().isRegistered(uuid)) {
            ulPlayer.sendMessage("messages.not_registered");
            return false;
        }

        // Authenticate passwords
        if (!getPlugin().getDB().comparePasswords(uuid, args[0])) {
            boolean not_exceeded = ulPlayer.onLoginAttempt();
            if (not_exceeded) {
                ulPlayer.sendMessage("messages.incorrect_password");
            }

            return false;
        }

        return true;
    }
}