package com.elchologamer.userlogin;

import com.elchologamer.userlogin.api.command.CommandHandler;
import com.elchologamer.userlogin.api.util.Configuration;
import com.elchologamer.userlogin.commands.Login;
import com.elchologamer.userlogin.commands.Register;
import com.elchologamer.userlogin.commands.subs.Help;
import com.elchologamer.userlogin.commands.subs.Reload;
import com.elchologamer.userlogin.commands.subs.SQL;
import com.elchologamer.userlogin.commands.subs.Set;
import com.elchologamer.userlogin.listeners.*;
import com.elchologamer.userlogin.util.Lang;
import com.elchologamer.userlogin.util.Metrics;
import com.elchologamer.userlogin.util.MySQL;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.files.DataFile;
import com.elchologamer.userlogin.util.files.LocationsFile;
import com.elchologamer.userlogin.util.files.MessagesFile;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;

public final class UserLogin extends JavaPlugin {

    public static final Configuration messagesFile = new MessagesFile();
    public static final Configuration locationsFile = new LocationsFile();
    public static final Configuration dataFile = new DataFile();
    public static final MySQL sql = new MySQL();
    private static UserLogin plugin;

    private static void setUsage(@NotNull String command, @NotNull String path) {
        Objects.requireNonNull(plugin.getCommand(command)).setUsage(Utils.color(
                Objects.requireNonNull(messagesFile.get().getString(path))));
    }

    public static UserLogin getPlugin() {
        return plugin;
    }

    public void pluginSetup() {
        // Add default configuration
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        // Create default language files
        Lang.createDefaultLang();

        // Set up configurations
        messagesFile.setup();
        locationsFile.setup();
        dataFile.setup();

        // Set usages for commands
        setUsage("userlogin", Path.USERLOGIN_USAGE);
        setUsage("login", Path.LOGIN_USAGE);
        setUsage("register", Path.REGISTER_USAGE);

        // Cancel all plugin tasks
        plugin.getServer().getScheduler().cancelTasks(plugin);
        Utils.playerIP.clear();
        Utils.timeouts.clear();

        if (!Utils.sqlMode()) {
            // Update passwords (Encrypt or decrypt each one of them if needed)
            Utils.updatePasswords(plugin.getConfig().getBoolean("password.encrypt"));
        } else {
            plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                try {
                    // Close current connection
                    if (sql.getConnection() != null && !sql.getConnection().isClosed())
                        sql.getConnection().close();

                    // Connect to MySQL database
                    sql.connect();

                    // Schedule data saving repeating task
                    long delay = plugin.getConfig().getLong("mysql.saveInterval") * 20;
                    UserLogin.plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, sql::saveData, delay, delay);

                    Utils.log(Utils.color(messagesFile.get().getString(Path.SQL_CONNECTION_SUCCESS)));
                } catch (@NotNull SQLException | ClassNotFoundException e) {
                    Utils.log(Utils.color(messagesFile.get().getString(Path.SQL_CONNECTION_ERROR)));
                    Utils.log(ChatColor.DARK_RED + e.getMessage());
                }
            });
        }
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Create BungeeCord messaging channel
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Register event listeners
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerMove(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new ReloadListener(), this);
        getServer().getPluginManager().registerEvents(new GeneralListener(), this);

        // Set CommandHandler for "userlogin" command
        CommandHandler handler = new CommandHandler("userlogin", this);

        handler.addCommand(new Help());
        handler.addCommand(new Set());
        handler.addCommand(new Reload());
        handler.addCommand(new SQL());

        // Set command executors
        Objects.requireNonNull(getCommand("login")).setExecutor(new Login());
        Objects.requireNonNull(getCommand("register")).setExecutor(new Register());

        // General plugin setup
        pluginSetup();

        // bStats setup
        Metrics metrics = new Metrics(this, 8586);
        metrics.addCustomChart(new Metrics.SimplePie("data_type",
                () -> Utils.sqlMode() ? "MySQL" : "YAML"));
        metrics.addCustomChart(new Metrics.SimplePie("lang",
                () -> getConfig().getString("lang")));

        // Check for new versions
        String version = getDescription().getVersion();
        if (getConfig().getBoolean("checkUpdates", true)) {
            Utils.log(ChatColor.BLUE + "Checking for updates...");
            String latest = Utils.fetch("https://api.spigotmc.org/legacy/update.php?resource=80669");
            if (latest != null) {
                if (!latest.equalsIgnoreCase(version))
                    Utils.log(ChatColor.YELLOW + "A new UserLogin version is available! (v" + latest + ")");
                else
                    Utils.log(ChatColor.GREEN + "Running latest version!");
            } else {
                Utils.log(ChatColor.RED + "Unable to get latest version");
            }
        }

        Utils.log(ChatColor.GREEN + "UserLogin v" + version + " enabled!");
    }

    @Override
    public synchronized void onDisable() {
        if (!Utils.sqlMode()) return;

        // Save data to MySQL database
        sql.saveData();
        Utils.log(Utils.color(messagesFile.get().getString(Path.SQL_DATA_SAVED)));
    }
}
