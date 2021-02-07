package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.util.PasswordEncryptor;
import com.elchologamer.userlogin.util.database.Database;
import com.elchologamer.userlogin.util.extensions.ULPlayer;

public class LoginCommand extends AuthCommand {

    public LoginCommand() {
        super("login");
    }

    @Override
    protected AuthType authenticate(ULPlayer ulPlayer, String[] args) {
        Database db = getPlugin().getDB();
        String password = db.getPassword(ulPlayer.getPlayer().getUniqueId());

        // Check if player is registered
        if (password == null) {
            ulPlayer.sendPathMessage("messages.not-registered");
            return null;
        }

        // Check usage
        if (args.length != 1) return null;

        // Decrypt stored password if needed
        password = PasswordEncryptor.decodeBase64(password);

        if (!args[0].equals(password)) {
            ulPlayer.sendPathMessage("messages.incorrect-password");
            return null;
        }

        return AuthType.LOGIN;
    }
}
