package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.api.QuickMap;
import com.elchologamer.userlogin.util.PasswordEncryptor;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.ULPlayer;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.database.Database;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RegisterCommand extends AuthCommand {

    @Override
    protected boolean authenticate(Player p, String[] args) {
        Database db = getPlugin().getDB();

        ULPlayer ulPlayer = getPlugin().getPlayer(p);

        // Check if player is not already registered
        UUID uuid = p.getUniqueId();
        if (db.getPassword(uuid) != null) {
            ulPlayer.sendPathMessage(Path.ALREADY_REGISTERED);
            return true;
        }

        // Check usage
        if (args.length != 2) return false;

        // Check if password is over the minimum amount of characters
        String password = args[0];
        int minChars = getPlugin().getConfig().getInt("password.minCharacters", 0);
        if (password.length() < minChars) {
            ulPlayer.sendPathMessage(Path.SHORT_PASSWORD, new QuickMap<>("chars", minChars));
            return true;
        }

        // Check that both passwords match
        if (!password.equals(args[1])) {
            ulPlayer.sendMessage(Path.DIFFERENT_PASSWORDS);
            return true;
        }

        // Encrypt password if enabled
        if (getPlugin().getConfig().getBoolean("password.encrypt"))
            password = PasswordEncryptor.encodeBase64(password);

        try {
            db.setPassword(uuid, password);
        } catch (Exception e) {
            e.printStackTrace();
            p.sendMessage(Utils.color("&dError registering your account, try again"));
            return false;
        }

        return true;
    }
}
