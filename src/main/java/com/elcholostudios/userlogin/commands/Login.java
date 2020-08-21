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
import java.util.Objects;
import java.util.UUID;

public class Login implements CommandExecutor, TabCompleter {

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

        // Check if player is registered
        if (!utils.isRegistered(player)) {
            utils.sendMessage(Path.NOT_REGISTERED, player);
            return true;
        }

        // Check usage
        if (args.length != 1) return false;

        // Check if password is correct
        String uuid = player.getUniqueId().toString();

        // Get stored password
        String password;
        if (!utils.sqlMode())
            password = Objects.requireNonNull(UserLogin.dataFile.get().getString(uuid + ".password"));
        else
            password = UserLogin.sql.data.get(UUID.fromString(uuid));

        // Decrypt stored password if needed
        if (password.startsWith("§"))
            password = utils.decrypt(password);

        if (!args[0].equals(password)) {
            utils.sendMessage(Path.INCORRECT_PASSWORD, player);
            return true;
        }

        // Call login event
        PlayerLoginEvent event = new PlayerLoginEvent(player, LoginType.LOGIN);
        UserLogin.plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled())
            return true;

        // Player logs in
        Utils.loggedIn.put(UUID.fromString(uuid), true);
        utils.cancelTimeout(player);

        utils.updateName(player);
        UserLogin.dataFile.save();

        // Send join message to player
        if (event.getMessage() != null)
            player.sendMessage(event.getMessage());
        // Teleport player
        if (event.getDestinationType() == DestinationType.NORMAL) {
            if (event.getDestinationLoc() != null)
                player.teleport(event.getDestinationLoc());

            utils.joinAnnounce(player, event.getAnnouncement());
            return true;
        }

        // Connect to spawn server
        if (event.getDestinationServer() != null)
            utils.sendToServer(player, event.getDestinationServer());
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
