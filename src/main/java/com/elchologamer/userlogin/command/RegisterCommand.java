package com.elchologamer.userlogin.command;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.database.Database;
import com.elchologamer.userlogin.util.QuickMap;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.UUID;

public class RegisterCommand extends AuthCommand {

    public RegisterCommand() {
        super("register", AuthType.REGISTER, 2);
    }

    @Override
    protected boolean authenticate(ULPlayer ulPlayer, String[] args) {
        Database db = getPlugin().getDB();

        // Check if player is not already registered
        UUID uuid = ulPlayer.getPlayer().getUniqueId();
        if (db.isRegistered(uuid)) {
            ulPlayer.sendMessage("messages.already_registered");
            return false;
        }

        String password = args[0];
        FileConfiguration config = getPlugin().getConfig();
        int minChars = config.getInt("password.minCharacters", 0);
        int maxChars = config.getInt("password.maxChars", 128);

        // Check that both passwords match
        if (!password.equals(args[1])) {
            ulPlayer.sendMessage("messages.different_passwords");
            return false;
        }

        // Check password length
        if (password.length() < minChars) {
            ulPlayer.sendMessage(
                    "messages.short_password",
                    new QuickMap<>("chars", minChars)
            );
            return false;
        }

        if (password.length() > maxChars) {
            ulPlayer.sendMessage(
                    "messages.long_password",
                    new QuickMap<>("chars", maxChars)
            );
            return false;
        }

        // Check password regex
        String regex = config.getString("password.regex", "").trim();
        if (!regex.equals("") && !password.matches(regex)) {
            ulPlayer.sendMessage(
                    "messages.regex_mismatch",
                    new QuickMap<>("regex", regex)
            );
            return false;
        }

        try {
            db.createPassword(uuid, password);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            ulPlayer.sendMessage("messages.register_failed");
            return false;
        }
    }
}