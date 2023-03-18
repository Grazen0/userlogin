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
import com.elchologamer.userlogin.listener.JoinQuitListener;
import com.elchologamer.userlogin.listener.PluginMsgListener;
import com.elchologamer.userlogin.listener.restriction.*;
import com.elchologamer.userlogin.manager.LangManager;
import com.elchologamer.userlogin.manager.LocationsManager;
import com.elchologamer.userlogin.util.FastLoginHook;
import com.elchologamer.userlogin.util.LogFilter;
import com.elchologamer.userlogin.util.Metrics;
import com.elchologamer.userlogin.util.Metrics.SimplePie;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public final class UserLogin extends JavaPlugin {

    private static UserLogin plugin;

    public final static int PLUGIN_ID = 80669;
    public final static int BSTATS_ID = 8586;

    private LangManager lang;
    private LocationsManager locationsManager;
    private Database db = null;

    @Override
    public void onEnable() {
        plugin = this;

        Utils.debug("RUNNING IN DEBUG MODE");

        // Must be loaded on enable as they get the plugin instance when initialized
        locationsManager = new LocationsManager();
        lang = new LangManager();

        reloadPlugin();

        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Try to find LuckPerms
        tryLoadLuckPerms();

        // Register FastLogin hook
        if (getServer().getPluginManager().isPluginEnabled("FastLogin")) {
            new FastLoginHook().register();
            Utils.log("FastLogin hook registered");
        }

        try {
            LogFilter.register();
        } catch (NoClassDefFoundError e) {
            Utils.log("&eFailed to register logging filter");
        }

        // Register event listeners
        if (getConfig().getBoolean("bungeecord.autoLogin")) {
            PluginMsgListener listener = new PluginMsgListener();
            getServer().getMessenger().registerIncomingPluginChannel(this, "userlogin:returned", listener);
            registerEvents(listener);
            Utils.log("Autologin enabled");
        } else {
            registerEvents(new JoinQuitListener());
        }

        registerEvents(new ChatRestriction());
        registerEvents(new MovementRestriction());
        registerEvents(new BlockBreakingRestriction());
        registerEvents(new BlockPlacingRestriction());
        registerEvents(new CommandRestriction());
        registerEvents(new ItemDropRestriction());
        registerEvents(new AttackRestriction());
        registerEvents(new ReceiveDamageRestriction());
        registerEvents(new InventoryClickRestriction());

        // Register Item Pickup restriction if class exists
        try {
            Class.forName("org.bukkit.event.entity.EntityPickupItemEvent");
            getServer().getPluginManager().registerEvents(new ItemPickupRestriction(), this);
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

        // bStats setup
        if (!getConfig().getBoolean("debug")) {
            Metrics metrics = new Metrics(this, BSTATS_ID);

            metrics.addCustomChart(new SimplePie("storage_type", () -> getConfig().getString("database.type", "yaml").toLowerCase()));
            metrics.addCustomChart(new SimplePie("lang", () -> getConfig().getString("lang", "en_US")));
        }

        // Check for updates
        if (getConfig().getBoolean("checkUpdates", true)) {
            getServer().getScheduler().runTaskAsynchronously(this, this::checkUpdates);
        }

        Utils.log(getName() + " v" + getDescription().getVersion() + " enabled");
    }

    public void reloadPlugin() {
        // Load configurations
        saveDefaultConfig();
        reloadConfig();

        locationsManager.reload();
        lang.reload();

        // Cancel all plugin tasks
        getServer().getScheduler().cancelTasks(this);

        // Start database
        if (db != null) {
            try {
                db.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        db = Database.select();
        getServer().getScheduler().runTaskAsynchronously(this, this::connectDatabase);
    }

    private void connectDatabase() {
        try {
            db.connect();

            if (db.shouldLogConnected()) {
                Utils.log(lang.getMessage("other.database_connected"));
            }
        } catch (Exception e) {
            String log = e instanceof ClassNotFoundException ?
                    lang.getMessage("other.driver_missing").replace("{driver}", e.getMessage()) :
                    lang.getMessage("other.database_error");

            if (log != null) Utils.log(log);
            e.printStackTrace();
        }
    }

    private void checkUpdates() {
        String latest = Utils.fetch("https://api.spigotmc.org/legacy/update.php?resource=" + PLUGIN_ID);
        String current = getDescription().getVersion();

        if (latest == null) {
            Utils.log("&cUnable to fetch latest version");
        } else if (!latest.equalsIgnoreCase(current)) {
            Utils.log("&eA new UserLogin version is available! (v" + latest + ")");
        } else {
            Utils.log("&aRunning latest version! (v" + current + ")");
        }
    }

    private void registerEvents(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    private void tryLoadLuckPerms() {
        try {
            Class.forName("net.luckperms.api.LuckPerms");
        } catch (ClassNotFoundException e) {
            return;
        }

        com.elchologamer.userlogin.util.LuckPermsContext.registerLuckPermsContext(plugin);
    }

    @Override
    public void onDisable() {
        if (getConfig().getBoolean("teleports.savePosition")) {
            for (Player player : getServer().getOnlinePlayers()) {
                if (ULPlayer.get(player).isLoggedIn()) {
                    locationsManager.savePlayerLocation(player);
                }
            }
        }

        if (db != null) {
            try {
                db.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static UserLogin getPlugin() {
        return plugin;
    }

    public Database getDB() {
        return db;
    }

    public LangManager getLang() {
        return lang;
    }

    public LocationsManager getLocations() {
        return locationsManager;
    }
}