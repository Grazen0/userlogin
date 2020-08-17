package com.elcholostudios.userlogin.commands.subs;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.lists.Path;
import com.elcholostudios.userlogin.util.command.SubCommand;
import com.elcholostudios.userlogin.util.Utils;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Set extends SubCommand {

    private final Utils utils = new Utils();

    public Set() {
        super("set", true);
        this.addSub("spawn");
        this.addSub("login");
    }

    @Override
    public boolean run(@NotNull CommandSender sender, String[] args) {
        if (args.length != 1 || !(args[0].equals("login") || args[0].equals("spawn"))) return false;

        // Save location
        Player player = (Player) sender;
        Location loc = player.getLocation();
        int x = (int) Math.round(loc.getX());
        int y = (int) Math.round(loc.getY());
        int z = (int) Math.round(loc.getZ());
        float yaw = Math.round(loc.getYaw());
        float pitch = Math.round(loc.getPitch());
        String world = Objects.requireNonNull(loc.getWorld()).getName();

        UserLogin.locationsFile.get().set(args[0] + ".x", x);
        UserLogin.locationsFile.get().set(args[0] + ".y", y);
        UserLogin.locationsFile.get().set(args[0] + ".z", z);
        UserLogin.locationsFile.get().set(args[0] + ".yaw", yaw);
        UserLogin.locationsFile.get().set(args[0] + ".pitch", pitch);
        UserLogin.locationsFile.get().set(args[0] + ".world", world);
        UserLogin.locationsFile.save();

        // Send message
        utils.sendMessage(Path.SET, player, new String[]{"type", "x", "y", "z", "yaw", "pitch", "world"},
                new String[]{args[0], Integer.toString(x), Integer.toString(y), Integer.toString(z),
                        Float.toString(yaw), Float.toString(pitch), world});
        return true;
    }
}
