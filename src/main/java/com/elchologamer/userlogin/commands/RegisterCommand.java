package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.PasswordEncryptor;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.database.Database;
import org.bukkit.entity.Player;

import java.util.UUID;

public class RegisterCommand extends AuthCommand {

    @Override
    protected boolean authenticate(Player p, String[] args) {
        UserLogin plugin = UserLogin.getPlugin();
        Database db = plugin.getDB();

        // Check if player is not already registered
        UUID uuid = p.getUniqueId();
        if (db.getPassword(uuid) != null) {
            Utils.sendMessage(Path.ALREADY_REGISTERED, p);
            return true;
        }

        // Check usage
        if (args.length != 2) return false;

        // Check if password is over the minimum amount of characters
        String password = args[0];
        int minChars = plugin.getConfig().getInt("password.minCharacters", 0);
        if (password.length() < minChars) {
            Utils.sendMessage(Path.SHORT_PASSWORD, p, new String[]{"chars"}, new String[]{Integer.toString(minChars)});
            return true;
        }

        // Check that both passwords match
        if (!password.equals(args[1])) {
            Utils.sendMessage(Path.DIFFERENT_PASSWORDS, p);
            return true;
        }

        // Encrypt password if enabled
        if (plugin.getConfig().getBoolean("password.encrypt"))
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