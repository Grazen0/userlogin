package com.elchologamer.userlogin.commands;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import com.elchologamer.userlogin.api.event.AuthenticationEvent;
import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.util.command.BaseCommand;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import com.elchologamer.userlogin.util.managers.LocationsManager;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public abstract class AuthCommand extends BaseCommand {

    private final AuthType type;
    private final int minArgs;
    private final UserLogin plugin = UserLogin.getPlugin();

    public AuthCommand(String name, AuthType type) {
        this(name, type, 0);
    }

    public AuthCommand(String name, AuthType type, int minArgs) {
        super(name, true);
        this.type = type;
        this.minArgs = minArgs;
    }

    protected abstract boolean authenticate(ULPlayer player, String[] args);

    public UserLogin getPlugin() {
        return plugin;
    }

    @Override
    public final boolean execute(CommandSender sender, Command command, String label, String[] args) {
        ULPlayer ulPlayer = plugin.getPlayer((Player) sender);

        // Check if player is already logged in
        if (ulPlayer.isLoggedIn()) {
            ulPlayer.sendPathMessage("messages.already_logged_in");
            return true;
        }

        // Check usage
        if (args.length < minArgs) return false;

        // Authenticate player
        if (authenticate(ulPlayer, args)) login(ulPlayer, type);

        return true;
    }

    public static void login(ULPlayer ulPlayer, AuthType type) {
        UserLogin plugin = UserLogin.getPlugin();
        Player p = ulPlayer.getPlayer();

        // Teleport player
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection teleports = config.getConfigurationSection("teleports");
        assert teleports != null;

        // Call event
        AuthenticationEvent event;
        boolean bungeeEnabled = config.getBoolean("bungeeCord.enabled");

        if (bungeeEnabled) {
            String target = config.getString("bungeeCord.spawnServer");
            event = new AuthenticationEvent(p, type, target);
        } else {
            Location target = null;
            LocationsManager manager = plugin.getLocationsManager();

            if (teleports.getBoolean("savePosition")) {
                target = manager.getPlayerLocation(p);
            } else if (teleports.getBoolean("toSpawn", true)) {
                target = manager.getLocation("spawn");
            }

            event = new AuthenticationEvent(p, type, target);
        }

        plugin.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;

        ulPlayer.cancelTimeout();
        ulPlayer.cancelRepeatingMessage();

        // Send message
        String message = event.getMessage();
        if (message != null) p.sendMessage(message);

        // Join announcement
        String announcement = event.getAnnouncement();
        if (announcement != null) {
            for (Player player : p.getServer().getOnlinePlayers()) {
                if (UserLoginAPI.isLoggedIn(player)) {
                    player.sendMessage(announcement);
                }
            }
        }

        ulPlayer.setLoggedIn(true);

        // Teleport to destination
        Location targetLoc = event.getDestination();
        String targetServer = event.getTargetServer();

        if (bungeeEnabled && targetServer != null) {
            ulPlayer.changeServer(targetServer);
        } else if (targetLoc != null) {
            p.teleport(targetLoc);
        }
    }
}
