package com.elchologamer.userlogin.command;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.command.base.BaseCommand;
import com.elchologamer.userlogin.database.Database;
import com.elchologamer.userlogin.util.QuickMap;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class ChangePasswordCommand extends BaseCommand {

    public ChangePasswordCommand() {
        super("changepassword", true);
    }

    @Override
    public boolean run(CommandSender sender, String label, String[] args) {
        Player player = (Player) sender;
        ULPlayer ulPlayer = ULPlayer.get(player);

        if (!ulPlayer.isLoggedIn()) {
            ulPlayer.sendMessage("messages.must_log_in");
            return true;
        }

        if (args.length < 3) return false;

        Database db = getPlugin().getDB();

        // Check first password
        if (!db.comparePasswords(player.getUniqueId(), args[0])) {
            ulPlayer.sendMessage("messages.incorrect_password");
            return true;
        }

        // Check that passwords match
        String newPassword = args[1];
        if (!newPassword.equals(args[2])) {
            ulPlayer.sendMessage("messages.different_passwords");
            return true;
        }

        FileConfiguration config = getPlugin().getConfig();
        int minChars = config.getInt("password.minCharacters", 0);
        int maxChars = config.getInt("password.maxChars", 128);

        // Check password length
        if (newPassword.length() < minChars) {
            ulPlayer.sendMessage(
                    "messages.short_password",
                    new QuickMap<>("chars", minChars)
            );
            return true;
        }

        if (newPassword.length() > maxChars) {
            ulPlayer.sendMessage(
                    "messages.long_password",
                    new QuickMap<>("chars", maxChars)
            );
            return true;
        }

        // Check password regex
        String regex = config.getString("password.regex", "").trim();
        if (!regex.equals("") && !newPassword.matches(regex)) {
            ulPlayer.sendMessage(
                    "messages.regex_mismatch",
                    new QuickMap<>("regex", regex)
            );
            return true;
        }

        try {
            db.updatePassword(player.getUniqueId(), newPassword);
            ulPlayer.sendMessage("messages.password_changed");
        } catch (Exception e) {
            ulPlayer.sendMessage("messages.password_change_error");
            e.printStackTrace();
        }

        return true;
    }
}