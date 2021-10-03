package com.elchologamer.userlogin;

import com.elchologamer.userlogin.command.ChangePasswordCommand;
import com.elchologamer.userlogin.command.LoginCommand;
import com.elchologamer.userlogin.command.RegisterCommand;
import com.elchologamer.userlogin.command.base.CommandHandler;
import com.elchologamer.userlogin.command.sub.HelpCommand;
import com.elchologamer.userlogin.command.sub.ReloadCommand;
import com.elchologamer.userlogin.command.sub.SetCommand;
import com.elchologamer.userlogin.command.sub.UnregisterCommand;
import com.elchologamer.userlogin.database.Database;
import com.elchologamer.userlogin.listener.OnPlayerJoin;
import com.elchologamer.userlogin.listener.OnPlayerQuit;
import com.elchologamer.userlogin.listener.restriction.*;
import com.elchologamer.userlogin.manager.LangManager;
import com.elchologamer.userlogin.manager.LocationsManager;
import com.elchologamer.userlogin.manager.PlayerManager;
import com.elchologamer.userlogin.util.FastLoginHook;
import com.elchologamer.userlogin.util.LogFilter;
import com.elchologamer.userlogin.util.Utils;
import com.github.games647.fastlogin.bukkit.FastLoginBukkit;
import org.bstats.bukkit.Metrics;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class UserLogin extends JavaPlugin {

    private static UserLogin plugin;

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
        Utils.debug("RUNNING IN DEBUG MODE");

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        playerManager = new PlayerManager();
        langManager = new LangManager();
        locationsManager = new LocationsManager();

        langManager.load();

        // Register FastLogin hook
        if (getServer().getPluginManager().isPluginEnabled("FastLogin")) {
            FastLoginBukkit fastLogin = JavaPlugin.getPlugin(FastLoginBukkit.class);
            fastLogin.getCore().setAuthPluginHook(new FastLoginHook());

            Utils.log("FastLogin hook registered");
        }

        try {
            LogFilter.register();
        } catch (NoClassDefFoundError e) {
            Utils.log("&eFailed to register logging filter");
        }

        // Register event listeners
        new ChatRestriction().register();
        new MovementRestriction().register();
        new BlockBreakingRestriction().register();
        new BlockPlacingRestriction().register();
        new CommandRestriction().register();
        new ItemDropRestriction().register();
        new MovementRestriction().register();
        new AttackRestriction().register();
        new ReceiveDamageRestriction().register();

        new OnPlayerJoin().register();
        new OnPlayerQuit().register();

        // Register Item Pickup restriction if class exists
        try {
            Class.forName("org.bukkit.event.entity.EntityPickupItemEvent");
            new ItemPickupRestriction().register();
        } catch (ClassNotFoundException ignored) {
        }

        CommandHandler mainCommand = new CommandHandler("userlogin");
        mainCommand.add(new HelpCommand());
        mainCommand.add(new SetCommand());
        mainCommand.add(new ReloadCommand());
        mainCommand.add(new UnregisterCommand());

        // Register commands
        mainCommand.register();
        new LoginCommand().register();
        new RegisterCommand().register();
        new ChangePasswordCommand().register();

        load();

        // bStats setup
        if (!getConfig().getBoolean("debug")) {
            Metrics metrics = new Metrics(this, bStatsID);
            metrics.addCustomChart(new Metrics.SimplePie("storage_type",
                    () -> getConfig().getString("database.type", "yaml").toLowerCase())
            );
            metrics.addCustomChart(new Metrics.SimplePie("lang",
                    () -> getConfig().getString("lang", "en_US"))
            );
        }

        // Check for updates
        if (getConfig().getBoolean("checkUpdates", true)) {
            getServer().getScheduler().runTaskAsynchronously(this, this::checkUpdates);
        }

        Utils.log("%s v%s enabled", getName(), plugin.getDescription().getVersion());
    }

    public void load() {
        // Load configurations
        saveDefaultConfig();
        reloadConfig();
        locationsManager.getConfig().saveDefault();
        langManager.load();

        // Cancel all plugin tasks
        getServer().getScheduler().cancelTasks(this);
        playerManager.clear();

        // Disconnect from database
        if (db != null) {
            try {
                db.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // Start database
        db = Database.select();
        getServer().getScheduler().runTaskAsynchronously(this, this::connectDatabase);
    }

    private void connectDatabase() {
        try {
            db.connect();
            if (db.logConnected()) {
                Utils.log(langManager.getMessage("other.database_connected"));
            }
        } catch (Exception e) {
            if (e instanceof ClassNotFoundException) {
                Utils.log(langManager.getMessage("other.driver_missing").replace("{driver}", e.getMessage()));
            } else {
                Utils.log(langManager.getMessage("other.database_error"));
                e.printStackTrace();
            }
        }
    }

    private void checkUpdates() {
        String url = "https://api.spigotmc.org/legacy/update.php?resource=" + pluginID;
        String version = getDescription().getVersion();
        String latest = Utils.fetch(url);

        if (latest == null) {
            Utils.log("&cUnable to fetch latest version");
        } else if (!latest.equalsIgnoreCase(version)) {
            Utils.log("&eA new UserLogin version is available! (v%s)", latest);
        } else {
            Utils.log("&aRunning latest version! (v%s)", version);
        }
    }

    @Override
    public void onDisable() {
        try {
            if (db != null) db.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    public PlayerManager getPlayers() {
        return playerManager;
    }

    public LangManager getLang() {
        return langManager;
    }
}
