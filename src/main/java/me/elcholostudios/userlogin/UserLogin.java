package me.elcholostudios.userlogin;

import me.elcholostudios.userlogin.commands.*;
import me.elcholostudios.userlogin.events.*;
import me.elcholostudios.userlogin.files.MessageFile;
import me.elcholostudios.userlogin.files.PlayerDataFile;
import org.apache.commons.lang.StringUtils;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;


public final class UserLogin extends JavaPlugin {

    public static UserLogin plugin;
    private static final Essentials es = new Essentials();

    @Override
    public void onEnable() {
        System.out.println("[UserLogin] UserLogin enabled!");

        plugin = this;

        getConfig().options().copyDefaults(true);
        saveConfig();

        MessageFile.setup();
        MessageFile.get().addDefault("display-messages.register-message", "&aWelcome, &b{player}&a! Type /register <password> <password> to enter");
        MessageFile.get().addDefault("display-messages.login-message", "&aWelcome back, &b{player}&a! Type /login <password> to enter");
        MessageFile.get().addDefault("display-messages.login-successful", "&bLogged in!");
        MessageFile.get().addDefault("display-messages.register-successful", "&bYou have been registered!");
        MessageFile.get().addDefault("display-messages.time-out", "&fConnection timed out");
        MessageFile.get().addDefault("display-messages.log-in-first", "&cLog in first to send chat messages!");
        MessageFile.get().addDefault("command-errors.incorrect-password", "&cIncorrect password!");
        MessageFile.get().addDefault("command-errors.incorrect-usage", "&cIncorrect usage of the command");
        MessageFile.get().addDefault("command-errors.player-only", "&fThis is a player-only command");
        MessageFile.get().addDefault("command-errors.not-registered", "&cYou are not registered!");
        MessageFile.get().addDefault("command-errors.already-registered", "&cYou are already registered!");
        MessageFile.get().addDefault("command-errors.non-matching-passwords", "&cThe passwords do not match!");
        MessageFile.get().addDefault("command-errors.no-permission", "&cYou don't have permission for that!");
        MessageFile.get().addDefault("command-errors.reload-failed", "&cFailed to reload configurations");
        MessageFile.get().addDefault("command-errors.few-characters", "&cThe minimum amount of characters is {min}");
        MessageFile.get().addDefault("command-errors.already-logged", "&cYou are already logged in!");
        MessageFile.get().addDefault("command-errors.command-disabled", "&cLog in to use that command!");
        MessageFile.get().addDefault("commands.login-set", "&eLogin spawn set at x: &b{x}&e, y: &b{y}&e, z: &b{z}&e, y: &b{yaw}&e, p: &b{pitch}&e, in world &b'{world}'");
        MessageFile.get().addDefault("commands.lobby-set", "&eLobby spawn set at x: &b{x}&e, y: &b{y}&e, z: &b{z}&e, y: &b{yaw}&e, p: &b{pitch}&e, in world &b'{world}'");
        MessageFile.get().addDefault("commands.reload-successful", "&eReload successful!");
        MessageFile.get().options().copyDefaults(true);
        MessageFile.save();

        PlayerDataFile.setup();

        getServer().getPluginManager().registerEvents(new OnPlayerJoin(), this);
        getServer().getPluginManager().registerEvents(new OnCommandReceived(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerQuit(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerChat(), this);
        getServer().getPluginManager().registerEvents(new OnPlayerMove(), this);

        Objects.requireNonNull(getCommand("setlogin")).setExecutor(new SetLogin());
        Objects.requireNonNull(getCommand("setlobby")).setExecutor(new SetLobby());
        Objects.requireNonNull(getCommand("register")).setExecutor(new Register());
        Objects.requireNonNull(getCommand("reloadlogin")).setExecutor(new ReloadLogin());
        Objects.requireNonNull(getCommand("login")).setExecutor(new Login());

        Objects.requireNonNull(getCommand("setlogin")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("setlogin")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("register")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("reloadlogin")).setTabCompleter(new TabCompletion());
        Objects.requireNonNull(getCommand("login")).setTabCompleter(new TabCompletion());

        updatePasswords();
    }

    public static void updatePasswords() {
        for(String key : PlayerDataFile.get().getKeys(false)){
            if(PlayerDataFile.get().isConfigurationSection(key) && PlayerDataFile.get().contains(key+".password")){
                boolean encrypt = UserLogin.plugin.getConfig().getBoolean("passwords.encryptPasswords");
                String password = PlayerDataFile.get().getString(key+".password");
                assert password != null;
                if(encrypt){
                    if(!StringUtils.left(password, 1).equals("§")){
                        password = es.encrypt(password);
                        PlayerDataFile.get().set(key+".password", password);
                    }
                }else{
                    if(StringUtils.left(password, 1).equals("§")){
                        password = es.decrypt(password);
                        PlayerDataFile.get().set(key+".password", password);
                    }
                }
            }
        }
        PlayerDataFile.save();
    }
}
