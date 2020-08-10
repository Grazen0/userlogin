package com.elcholostudios.userlogin.commands;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.lists.Location;
import com.elcholostudios.userlogin.util.lists.Path;
import com.elcholostudios.userlogin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Register implements CommandExecutor, TabCompleter {

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

        // Check if player is not already registered
        if (utils.isRegistered(player)) {
            utils.sendMessage(Path.ALREADY_REGISTERED, player);
            return true;
        }

        // Check usage
        if (args.length != 2) return false;

        // Check if password is over the minimum amount of characters
        String password = args[0];
        int minChars = UserLogin.plugin.getConfig().getInt("password.minCharacters");
        if (password.length() < minChars) {
            utils.sendMessage(Path.SHORT_PASSWORD, player, new String[]{"chars"}, new String[]{Integer.toString(minChars)});
            return true;
        }

        // Check if both passwords match
        if (!password.equals(args[1])) {
            utils.sendMessage(Path.DIFFERENT_PASSWORDS, player);
            return true;
        }

        // Player gets registered
        String uuid = player.getUniqueId().toString();
        Utils.loggedIn.put(UUID.fromString(uuid), true);

        // Save password, encrypted if enabled
        if (UserLogin.plugin.getConfig().getBoolean("password.encrypt")) password = utils.encrypt(password);
        UserLogin.dataFile.get().set(uuid + ".password", password);

        // Set name and save file
        utils.updateName(player);
        UserLogin.dataFile.save();

        // Send message, cancel timeout, and teleport to spawn if enabled
        utils.sendMessage(Path.REGISTERED, player);
        utils.cancelTimeout(player);
        if (UserLogin.plugin.getConfig().getBoolean("teleports.toSpawn"))
            player.teleport(utils.getLocation(Location.SPAWN));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
