package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.event.PlayerLoginEvent;
import com.elchologamer.userlogin.api.event.enums.DestinationType;
import com.elchologamer.userlogin.api.event.enums.LoginType;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.lists.Path;
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

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            Utils.sendMessage(Path.PLAYER_ONLY, sender);
            return true;
        }

        // Check if player is not already logged in
        Player player = (Player) sender;
        try {
            if (Utils.loggedIn.get(player.getUniqueId())) {
                Utils.sendMessage(Path.ALREADY_LOGGED_IN, player);
                return true;
            }
        } catch (NullPointerException ignored) {
            return true;
        }

        // Check if player is registered
        if (!Utils.isRegistered(player)) {
            Utils.sendMessage(Path.NOT_REGISTERED, player);
            return true;
        }

        // Check usage
        if (args.length != 1) return false;

        // Check if password is correct
        String uuid = player.getUniqueId().toString();

        // Get stored password
        String password;
        if (!Utils.sqlMode())
            password = Objects.requireNonNull(UserLogin.dataFile.get().getString(uuid + ".password"));
        else
            password = UserLogin.sql.data.get(UUID.fromString(uuid));

        // Decrypt stored password if needed
        if (password.startsWith("§"))
            password = Utils.decrypt(password);

        if (!args[0].equals(password)) {
            Utils.sendMessage(Path.INCORRECT_PASSWORD, player);
            return true;
        }

        this.login(player);
        return true;
    }

    public void login(@NotNull Player player) {
        // Call login event
        PlayerLoginEvent event = new PlayerLoginEvent(player, LoginType.LOGIN);
        UserLogin.getPlugin().getServer().getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        // Player logs in
        Utils.loggedIn.put(player.getUniqueId(), true);
        Utils.cancelTimeout(player);

        // Update name if MySQL mode is disabled
        if (!Utils.sqlMode()) {
            Utils.updateName(player);
            UserLogin.dataFile.save();
        }

        // Send join message to player
        if (event.getMessage() != null)
            player.sendMessage(event.getMessage());

        // Teleport player
        if (event.getDestinationType() == DestinationType.NORMAL) {
            if (event.getDestinationLoc() != null)
                player.teleport(event.getDestinationLoc());

            Utils.joinAnnounce(player, event.getAnnouncement());
            return;
        }

        // Connect to spawn server
        if (event.getDestinationServer() != null)
            Utils.sendToServer(player, event.getDestinationServer());
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return new ArrayList<>();
    }
}
