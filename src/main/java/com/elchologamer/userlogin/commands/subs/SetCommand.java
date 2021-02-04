package com.elchologamer.userlogin.commands.subs;

import com.elchologamer.userlogin.api.CustomConfig;
import com.elchologamer.userlogin.api.command.SubCommand;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SetCommand extends SubCommand {

    private final CustomConfig file;

    public SetCommand(CustomConfig locationsConfig) {
        super("set");
        this.file = locationsConfig;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length != 1 || !(args[0].equals("login") || args[0].equals("spawn")))
            return false;

        // Save location
        Player player = (Player) sender;
        Location loc = player.getLocation();
        int x = (int) Math.round(loc.getX());
        int y = (int) Math.round(loc.getY());
        int z = (int) Math.round(loc.getZ());
        float yaw = Math.round(loc.getYaw());
        float pitch = Math.round(loc.getPitch());
        String world = Objects.requireNonNull(loc.getWorld()).getName();

        file.get().set(args[0] + ".x", x);
        file.get().set(args[0] + ".y", y);
        file.get().set(args[0] + ".z", z);
        file.get().set(args[0] + ".yaw", yaw);
        file.get().set(args[0] + ".pitch", pitch);
        file.get().set(args[0] + ".world", world);
        file.save();

        // Send message
        Utils.sendMessage(Path.SET, player, new String[]{"type", "x", "y", "z", "yaw", "pitch", "world"},
                new String[]{args[0], Integer.toString(x), Integer.toString(y), Integer.toString(z),
                        Float.toString(yaw), Float.toString(pitch), world});
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
