package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.QuickMap;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.ULPlayer;
import com.elchologamer.userlogin.util.Utils;
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

public abstract class AuthCommand implements CommandExecutor, TabCompleter {

    private final UserLogin plugin;

    public AuthCommand() {
        plugin = UserLogin.getPlugin();
    }

    protected abstract boolean authenticate(Player player, String[] args);

    public UserLogin getPlugin() {
        return plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getMessage(Path.PLAYER_ONLY));
            return true;
        }

        Player p = (Player) sender;
        ULPlayer ulPlayer = plugin.getPlayer(p);

        // Check if player is already logged in
        if (ulPlayer.isLoggedIn()) {
            ulPlayer.sendPathMessage(Path.ALREADY_LOGGED_IN);
            return true;
        }

        // Authenticate player
        if (authenticate(p, args)) login(ulPlayer, plugin);
        return true;
    }

    public static void login(ULPlayer ulPlayer, UserLogin plugin) {
        Player p = ulPlayer.getPlayer();

        ulPlayer.setLoggedIn(true);
        ulPlayer.cancelTimeout();
        ulPlayer.cancelRepeatingMessage();

        // Send join message to player
        ulPlayer.sendPathMessage(Path.LOGGED_IN);

        // Teleport player
        FileConfiguration config = Utils.getConfig();
        ConfigurationSection teleports = config.getConfigurationSection("teleports");
        assert teleports != null;

        Location spawn = plugin.getLocationsManager().getLocation("spawn");

        // Send to spawn server if enabled
        if (config.getBoolean("bungeeCord.enabled")) {
            String target = config.getString("bungeeCord.spawnServer");
            ulPlayer.changeServer(target);
            return;
        }

        // Join announcement
        for (Player player : p.getServer().getOnlinePlayers()) {
            if (player.equals(p)) continue;

            ULPlayer ul = plugin.getPlayer(player);
            if (ul.isLoggedIn()) {
                ul.sendPathMessage(
                        Path.LOGIN_ANNOUNCEMENT,
                        new QuickMap<>("player", p.getName())
                );
            }
        }

        if (teleports.getBoolean("savePosition")) {
            Location loc = plugin.getLocationsManager().getPlayerLocation(p);
            p.teleport(loc);
        } else if (teleports.getBoolean("toSpawn", true)) {
            // Teleport to spawn
            p.teleport(spawn);
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return new ArrayList<>();
    }
}
