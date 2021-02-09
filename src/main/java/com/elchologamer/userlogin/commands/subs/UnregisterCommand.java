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
        UUID uuid = null;

        try {
            uuid = victim == null ? getUuidFromAPI(args[0]) : victim.getUniqueId();
        } catch (UnsupportedEncodingException ignored) {
        }

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

    private UUID getUuidFromAPI(String name) throws UnsupportedEncodingException {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, "UTF-8");
        String res = Utils.fetch(url);
        if (res == null) return null;

        JsonElement base = new JsonParser().parse(res);
        if (base == null || base.isJsonNull()) return null;

        JsonObject data = base.getAsJsonObject();
        if (!data.has("id")) return null;

        // Found this solution here:
        // https://stackoverflow.com/questions/18986712/creating-a-uuid-from-a-string-with-no-dashes
        String uuidString = data.get("id").getAsString()
                .replaceFirst(
                        "(\\p{XDigit}{8})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}{4})(\\p{XDigit}+)",
                        "$1-$2-$3-$4-$5"
                );

        return UUID.fromString(uuidString);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
