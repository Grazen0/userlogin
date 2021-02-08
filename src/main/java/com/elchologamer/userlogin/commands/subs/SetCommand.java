package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.CustomConfig;
import com.elchologamer.userlogin.util.command.SubCommand;
import com.elchologamer.userlogin.util.extensions.QuickMap;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import org.bukkit.Location;
import org.bukkit.command.Command;
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
    public boolean execute(CommandSender sender, Command command, String[] args) {
        if (!"login".equals(args[0]) && !"spawn".equals(args[0])) return false;


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

        config.set(args[0] + ".x", x);
        config.set(args[0] + ".y", y);
        config.set(args[0] + ".z", z);
        config.set(args[0] + ".yaw", yaw);
        config.set(args[0] + ".pitch", pitch);
        config.set(args[0] + ".world", world);
        locationsConfig.save();

        // Send message
        ulPlayer.sendPathMessage(
                "commands.set",
                new QuickMap<>("type", (Object) args[0])
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
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> options = new ArrayList<>();
        if (args.length == 1) {
            options.add("login");
            options.add("spawn");
        }
        return options;
    }
}
