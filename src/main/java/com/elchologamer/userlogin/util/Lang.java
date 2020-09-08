package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.util.lists.Path;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Lang {

    public static void createDefaultLang() {
        // Create lang folder
        File folder = new File(UserLogin.getPlugin().getDataFolder(), "lang\\");
        folder.mkdir();

        File en_US = new File(UserLogin.getPlugin().getDataFolder(), "lang\\en_US.yml");
        File es_ES = new File(UserLogin.getPlugin().getDataFolder(), "lang\\es_ES.yml");

        try {
            // English language
            en_US.createNewFile();
            FileConfiguration config = YamlConfiguration.loadConfiguration(en_US);

            config.addDefault(Path.PLAYER_ONLY, "&cThis is a player-only command");
            config.addDefault(Path.SET, "&eThe {type} location has been set at &bX: {x}, Y: {y}, Z: {z}, Y: {yaw}, P: {pitch}, &eat the world &b\"{world}\"");
            config.addDefault(Path.RELOAD, "&ePlugin reloaded!");
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
            config.addDefault(Path.JOIN_TITLE, "&6Welcome to the server!");
            config.addDefault(Path.JOIN_SUBTITLE, "&9Have fun!");

            config.addDefault(Path.USERLOGIN_USAGE, "&cCorrect usage: /<command> <sub> (Use /ul help for more)");
            config.addDefault(Path.LOGIN_USAGE, "&cCorrect usage: /<command> <password>");
            config.addDefault(Path.REGISTER_USAGE, "&cCorrect usage: /<command> <password> <password>");

            config.addDefault(Path.SQL_STATE, "&eCurrent MySQL status: &6{state}");
            config.addDefault(Path.SQL_CONNECTED, "Connected");
            config.addDefault(Path.SQL_DISCONNECTED, "Disconnected");
            config.addDefault(Path.SQL_DISABLED, "Disabled");

            config.addDefault(Path.SQL_CONNECTION_SUCCESS, "&aMySQL connected successfully!");
            config.addDefault(Path.SQL_DATA_SAVED, "&3Local data saved to MySQL database");
            config.addDefault(Path.SQL_CONNECTION_ERROR, "&cAn error occurred while trying to connect to MySQL database, read logs for more");
            config.addDefault(Path.SQL_SAVE_ERROR, "&cAn error occurred while saving local data to MySQL database, beware.");

            List<String> help = new ArrayList<>();
            help.add("&a----------- | &2[UserLogin Help] &a| -----------");
            help.add("&2/ul help: &aShows this help list");
            help.add("&2/ul reload: &aReloads the configurations");
            help.add("&2/ul set <login>|<spawn>: &aSets the specified location at your position");
            help.add("&2/ul sql: &aShows the current status of the MySQL connection");
            help.add("&a-----------------------------------------------------");

            config.addDefault(Path.HELP, help);

            config.options().copyDefaults(true);
            config.save(en_US);

            // Spanish language
            es_ES.createNewFile();
            config = YamlConfiguration.loadConfiguration(es_ES);

            config.addDefault(Path.PLAYER_ONLY, "&cEste comando solo puede ser usado por jugadores");
            config.addDefault(Path.SET, "&eLas coordenadas de {type} han sido establecidas en &bX: {x}, Y: {y}, Z: {z}, Y: {yaw}, P: {pitch}, &een el mundo &b\"{world}\"");
            config.addDefault(Path.RELOAD, "&ePlugin recargado!");
            config.addDefault(Path.NOT_REGISTERED, "&c¡No estás registrado! Use &a/register <password> <password> &cpara registrarte!");
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
            config.addDefault(Path.JOIN_TITLE, "&6Bienvenido al servidor!");
            config.addDefault(Path.JOIN_SUBTITLE, "&9Diviértete!");

            config.addDefault(Path.USERLOGIN_USAGE, "&cUso correcto: /<command> <sub> (Usa &4/ul help &para más)");
            config.addDefault(Path.LOGIN_USAGE, "&cUso correcto: /<command> <password>");
            config.addDefault(Path.REGISTER_USAGE, "&cUso correcto: /<command> <password> <password>");

            config.addDefault(Path.SQL_STATE, "&eEstado de MySQL actual: &6{state}");
            config.addDefault(Path.SQL_CONNECTED, "Conectado");
            config.addDefault(Path.SQL_DISCONNECTED, "Desconectado");
            config.addDefault(Path.SQL_DISABLED, "Desactivado");

            config.addDefault(Path.SQL_CONNECTION_SUCCESS, "&aBase de datos MySQL conectada!");
            config.addDefault(Path.SQL_DATA_SAVED, "&3Datos locales guardados en la base de datos");
            config.addDefault(Path.SQL_CONNECTION_ERROR, "&cError al conectar a la base de datos MySQL, leer el error para más información");
            config.addDefault(Path.SQL_SAVE_ERROR, "&cError al guardar los datos locales en la base de datos.");

            help.clear();
            help.add("&a--------- | &2[Ayuda de UserLogin] &a| ---------");
            help.add("&2/ul help: &aMuestra esta lista de ayuda");
            help.add("&2/ul reload: &aRecarga las configuraciones");
            help.add("&2/ul set <login>|<spawn>: &aEstablece la posición indicada en tus coordenadas");
            help.add("&2/ul sql: &aMuestra el estado actual de la conexión MySQL");
            help.add("&a-----------------------------------------------------");

            config.addDefault(Path.HELP, help);

            config.options().copyDefaults(true);
            config.save(es_ES);
        } catch (IOException e) {
            System.out.println("Error while trying to create default lang files");
        }
    }
}
