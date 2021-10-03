package com.elchologamer.userlogin.command.sub;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.CustomConfig;
import com.elchologamer.userlogin.command.base.SubCommand;
import com.elchologamer.userlogin.util.QuickMap;
import com.elchologamer.userlogin.ULPlayer;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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
        if (!type.equals("spawn") && !type.equals("login")) return false;

        ULPlayer ulPlayer = plugin.getPlayer((Player) sender);
        Player player = ulPlayer.getPlayer();

        // Save location
        Location loc = player.getLocation();
        double x = loc.getX();
        double y = loc.getY();
        double z = loc.getZ();
        float yaw = loc.getYaw();
        float pitch = loc.getPitch();
        String world = loc.getWorld().getName();

        CustomConfig locationsConfig = plugin.getLocationsManager().getConfig();
        FileConfiguration config = locationsConfig.get();

        config.set(type + ".x", x);
        config.set(type + ".y", y);
        config.set(type + ".z", z);
        config.set(type + ".yaw", yaw);
        config.set(type + ".pitch", pitch);
        config.set(type + ".world", world);
        locationsConfig.save();

        // Send message
        ulPlayer.sendMessage(
                "commands.set",
                new QuickMap<>("type", (Object) type)
                        .set("x", (int) x)
                        .set("y", (int) y)
                        .set("z", (int) z)
                        .set("yaw", (int) yaw)
                        .set("pitch", (int) pitch)
                        .set("world", world)
        );

        return true;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) {
        List<String> options = new ArrayList<>();

        if (args.length == 1) {
            options.add("login");
            options.add("spawn");
        }

        return options;
    }
}
