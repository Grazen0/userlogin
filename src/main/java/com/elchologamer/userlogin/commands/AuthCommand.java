package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.player.ULPlayer;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class AuthCommand implements CommandExecutor, TabCompleter {

    protected abstract boolean authenticate(Player player, String[] args);

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            Utils.sendMessage(Path.PLAYER_ONLY, sender);
            return true;
        }

        UserLogin plugin = UserLogin.getPlugin();
        Player p = (Player) sender;
        ULPlayer ulPlayer = plugin.getPlayerManager().get(p);

        // Check if player is already logged in
        if (ulPlayer.isLoggedIn()) {
            Utils.sendMessage(Path.ALREADY_LOGGED_IN, p);
            return true;
        }

        // Authenticate player
        if (authenticate(p, args)) login(ulPlayer);
        return true;
    }

    public static void login(ULPlayer ulPlayer) {
        Player p = ulPlayer.getPlayer();

        ulPlayer.setLoggedIn(true);
        ulPlayer.cancelTimeout();
        ulPlayer.cancelRepeatingMessage();

        // Send join message to player
        Utils.sendMessage(Path.LOGGED_IN, p);

        // Teleport player
        FileConfiguration config = Utils.getConfig();
        ConfigurationSection teleports = config.getConfigurationSection("teleports");
        assert teleports != null;

        Location spawn = Utils.getLocation("spawn");

        if (teleports.getBoolean("savePosition")) {
            // Teleport to either last location or spawn
            UUID uuid = p.getUniqueId();
            Location loc = Utils.getLocation(
                    "playerLocations." + uuid,
                    spawn
            );

            p.teleport(loc);
        } else if (teleports.getBoolean("toSpawn", true)) {
            // Teleport to spawn
            p.teleport(spawn);
        } else if (config.getBoolean("bungeeCord.enabled")) {
            String target = config.getString("bungeeCord.spawnServer");
            ulPlayer.changeServer(target);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}