package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.command.SubCommand;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.database.Database;
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

    public UnregisterCommand() {
        super("unregister");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1) return false;

        Database db = UserLogin.getPlugin().getDB();

        // Try getting player directly from server
        Player victim = sender.getServer().getPlayer(args[0]);
        UUID uuid = null;

        try {
            uuid = victim == null ? getUuidFromAPI(args[0]) : victim.getUniqueId();
        } catch (UnsupportedEncodingException ignored) {
        }

        if (uuid == null || db.getPassword(uuid) == null) {
            Utils.sendMessage(Path.PLAYER_NOT_FOUND, sender);
            return true;
        }

        try {
            db.deletePassword(uuid);
        } catch (Exception e) {
            e.printStackTrace();
            sender.sendMessage(Utils.color("&dError deleting password. Read console for more info"));
        }

        Utils.sendMessage(Path.PLAYER_UNREGISTERED, sender, new String[]{"player"}, new String[]{args[0]});
        return true;
    }

    private UUID getUuidFromAPI(String name) throws UnsupportedEncodingException {
        String url = "https://api.mojang.com/users/profiles/minecraft/" + URLEncoder.encode(name, "UTF-8");
        String res = Utils.fetch(url);
        if (res == null) return null;

        JsonObject data = new JsonParser().parse(res).getAsJsonObject();
        if (!data.has("id")) return null;

        return UUID.fromString(data.get("id").getAsString());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
