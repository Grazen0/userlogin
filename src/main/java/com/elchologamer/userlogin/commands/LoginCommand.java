package com.elchologamer.userlogin.commands;

import com.elchologamer.pluginapi.util.command.SpigotCommand;
import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.api.enums.DestinationType;
import com.elchologamer.userlogin.api.enums.LoginType;
import com.elchologamer.userlogin.api.event.PlayerLoginEvent;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LoginCommand extends SpigotCommand {

    private final UserLogin plugin;

    public LoginCommand(UserLogin plugin) {
        super("login");
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            Utils.sendMessage(Path.PLAYER_ONLY, sender);
            return true;
        }

        // Check if player is not already logged in
        Player player = (Player) sender;
        if (UserLoginAPI.isLoggedIn(player)) {
            Utils.sendMessage(Path.ALREADY_LOGGED_IN, player);
            return true;
        }

        // Check if player is registered
        if (!UserLoginAPI.isRegistered(player)) {
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
            password = plugin.getPlayerData().get().getString(uuid + ".password");
        else
            password = UserLogin.getPlugin().getSQL().getData().get(UUID.fromString(uuid));

        // Decrypt stored password if needed
        password = Utils.decrypt(password);

        if (!args[0].equals(password)) {
            Utils.sendMessage(Path.INCORRECT_PASSWORD, player);
            return true;
        }

        login(player);
        return true;
    }

    public void login(Player player) {
        // Call login event
        PlayerLoginEvent event = new PlayerLoginEvent(player, LoginType.LOGIN);
        plugin.getServer().getPluginManager().callEvent(event);

        if (event.isCancelled())
            return;

        // Player logs in
        Utils.changeLoggedIn(player, true);
        Utils.cancelTimeout(player);

        // Update name if MySQL mode is disabled
        if (!Utils.sqlMode()) {
            Utils.updateName(player);
            plugin.getPlayerData().save();
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

        // Connect to target server
        if (event.getDestinationServer() != null)
            Utils.sendToServer(player, event.getDestinationServer());
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
