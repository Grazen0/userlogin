package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.util.PasswordEncryptor;
import com.elchologamer.userlogin.util.Path;
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
        if (db.getPassword(uuid) != null) {
            ulPlayer.sendPathMessage(Path.ALREADY_REGISTERED);
            return null;
        }

        // Check usage
        if (args.length != 2) return null;

        // Check if password is over the minimum amount of characters
        String password = args[0];
        int minChars = getPlugin().getConfig().getInt("password.minCharacters", 0);
        if (password.length() < minChars) {
            ulPlayer.sendPathMessage(Path.SHORT_PASSWORD, new QuickMap<>("chars", minChars));
            return null;
        }

        // Check that both passwords match
        if (!password.equals(args[1])) {
            ulPlayer.sendPathMessage(Path.DIFFERENT_PASSWORDS);
            return null;
        }

        // Encrypt password if enabled
        if (getPlugin().getConfig().getBoolean("password.encrypt"))
            password = PasswordEncryptor.encodeBase64(password);

        try {
            db.setPassword(uuid, password);
        } catch (Exception e) {
            e.printStackTrace();
            ulPlayer.sendPathMessage("commands.errors.register-failed");
            return null;
        }

        return AuthType.REGISTER;
    }
}
