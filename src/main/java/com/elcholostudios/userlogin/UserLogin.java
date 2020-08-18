package com.elcholostudios.userlogin;

import com.elcholostudios.userlogin.commands.Login;
import com.elcholostudios.userlogin.commands.Register;
import com.elcholostudios.userlogin.commands.subs.Help;
import com.elcholostudios.userlogin.commands.subs.Reload;
import com.elcholostudios.userlogin.commands.subs.SQL;
import com.elcholostudios.userlogin.commands.subs.Set;
import com.elcholostudios.userlogin.events.*;
import com.elcholostudios.userlogin.files.DataFile;
import com.elcholostudios.userlogin.files.LocationsFile;
import com.elcholostudios.userlogin.files.MessagesFile;
import com.elcholostudios.userlogin.util.Configuration;
import com.elcholostudios.userlogin.util.Lang;
import com.elcholostudios.userlogin.util.MySQL;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.util.command.CommandHandler;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.Objects;

public final class UserLogin extends JavaPlugin {

    public static final Configuration messagesFile = new MessagesFile();
    public static final Configuration locationsFile = new LocationsFile();
    public static final Configuration dataFile = new DataFile();
    public static final MySQL sql = new MySQL();
    public static CommandHandler handler;
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

        // Listeners setup
        addListener(new OnPlayerJoin());
        addListener(new OnCommandSent());
        addListener(new OnConsoleCommand());
        addListener(new OnChatMessage());
        addListener(new OnPlayerMove());
        addListener(new OnPlayerQuit());

        // Command setup
        handler = new CommandHandler("userlogin", this);

        handler.addCommand(new Help());
        handler.addCommand(new Set());
        handler.addCommand(new Reload());
        handler.addCommand(new SQL());
        handler.trim();

        Objects.requireNonNull(getCommand("login")).setExecutor(new Login());
        Objects.requireNonNull(getCommand("register")).setExecutor(new Register());

        pluginSetup();

        utils.consoleLog(ChatColor.GREEN + "UserLogin enabled!");
    }

    @Override
    public synchronized void onDisable() {
        if (!utils.sqlMode()) return;

        sql.saveData();
        utils.consoleLog(utils.color(Objects.requireNonNull(utils.getConfig().getString(Path.SQL_DATA_SAVED))));
    }

    private void addListener(@NotNull Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }
}
