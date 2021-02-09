package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.command.SubCommand;
import com.elchologamer.userlogin.util.database.Database;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class UnregisterCommand extends SubCommand {

    private final UserLogin plugin = UserLogin.getPlugin();

    public UnregisterCommand() {
        super("unregister", "ul.unregister");
    }

    @Override
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (args.length != 1) return false;

        Database db = plugin.getDB();

        // Try getting player directly from server
        Player victim = sender.getServer().getPlayer(args[0]);
        UUID uuid = victim == null ? Utils.fetchPlayerUUID(args[0]) : victim.getUniqueId();

        if (uuid == null || !db.isRegistered(uuid)) {
            sender.sendMessage(plugin.getMessage("commands.errors.player_not_found"));
            return true;
        }

        try {
            db.deletePassword(uuid);

            String message = plugin.getMessage("commands.player_unregistered");
            sender.sendMessage(message.replace("{player}", victim == null ? args[0] : victim.getName()));
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(plugin.getMessage("commands.errors.unregister_failed"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
