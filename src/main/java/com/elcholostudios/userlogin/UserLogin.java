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
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

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

        Objects.requireNonNull(getCommand("userlogin")).setExecutor(handler);
        Objects.requireNonNull(getCommand("login")).setExecutor(new Login());
        Objects.requireNonNull(getCommand("register")).setExecutor(new Register());

        pluginSetup();

        System.out.println(ChatColor.GREEN + "UserLogin enabled!");
    }

    public static void pluginSetup() {
        Utils utils = new Utils();

        // Add default configuration
        UserLogin.plugin.getConfig().options().copyDefaults();
        UserLogin.plugin.saveDefaultConfig();
        UserLogin.plugin.reloadConfig();

        // Add default messages
        messagesFile.setup();
        messagesFile.registerDefaults();

        // Add default locations
        locationsFile.setup();
        locationsFile.registerDefaults();

        dataFile.setup();

        // Update passwords (Encrypt or decrypt each one of them if needed)
        utils.updatePasswords(UserLogin.plugin.getConfig().getBoolean("password.encrypt"));
    }
}
