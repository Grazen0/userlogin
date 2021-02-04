package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.PasswordEncryptor;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.database.Database;
import org.bukkit.entity.Player;

public class LoginCommand extends AuthCommand {

    @Override
    protected boolean authenticate(Player p, String[] args) {
        Database db = UserLogin.getPlugin().getDB();
        String password = db.getPassword(p.getUniqueId());

        // Check if player is registered
        if (password == null) {
            Utils.sendMessage(Path.NOT_REGISTERED, p);
            return false;
        }

        // Check usage
        if (args.length != 1) return false;

        // Decrypt stored password if needed
        password = PasswordEncryptor.decodeBase64(password);

        if (!args[0].equals(password)) {
            Utils.sendMessage(Path.INCORRECT_PASSWORD, p);
            return false;
        }

        return true;
    }
}
