package com.elchologamer.userlogin;

import com.elchologamer.userlogin.commands.LoginCommand;
import com.elchologamer.userlogin.commands.RegisterCommand;
import com.elchologamer.userlogin.commands.subs.HelpCommand;
import com.elchologamer.userlogin.commands.subs.ReloadCommand;
import com.elchologamer.userlogin.commands.subs.SetCommand;
import com.elchologamer.userlogin.commands.subs.UnregisterCommand;
import com.elchologamer.userlogin.listeners.OnPlayerJoin;
import com.elchologamer.userlogin.listeners.OnPlayerQuit;
import com.elchologamer.userlogin.listeners.restrictions.BlockBreakRestriction;
import com.elchologamer.userlogin.listeners.restrictions.ChatRestriction;
import com.elchologamer.userlogin.listeners.restrictions.CommandRestriction;
import com.elchologamer.userlogin.listeners.restrictions.ItemDropRestriction;
import com.elchologamer.userlogin.listeners.restrictions.ItemPickupRestriction;
import com.elchologamer.userlogin.listeners.restrictions.MovementRestriction;
import com.elchologamer.userlogin.util.Metrics;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.command.SubCommandHandler;
import com.elchologamer.userlogin.util.database.Database;
import com.elchologamer.userlogin.util.extensions.ULPlayer;
import com.elchologamer.userlogin.util.managers.LangManager;
import com.elchologamer.userlogin.util.managers.LocationsManager;
import com.elchologamer.userlogin.util.managers.PlayerManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class UserLogin extends JavaPlugin {

    private static UserLogin plugin;
    private SubCommandHandler mainCommand;

    private PlayerManager playerManager;
    private LangManager langManager;
    private LocationsManager locationsManager;

    private final int pluginID = 80669;
    private final int bStatsID = 8586;

    private Database db = null;

    public static UserLogin getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Initialize managers
        playerManager = new PlayerManager();
        langManager = new LangManager();
        locationsManager = new LocationsManager();

        // Plugin messaging setup
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Register event listeners
        new ChatRestriction().register();
        new MovementRestriction().register();
        new BlockBreakRestriction().register();
        new CommandRestriction().register();
        new ItemDropRestriction().register();
        new MovementRestriction().register();

        new OnPlayerJoin().register();
        new OnPlayerQuit().register();

        // Register Item Pickup restriction if class exists
        try {
            Class.forName("org.bukkit.event.entity.EntityPickupItemEvent");
            registerEvent(new ItemPickupRestriction());
        } catch (ClassNotFoundException ignored) {
        }

        mainCommand = new SubCommandHandler("userlogin");

        // Register sub-commands
        mainCommand.add(new HelpCommand());
        mainCommand.add(new SetCommand());
        mainCommand.add(new ReloadCommand());
        mainCommand.add(new UnregisterCommand());

        // Register commands
        mainCommand.register();
        new LoginCommand().register();
        new RegisterCommand().register();

        load();

        // bStats setup
        Metrics metrics = new Metrics(this, bStatsID);
        metrics.addCustomChart(new Metrics.SimplePie("data_type",
                () -> getConfig().getString("database.type", "yaml").toLowerCase())
        );
        metrics.addCustomChart(new Metrics.SimplePie("lang",
                () -> getConfig().getString("lang", "en_US"))
        );


        // Check for new versions
        PluginDescriptionFile desc = getDescription();
        String version = desc.getVersion();
        String name = desc.getName();

        if (getConfig().getBoolean("checkUpdates", true)) {
            getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                String url = "https://api.spigotmc.org/legacy/update.php?resource=" + pluginID;
                String latest = Utils.fetch(url);

                if (latest == null) {
                    Utils.log("&cUnable to get latest version");
                    return;
                }

                if (!latest.equalsIgnoreCase(version)) {
                    Utils.log("&eA new UserLogin version is available! (v" + latest + ")");
                } else {
                    Utils.log("&aRunning latest version! (v" + version + ")");
                }
            });
        }

        Utils.log("&a" + name + " v" + version + " enabled!");
    }

    public void load() {
        // Load configurations
        saveDefaultConfig();
        reloadConfig();
        locationsManager.getConfig().saveDefault();
        langManager.load();

        // Cancel all plugin tasks
        getServer().getScheduler().cancelTasks(plugin);
        playerManager.clear();

        db = Database.select(); // Select database

        getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
            try {
                db.disconnect();
                db.connect();
            } catch (ClassNotFoundException e) {
                Utils.log("&dJDBC Driver database not found: " + e.getCause());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDisable() {
        try {
            if (db != null) db.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void registerEvent(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public FileConfiguration getMessages() {
        return langManager.getMessages();
    }

    public String getMessage(String path, String def) {
        FileConfiguration config = getMessages();

        String message = config.getString(path);
        return message == null ? def : Utils.color(message);
    }

    public String getMessage(String path) {
        return getMessage(path, null);
    }

    public ULPlayer getPlayer(Player player) {
        return playerManager.get(player);
    }

    public Database getDB() {
        return db;
    }

    public LocationsManager getLocationsManager() {
        return locationsManager;
    }

    public PlayerManager getPlayerManager() {
        return playerManager;
    }

}
