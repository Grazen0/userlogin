package com.elcholostudios.userlogin.commands;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.api.event.PlayerLoginEvent;
import com.elcholostudios.userlogin.api.event.enums.DestinationType;
import com.elcholostudios.userlogin.api.event.enums.LoginType;
import com.elcholostudios.userlogin.util.Utils;
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
import java.util.UUID;

public class Register implements CommandExecutor, TabCompleter {

    private final Utils utils = new Utils();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            utils.sendMessage(Path.PLAYER_ONLY, sender);
            return true;
        }

        // Check if player is not already logged in
        Player player = (Player) sender;
        try {
            if (Utils.loggedIn.get(player.getUniqueId())) {
                utils.sendMessage(Path.ALREADY_LOGGED_IN, player);
                return true;
            }
        } catch (NullPointerException ignored) {
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
        int minChars = utils.getConfig().getInt("password.minCharacters");
        if (password.length() < minChars) {
            utils.sendMessage(Path.SHORT_PASSWORD, player, new String[]{"chars"}, new String[]{Integer.toString(minChars)});
            return true;
        }

        // Check if both passwords match
        if (!password.equals(args[1])) {
            utils.sendMessage(Path.DIFFERENT_PASSWORDS, player);
            return true;
        }

        // Call register event
        PlayerLoginEvent event = new PlayerLoginEvent(player, LoginType.REGISTER);

        if (utils.normalMode() && !utils.getConfig().getBoolean("teleports.toSpawn"))
            event.setDestinationLoc(null);

        UserLogin.plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled())
            return true;

        // Player gets registered
        String uuid = player.getUniqueId().toString();

        Utils.loggedIn.put(UUID.fromString(uuid), true);
        utils.cancelTimeout(player);

        // Encrypt password if enabled
        if (utils.getConfig().getBoolean("password.encrypt")) password = utils.encrypt(password);

        if (!utils.sqlMode()) {
            UserLogin.dataFile.get().set(uuid + ".password", password);

            // Set name and save file
            utils.updateName(player);
            UserLogin.dataFile.save();
        } else
            UserLogin.sql.data.put(UUID.fromString(uuid), password);

        // Send message, cancel timeout, and teleport to spawn if enabled
        if (event.getMessage() != null)
            player.sendMessage(event.getMessage());

        if (event.getDestinationType() == DestinationType.NORMAL) {
            // Send to spawn if enabled
            if (event.getDestinationLoc() != null)
                player.teleport(event.getDestinationLoc());

            // Announce join message to other players
            utils.joinAnnounce(player, event.getAnnouncement());
            return true;
        }

        // Connect player to spawn server
        utils.sendToServer(player, "");
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
