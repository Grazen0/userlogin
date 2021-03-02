package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.util.command.BaseCommand;
import com.elchologamer.userlogin.util.database.Database;
import com.elchologamer.userlogin.util.extensions.QuickMap;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChangePasswordCommand extends BaseCommand {

    public ChangePasswordCommand() {
        super("changepassword", true);
    }

    @Override
    public boolean run(CommandSender sender, String label, String[] args) {
        Player p = (Player) sender;
        ULPlayer ulPlayer = getPlugin().getPlayer(p);

        if (!ulPlayer.isLoggedIn()) {
            ulPlayer.sendPathMessage("messages.must_log_in");
            return true;
        }

        if (args.length < 3) return false;

        Database db = getPlugin().getDB();

        // Check first password
        if (!db.comparePasswords(p.getUniqueId(), args[0])) {
            ulPlayer.sendPathMessage("messages.incorrect_password");
            return true;
        }

        // Check that passwords match
        String newPassword = args[1];
        if (!newPassword.equals(args[2])) {
            ulPlayer.sendPathMessage("messages.different_passwords");
            return true;
        }

        FileConfiguration config = getPlugin().getConfig();
        int minChars = config.getInt("password.minCharacters", 0);
        int maxChars = config.getInt("password.maxChars", 128);

        // Check password length
        if (newPassword.length() < minChars) {
            ulPlayer.sendPathMessage(
                    "messages.short_password",
                    new QuickMap<>("chars", minChars)
            );
            return true;
        }

        if (newPassword.length() > maxChars) {
            ulPlayer.sendPathMessage(
                    "messages.long_password",
                    new QuickMap<>("chars", maxChars)
            );
            return true;
        }

        // Check password regex
        String regex = config.getString("password.regex", "").trim();
        if (!regex.equals("") && !newPassword.matches(regex)) {
            ulPlayer.sendPathMessage(
                    "messages.regex_mismatch",
                    new QuickMap<>("regex", regex)
            );
            return true;
        }

        try {
            db.updatePassword(p.getUniqueId(), newPassword);
            ulPlayer.sendPathMessage("messages.password_changed");
        } catch (Exception e) {
            ulPlayer.sendPathMessage("messages.password_change_error");
            e.printStackTrace();
        }

        return true;
    }
}
