package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.util.PasswordEncryptor;
import com.elchologamer.userlogin.util.database.Database;
import com.elchologamer.userlogin.util.extensions.QuickMap;
import com.elchologamer.userlogin.util.extensions.ULPlayer;

import java.util.UUID;

public class RegisterCommand extends AuthCommand {

    public RegisterCommand() {
        super("register");
    }

    @Override
    protected AuthType authenticate(ULPlayer ulPlayer, String[] args) {
        Database db = getPlugin().getDB();

        // Check if player is not already registered
        UUID uuid = ulPlayer.getPlayer().getUniqueId();
        if (db.isRegistered(uuid)) {
            ulPlayer.sendPathMessage("messages.already-registered");
            return null;
        }

        // Check usage
        if (args.length != 2) return null;

        // Check if password is over the minimum amount of characters
        String password = args[0];
        int minChars = getPlugin().getConfig().getInt("password.minCharacters", 0);
        if (password.length() < minChars) {
            ulPlayer.sendPathMessage(
                    "messages.short-password",
                    new QuickMap<>("chars", minChars)
            );
            return null;
        }

        // Check that both passwords match
        if (!password.equals(args[1])) {
            ulPlayer.sendPathMessage("messages.different-passwords");
            return null;
        }

        // Encrypt password if enabled
        if (getPlugin().getConfig().getBoolean("password.encrypt"))
            password = PasswordEncryptor.encodeBase64(password);

        try {
            db.createPassword(uuid, password);
            return AuthType.REGISTER;
        } catch (Exception e) {
            e.printStackTrace();
            ulPlayer.sendPathMessage("messages.register-failed");
            return null;
        }
    }
}
