package com.elchologamer.userlogin.command.sub;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.command.base.SubCommand;
import com.elchologamer.userlogin.util.QuickMap;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SetCommand extends SubCommand {

    private final UserLogin plugin = UserLogin.getPlugin();

    public SetCommand() {
        super("set", true, "ul.set");
    }

    @Override
    public boolean run(CommandSender sender, String[] args) {
        if (args.length == 0) return false;

        String type = args[0].toLowerCase();
        if (!type.equals("spawn") && !type.equals("login") && !type.equals("prelogin") && !type.equals("postlogin"))
            return false;

        if (type.equals("prelogin")) {
            type = "login";
        } else if (type.equals("postlogin")) {
            type = "spawn";
        }

        ULPlayer ulPlayer = ULPlayer.get((Player) sender);
        Player player = ulPlayer.getPlayer();

        // Save location
        Location loc = player.getLocation();
        plugin.getLocations().saveLocation(type, loc);

        // Send message
        ulPlayer.sendMessage(
                "commands.set",
                new QuickMap<>("type", (Object) args[0].toLowerCase())
                        .set("x", (int) loc.getX())
                        .set("y", (int) loc.getY())
                        .set("z", (int) loc.getZ())
                        .set("yaw", (int) loc.getYaw())
                        .set("pitch", (int) loc.getPitch())
                        .set("world", loc.getWorld().getName())
        );

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.add("prelogin");
            options.add("postlogin");
        }

        return options;
    }
}