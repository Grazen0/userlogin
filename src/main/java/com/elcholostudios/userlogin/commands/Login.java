package com.elcholostudios.userlogin.commands;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.util.lists.Location;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Login implements CommandExecutor, TabCompleter {

    private final Utils utils = new Utils();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            utils.sendMessage(Path.PLAYER_ONLY, sender);
            return true;
        }

        // Check if player is not already logged in
        Player player = (Player) sender;
        if (Utils.loggedIn.get(player.getUniqueId())) {
            utils.sendMessage(Path.ALREADY_LOGGED_IN, player);
            return true;
        }

        // Check if player is registered
        if (!utils.isRegistered(player)) {
            utils.sendMessage(Path.NOT_REGISTERED, player);
            return true;
        }

        // Check usage
        if (args.length != 1) return false;

        // Check if password is correct
        String uuid = player.getUniqueId().toString();
        String password = Objects.requireNonNull(
                UserLogin.dataFile.get().getString(uuid + ".password")); // Get stored password

        // Decrypt stored password if needed
        if (password.startsWith("§")) password = utils.decrypt(password);

        if (!args[0].equals(password)) {
            utils.sendMessage(Path.INCORRECT_PASSWORD, player);
            return true;
        }

        // Player logs in
        Utils.loggedIn.put(UUID.fromString(uuid), true);
        utils.cancelTimeout(player);

        utils.updateName(player);
        UserLogin.dataFile.save();

        // Send message and teleport to spawn if enabled
        utils.sendMessage(Path.LOGGED_IN, player);

        if (utils.normalMode() && utils.getConfig().getBoolean("teleports.toSpawn")) {
            player.teleport(utils.getLocation(Location.SPAWN));
        } else if (!utils.normalMode()) {
            org.bukkit.Location loc = utils.getLocation("playerLocations." + uuid);
            if(loc == null) return true;
            player.teleport(loc);
        }

        utils.joinAnnounce(player);

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
