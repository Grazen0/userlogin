package com.elchologamer.userlogin.command.sub;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.command.base.SubCommand;
import com.elchologamer.userlogin.database.Database;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class UnregisterCommand extends SubCommand {

    private final UserLogin plugin = UserLogin.getPlugin();

    public UnregisterCommand() {
        super("unregister", "ul.unregister");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        if (args.length != 1) return false;

        Database db = plugin.getDB();

        // Try getting player directly from server
        Player victim = sender.getServer().getPlayer(args[0]);
        UUID uuid = victim == null ? Utils.fetchPlayerUUID(args[0]) : victim.getUniqueId();

        if (uuid == null || !db.isRegistered(uuid)) {
            sender.sendMessage(plugin.getLang().getMessage("commands.errors.player_not_found"));
            return true;
        }

        try {
            db.deletePassword(uuid);

            String message = plugin.getLang().getMessage("commands.player_unregistered");
            sender.sendMessage(message.replace("{player}", victim == null ? args[0] : victim.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(plugin.getLang().getMessage("commands.errors.unregister_failed"));
        }

        return true;
    }
}