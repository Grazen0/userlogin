package com.elcholostudios.userlogin;

import com.elcholostudios.userlogin.commands.CommandHandler;
import com.elcholostudios.userlogin.commands.Login;
import com.elcholostudios.userlogin.commands.Register;
import com.elcholostudios.userlogin.commands.subs.Help;
import com.elcholostudios.userlogin.commands.subs.Reload;
import com.elcholostudios.userlogin.commands.subs.Set;
import com.elcholostudios.userlogin.events.*;
import com.elcholostudios.userlogin.files.DataFile;
import com.elcholostudios.userlogin.files.LocationsFile;
import com.elcholostudios.userlogin.files.MessagesFile;
import com.elcholostudios.userlogin.util.Configuration;
import com.elcholostudios.userlogin.util.Utils;
import com.elcholostudios.userlogin.util.lists.Path;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class UserLogin extends JavaPlugin {

    public static final CommandHandler handler = new CommandHandler();
    public static final Configuration messagesFile = new MessagesFile();
    public static final Configuration locationsFile = new LocationsFile();
    public static final Configuration dataFile = new DataFile();
    public static UserLogin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Listeners setup
        addListener(new OnPlayerJoin());
        addListener(new OnCommandSent());
        addListener(new OnConsoleCommand());
        addListener(new OnChatMessage());
        addListener(new OnPlayerMove());
        addListener(new OnPlayerQuit());

        // Command setup
        handler.addCommand(new Help());
        handler.addCommand(new Set());
        handler.addCommand(new Reload());
        handler.trim();

        Objects.requireNonNull(getCommand("userlogin")).setExecutor(handler);
        Objects.requireNonNull(getCommand("login")).setExecutor(new Login());
        Objects.requireNonNull(getCommand("register")).setExecutor(new Register());

        pluginSetup();

        System.out.println("UserLogin enabled!");
    }

    public static void pluginSetup() {
        Utils utils = new Utils();

        // Add default configuration
        plugin.saveDefaultConfig();
        plugin.reloadConfig();

        // Create default language files
        createDefaultLang();

        // Set up configurations
        messagesFile.setup();
        locationsFile.setup();
        dataFile.setup();

        // Set usages for commands
        setUsage("userlogin", Path.USERLOGIN_USAGE);
        setUsage("login", Path.LOGIN_USAGE);
        setUsage("register", Path.REGISTER_USAGE);

        // Update passwords (Encrypt or decrypt each one of them if needed)
        utils.updatePasswords(plugin.getConfig().getBoolean("password.encrypt"));
    }

    private static void setUsage(String command, String path) {
        Utils utils = new Utils();
        Objects.requireNonNull(plugin.getCommand(command)).setUsage(utils.color(messagesFile.get().getString(path)));
    }

    private void addListener(Listener listener) {
        Bukkit.getServer().getPluginManager().registerEvents(listener, this);
    }

    private static void createDefaultLang() {
        // Create lang folder
        File folder = new File(UserLogin.plugin.getDataFolder(), "lang\\");
        folder.mkdir();

        File en_US = new File(plugin.getDataFolder(), "lang\\en_US.yml");
        File es_ES = new File(plugin.getDataFolder(), "lang\\es_ES.yml");

        try {
            // English language
            en_US.createNewFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(en_US);

            config.addDefault(Path.PLAYER_ONLY, "&cThis is a player-only command");
            config.addDefault(Path.SET, "&eThe {type} location has been set at &bX: {x}, Y: {y}, Z: {z}, Y: {yaw}, P: {pitch}, &eat the world &b\"{world}\"");
            config.addDefault(Path.RELOAD, "&eConfigurations reloaded!");
            config.addDefault(Path.NOT_REGISTERED, "&cYou are not registered! Use &a/register <password> <password> &cto get registered!");
            config.addDefault(Path.ALREADY_REGISTERED, "&cYou are already registered!");
            config.addDefault(Path.INCORRECT_PASSWORD, "&4Incorrect password!");
            config.addDefault(Path.SHORT_PASSWORD, "&cYour password must have a minimum of &4{chars} &ccharacters");
            config.addDefault(Path.DIFFERENT_PASSWORDS, "&cYour passwords don't match!");
            config.addDefault(Path.ALREADY_LOGGED_IN, "&cYou are already logged in!");
            config.addDefault(Path.LOGGED_IN, "&9Login successful!");
            config.addDefault(Path.REGISTERED, "&9You have been registered!");
            config.addDefault(Path.WELCOME_LOGIN, "&6Welcome! Use &3/login <password> &6to enter the server");
            config.addDefault(Path.WELCOME_REGISTER, "&6Welcome! Use &3/register <password> <password> &6to register your account");
            config.addDefault(Path.CHAT_DISABLED, "&cYou must first log in to use the chat!");
            config.addDefault(Path.TIMEOUT, "You have stayed for too much time without logging in");
            config.addDefault(Path.LOGIN_ANNOUNCEMENT, "&e{player} has joined the server!");
            config.addDefault(Path.USERLOGIN_USAGE, "&cCorrect usage: /<command> <sub> (Use /ul help for more)");
            config.addDefault(Path.LOGIN_USAGE, "&cCorrect usage: /<command> <password>");
            config.addDefault(Path.REGISTER_USAGE, "&cCorrect usage: /<command> <password> <password>");

            List<String> help = new ArrayList<>();
            help.add("&a----------- | &2[UserLogin Help] &a| -----------");
            help.add("&2/ul help: &aShows this help list");
            help.add("&2/ul reload: &aReloads the configurations");
            help.add("&2/ul set <login>|<spawn>: &aSets the specified location at your position");
            help.add("&a-----------------------------------------------------");

            config.addDefault(Path.HELP, help);

            config.options().copyDefaults(true);
            config.save(en_US);

            // Spanish language
            es_ES.createNewFile();
            config = YamlConfiguration.loadConfiguration(es_ES);

            config.addDefault(Path.PLAYER_ONLY, "&cEste comando solo puede ser usado por jugadores");
            config.addDefault(Path.SET, "&eLas coordenadas de {type} han sido establecidas en &bX: {x}, Y: {y}, Z: {z}, Y: {yaw}, P: {pitch}, &een el mundo &b\"{world}\"");
            config.addDefault(Path.RELOAD, "&eConfiguraciones recargadas!");
            config.addDefault(Path.NOT_REGISTERED, "&cNo estás registrado! Use &a/register <password> <password> &cpara registrarte!");
            config.addDefault(Path.ALREADY_REGISTERED, "&cYa estás registrado!");
            config.addDefault(Path.INCORRECT_PASSWORD, "&4Contraseña incorrecta!");
            config.addDefault(Path.SHORT_PASSWORD, "&cTu contraseña debe tener un mínimo de &4{chars} &ccaracteres");
            config.addDefault(Path.DIFFERENT_PASSWORDS, "&cLas contraseñas no concuerdan!");
            config.addDefault(Path.ALREADY_LOGGED_IN, "&cYa estás logeado!");
            config.addDefault(Path.LOGGED_IN, "&9Has sido logeado!");
            config.addDefault(Path.REGISTERED, "&9Has sido registrado!");
            config.addDefault(Path.WELCOME_LOGIN, "&6Bienvenido! Usa &3/login <password> &6para entrar al servidor");
            config.addDefault(Path.WELCOME_REGISTER, "&6Bienvenido! Usa &3/register <password> <password> &6para registrarte");
            config.addDefault(Path.CHAT_DISABLED, "&cDebes estar logeado para usar el chat!");
            config.addDefault(Path.TIMEOUT, "Te has quedado mucho tiempo sin logearte");
            config.addDefault(Path.LOGIN_ANNOUNCEMENT, "&e{player} se ha unido al servidor!");
            config.addDefault(Path.USERLOGIN_USAGE, "&cUso correcto: /<command> <sub> (Usa &4/ul help &para más)");
            config.addDefault(Path.LOGIN_USAGE, "&cUso correcto: /<command> <password>");
            config.addDefault(Path.REGISTER_USAGE, "&cUso correcto: /<command> <password> <password>");

            help.clear();
            help.add("&a--------- | &2[Ayuda de UserLogin] &a| ---------");
            help.add("&2/ul help: &aMuestra esta lista de ayuda");
            help.add("&2/ul reload: &aRecarga las configuraciones");
            help.add("&2/ul set <login>|<spawn>: &aEstablece la posición indicada en tus coordenadas");
            help.add("&a-----------------------------------------------------");

            config.addDefault(Path.HELP, help);

            config.options().copyDefaults(true);
            config.save(es_ES);
        } catch (IOException e) {
            System.out.println("Error while trying to create default lang files");
        }
    }
}
