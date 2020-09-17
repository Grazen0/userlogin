package com.elchologamer.userlogin;

import com.elchologamer.pluginapi.SpigotPlugin;
import com.elchologamer.pluginapi.util.CustomConfig;
import com.elchologamer.pluginapi.util.Metrics;
import com.elchologamer.pluginapi.util.command.CommandHandler;
import com.elchologamer.pluginapi.util.command.SpigotCommand;
import com.elchologamer.userlogin.commands.LoginCommand;
import com.elchologamer.userlogin.commands.RegisterCommand;
import com.elchologamer.userlogin.commands.subs.HelpCommand;
import com.elchologamer.userlogin.commands.subs.ReloadCommand;
import com.elchologamer.userlogin.commands.subs.SetCommand;
import com.elchologamer.userlogin.commands.subs.SqlCommand;
import com.elchologamer.userlogin.listeners.*;
import com.elchologamer.userlogin.util.Lang;
import com.elchologamer.userlogin.util.MySQL;
import com.elchologamer.userlogin.util.Path;
import com.elchologamer.userlogin.util.Utils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class UserLogin extends SpigotPlugin {

    private static UserLogin plugin;
    private final CustomConfig locationsFile = new CustomConfig("locations.yml");
    private final CustomConfig dataFile = new CustomConfig("playerData.yml");
    private final MySQL sql = new MySQL();
    private final Map<String, FileConfiguration> langMap = new HashMap<>();

    private final LoginCommand loginCommand = new LoginCommand(this);
    private final RegisterCommand registerCommand = new RegisterCommand(this);
    private final CommandHandler mainCommand = new CommandHandler("userlogin", null, null, "ul.userlogin", null);

    public static UserLogin getPlugin() {
        return plugin;
    }

    private void setCommand(SpigotCommand command) {
        if (command == null) return;
        command.unregister();

        String name = command.getName();

        command.setUsage(getMessage("commands.usages." + name, "/" + name))
                .setDescription(getMessage("commands.descriptions." + name, ""));

        List<String> aliases = getConfig().getStringList("commandAliases." + name);
        if (!getConfig().isConfigurationSection("commandAliases") && name.equals("userlogin"))
            aliases.add("ul");

        command.setAliases(aliases);
        command.register();
    }

    public void pluginSetup() {
        // Add default configuration
        saveDefaultConfig();
        reloadConfig();

        // Create default language files
        Lang.createDefaultLang();

        // Set up configurations
        locationsFile.saveDefault();
        dataFile.saveDefault();

        // Load lang files
        File langFolder = new File(getDataFolder(), "lang\\");
        langFolder.mkdir();

        langMap.clear();
        File[] files = langFolder.listFiles();
        if (files != null) {
            for (File file : files) {
                langMap.put(file.getName().replace(".yml", ""),
                        YamlConfiguration.loadConfiguration(file));
            }
        }

        // Set usages for commands
        setCommand(mainCommand);
        setCommand(loginCommand);
        setCommand(registerCommand);

        setPermissionMessage(getMessage(Path.NO_PERMISSION));

        // Cancel all plugin tasks
        getServer().getScheduler().cancelTasks(plugin);
        Utils.playerIP.clear();
        Utils.timeouts.clear();

        if (!Utils.sqlMode()) {
            // Update passwords (Encrypt or decrypt each one of them if needed)
            for (String key : dataFile.get().getKeys(false)) {
                String password = dataFile.get().getString(key + ".password");
                if (password == null) continue;
                if (getConfig().getBoolean("password.encrypt"))
                    password = Utils.encrypt(password);
                else
                    password = Utils.decrypt(password);

                dataFile.get().set(key + ".password", password);
            }
            dataFile.save();
        } else {
            getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                try {
                    // Close current connection
                    if (sql.getConnection() != null && !sql.getConnection().isClosed())
                        sql.getConnection().close();

                    // Connect to MySQL database
                    sql.connect();

                    // Schedule data saving repeating task
                    long delay = getConfig().getLong("mysql.saveInterval") * 20;
                    getServer().getScheduler().scheduleSyncRepeatingTask(this, sql::saveData, delay, delay);

                    Utils.log(getMessage(Path.SQL_CONNECTION_SUCCESS));
                } catch (SQLException | ClassNotFoundException e) {
                    Utils.log(getMessage(Path.SQL_CONNECTION_ERROR));
                    Utils.log(ChatColor.DARK_RED + e.getMessage());
                }
            });
        }
    }

    @Override
    public void onEnable() {
        plugin = this;

        // Plugin messaging setup
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        // Register event listeners
        getServer().getPluginManager().registerEvents(new OnPlayerJoin(this), this);
        getServer().getPluginManager().registerEvents(new OnPlayerMove(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerQuit(this), this);
        getServer().getPluginManager().registerEvents(new GeneralListener(), this);
        try {
            Class.forName("org.bukkit.event.entity.EntityPickupItemEvent");
            getServer().getPluginManager().registerEvents(new OnItemPickup(), this);
        } catch (ClassNotFoundException ignored) {
        }
        listenReloads();

        // Register commands
        mainCommand.addSubCommands(
                new HelpCommand(),
                new SetCommand(locationsFile),
                new ReloadCommand(),
                new SqlCommand())
                .register();
        loginCommand.register();
        registerCommand.register();

        pluginSetup();

        // bStats setup
        Metrics metrics = new Metrics(this, 8586);
        metrics.addCustomChart(new Metrics.SimplePie("data_type",
                () -> Utils.sqlMode() ? "MySQL" : "YAML"));
        metrics.addCustomChart(new Metrics.SimplePie("lang",
                () -> getConfig().getString("lang", "en_US")));

        // Check for new versions
        String version = getDescription().getVersion();
        if (getConfig().getBoolean("checkUpdates", true)) {
            getServer().getScheduler().scheduleSyncDelayedTask(this, () -> {
                String latest = getLatestVersion(80669);
                if (latest == null) {
                    Utils.log(ChatColor.RED + "Unable to get latest version");
                    return;
                }

                if (!latest.equalsIgnoreCase(version))
                    Utils.log(ChatColor.YELLOW + "A new UserLogin version is available! (v" + latest + ")");
                else
                    Utils.log(ChatColor.GREEN + "Running latest version! (v" + version + ")");
            });
        }

        logEnabled();
    }

    @Override
    public synchronized void onDisable() {
        if (!Utils.sqlMode()) return;

        // Save data to MySQL database
        sql.saveData();
        Utils.log(getMessage(Path.SQL_DATA_SAVED));
    }

    public FileConfiguration getMessages() {
        String lang = getConfig().getString("lang", "en_US");
        return langMap.get(lang);
    }

    public String getMessage(String path, String def) {
        FileConfiguration config = getMessages();
        return Utils.color(config == null ? "" : config.getString(path, def));
    }

    @Override
    public String getMessage(String path) {
        return getMessage(path, null);
    }

    public CustomConfig getLocations() {
        return locationsFile;
    }

    public CustomConfig getPlayerData() {
        return dataFile;
    }

    public MySQL getSQL() {
        return sql;
    }
}
