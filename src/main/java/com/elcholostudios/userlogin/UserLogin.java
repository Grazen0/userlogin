package com.elcholostudios.userlogin;

import com.elcholostudios.userlogin.commands.CommandHandler;
import com.elcholostudios.userlogin.commands.Login;
import com.elcholostudios.userlogin.commands.Register;
import com.elcholostudios.userlogin.commands.subs.Help;
import com.elcholostudios.userlogin.commands.subs.Reload;
import com.elcholostudios.userlogin.commands.subs.Set;
import com.elcholostudios.userlogin.events.*;
import com.elcholostudios.userlogin.util.Configuration;
import com.elcholostudios.userlogin.util.lists.Path;
import com.elcholostudios.userlogin.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class UserLogin extends JavaPlugin {

    public static final CommandHandler handler = new CommandHandler();
    public static final Configuration messagesFile = new Configuration("messages");
    public static final Configuration locationsFile = new Configuration("locations");
    public static final Configuration dataFile = new Configuration("playerData");
    public static UserLogin plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Listeners setup
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnCommandSent(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnConsoleCommand(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnChatMessage(), this);
        Bukkit.getServer().getPluginManager().registerEvents(new OnPlayerMove(), this);

        // Command setup
        handler.addCommand(new Help());
        handler.addCommand(new Set());
        handler.addCommand(new Reload());
        handler.trim();

        pluginSetup();

        Objects.requireNonNull(getCommand("userlogin")).setExecutor(handler);
        Objects.requireNonNull(getCommand("login")).setExecutor(new Login());
        Objects.requireNonNull(getCommand("register")).setExecutor(new Register());

        System.out.println("UserLogin enabled!");
    }

    public static void pluginSetup() {
        Utils utils = new Utils();

        UserLogin.plugin.getConfig().options().copyDefaults();
        UserLogin.plugin.saveDefaultConfig();
        UserLogin.plugin.reloadConfig();

        messagesFile.setup();
        messagesFile.get().addDefault(Path.PLAYER_ONLY, "&cThis is a player-only command");
        messagesFile.get().addDefault(Path.SET, "&eThe {type} location has been set at &bX: {x}, Y: {y}, Z: {z}, Y: {yaw}, P: {pitch}, &eat the world &b\"{world}\"");
        messagesFile.get().addDefault(Path.RELOAD, "&eConfigurations reloaded!");
        messagesFile.get().addDefault(Path.NOT_REGISTERED, "&cYou are not registered! Use &a/register <password> <password> &cto get registered!");
        messagesFile.get().addDefault(Path.ALREADY_REGISTERED, "&cYou are already registered!");
        messagesFile.get().addDefault(Path.INCORRECT_PASSWORD, "&4Incorrect password!");
        messagesFile.get().addDefault(Path.SHORT_PASSWORD, "&cYour password must have a minimum of &4{chars} &ccharacters");
        messagesFile.get().addDefault(Path.DIFFERENT_PASSWORDS, "&cYour passwords don't match!");
        messagesFile.get().addDefault(Path.ALREADY_LOGGED_IN, "&cYou are already logged in!");
        messagesFile.get().addDefault(Path.LOGGED_IN, "&9Login successful!");
        messagesFile.get().addDefault(Path.REGISTERED, "&9You have been registered!");
        messagesFile.get().addDefault(Path.WELCOME_LOGIN, "&6Welcome! Use &3/login <password> &6to enter the server");
        messagesFile.get().addDefault(Path.WELCOME_REGISTER, "&6Welcome! Use &3/register <password> <password> &6to register your account");
        messagesFile.get().addDefault(Path.CHAT_DISABLED, "&cYou must first log in to use the chat!");
        messagesFile.get().addDefault(Path.TIMEOUT, "You have stayed for too much time without logging in");

        List<String> help = new ArrayList<>();
        help.add("&a----------- | &2[UserLogin Help] &a| -----------");
        help.add("&2/ul help: &aShows this help list");
        help.add("&2/ul reload: &aShows this help list");
        help.add("&2/ul set <login>|<spawn>: &aSets the specified location at your position");
        help.add("&a-----------------------------------------------------");

        messagesFile.get().addDefault(Path.HELP, help);

        messagesFile.get().options().copyDefaults(true);
        messagesFile.save();

        locationsFile.setup();
        locationsFile.get().addDefault("login.x", 0);
        locationsFile.get().addDefault("login.y", 0);
        locationsFile.get().addDefault("login.z", 0);
        locationsFile.get().addDefault("login.yaw", 0);
        locationsFile.get().addDefault("login.pitch", 0);
        locationsFile.get().addDefault("login.world", "DEFAULT");
        locationsFile.get().addDefault("spawn.x", 0);
        locationsFile.get().addDefault("spawn.y", 0);
        locationsFile.get().addDefault("spawn.z", 0);
        locationsFile.get().addDefault("spawn.yaw", 0);
        locationsFile.get().addDefault("spawn.pitch", 0);
        locationsFile.get().addDefault("spawn.world", "DEFAULT");
        locationsFile.get().options().copyDefaults(true);
        locationsFile.save();

        dataFile.setup();

        utils.updatePasswords(UserLogin.plugin.getConfig().getBoolean("password.encrypt"));
    }
}
