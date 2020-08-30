package com.elchologamer.userlogin;

import com.elchologamer.userlogin.api.command.CommandHandler;
import com.elchologamer.userlogin.api.util.Configuration;
import com.elchologamer.userlogin.commands.Login;
import com.elchologamer.userlogin.commands.Register;
import com.elchologamer.userlogin.commands.subs.Help;
import com.elchologamer.userlogin.commands.subs.Reload;
import com.elchologamer.userlogin.commands.subs.SQL;
import com.elchologamer.userlogin.commands.subs.Set;
import com.elchologamer.userlogin.util.files.DataFile;
import com.elchologamer.userlogin.util.files.LocationsFile;
import com.elchologamer.userlogin.util.files.MessagesFile;
import com.elchologamer.userlogin.listeners.*;
import com.elchologamer.userlogin.util.Lang;
import com.elchologamer.userlogin.util.Metrics;
import com.elchologamer.userlogin.util.Utils;
import com.elchologamer.userlogin.util.data.MySQL;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.Bukkit;
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
    public static UserLogin plugin;
    private final Utils utils = new Utils();

    public static void pluginSetup() {
        Utils utils = new Utils();

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

        if (!utils.sqlMode()) {
            // Update passwords (Encrypt or decrypt each one of them if needed)
            utils.updatePasswords(plugin.getConfig().getBoolean("password.encrypt"));
        } else {
            try {
                // Connect to MySQL database
                sql.connect();

                // Schedule data saving repeating task
                long delay = plugin.getConfig().getLong("mysql.saveInterval") * 20;
                Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, sql::saveData, delay, delay);

                utils.consoleLog(utils.color(Objects.requireNonNull(utils.getConfig().getString(Path.SQL_CONNECTION_SUCCESS))));
            } catch (@NotNull SQLException | ClassNotFoundException e) {
                utils.consoleLog(utils.color(Objects.requireNonNull(
                        utils.getConfig().getString(Path.SQL_CONNECTION_ERROR))));

                e.printStackTrace();
            }
        }
    }

    private static void setUsage(@NotNull String command, @NotNull String path) {
        Utils utils = new Utils();
        Objects.requireNonNull(plugin.getCommand(command)).setUsage(utils.color(
                Objects.requireNonNull(messagesFile.get().getString(path))));
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Create BungeeCord messaging channel
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Register event listeners
        this.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerMove(), this);
        this.getServer().getPluginManager().registerEvents(new OnPlayerQuit(), this);
        this.getServer().getPluginManager().registerEvents(new ReloadListener(), this);
        this.getServer().getPluginManager().registerEvents(new GeneralListener(), this);

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
                () -> this.utils.sqlMode() ? "MySQL" : "YAML"));
        metrics.addCustomChart(new Metrics.SimplePie("lang",
                () -> this.getConfig().getString("lang")));

        utils.consoleLog(ChatColor.GREEN + "UserLogin enabled!");
    }

    @Override
    public synchronized void onDisable() {
        if (!utils.sqlMode()) return;

        // Save data to MySQL database
        sql.saveData();
        utils.consoleLog(utils.color(Objects.requireNonNull(utils.getConfig().getString(Path.SQL_DATA_SAVED))));
    }
}
