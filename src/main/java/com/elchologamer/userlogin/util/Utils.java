package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Utils {

    public static final Map<UUID, Boolean> loggedIn = new HashMap<>();
    public static final Map<UUID, Integer> timeouts = new HashMap<>();
    public static final Map<UUID, Integer> repeatingMsg = new HashMap<>();
    public static final Map<UUID, String> playerIP = new HashMap<>();

    private Utils() {
    }

    public static void cancelTimeout(Player player) {
        UUID uuid = player.getUniqueId();
        Integer id = timeouts.get(uuid);
        if (id != null)
            UserLogin.getPlugin().getServer().getScheduler().cancelTask(id);

        id = repeatingMsg.get(uuid);
        if (id != null)
            UserLogin.getPlugin().getServer().getScheduler().cancelTask(id);
    }

    public static void sendMessage(String path, CommandSender player) {
        sendMessage(path, player, new String[0], new String[0]);
    }

    public static void sendMessage(String path, CommandSender player, String[] replace, String[] replacement) {
        String msg = UserLogin.getPlugin().getMessage(path);
        if (msg == null || msg.equals("")) return;
        msg = com.elchologamer.pluginapi.Utils.color(msg);

        // Replace variables
        for (int i = 0; i < replace.length; i++) {
            msg = msg.replace("{" + replace[i] + "}", replacement[i]);
        }

        player.sendMessage(msg);
    }

    public static String color(String s) {
        return com.elchologamer.pluginapi.Utils.color(s);
    }

    public static String encrypt(String password) {
        if (password == null || password.startsWith("§")) return password;
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(io);

            os.writeObject(password);
            os.flush();

            return "§" + Base64.getEncoder().encodeToString(io.toByteArray());
        } catch (IOException e) {
            return password;
        }
    }

    public static String decrypt(String password) {
        if (password == null || !password.startsWith("§")) return password;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(password.replaceFirst("§", "")));
            ObjectInputStream is = new ObjectInputStream(in);
            return (String) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return password;
        }
    }

    public static void updateName(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!UserLogin.getPlugin().getPlayerData().get().getKeys(false).contains(uuid)) return;

        UserLogin.getPlugin().getPlayerData().get().set(uuid + ".name", player.getName());
        UserLogin.getPlugin().getPlayerData().save();
    }

    public static Location getLocation(String location) {
        ConfigurationSection section = UserLogin.getPlugin().getLocations().get().getConfigurationSection(location);
        if (section == null) return null;

        // Get coordinates
        String worldName = section.getString("world");
        double x = section.getDouble("x");
        double y = section.getDouble("y");
        double z = section.getDouble("z");
        float yaw = (float) section.getDouble("yaw");
        float pitch = (float) section.getDouble("pitch");

        // Return default location if options are on default values
        if (worldName == null || (worldName.equals("DEFAULT") && (x + y + z + yaw + pitch) == 0))
            return UserLogin.getPlugin().getServer().getWorlds().get(0).getSpawnLocation();

        // Return actual location
        return new Location(
                UserLogin.getPlugin().getServer().getWorld(worldName),
                section.getInt("x"),
                section.getInt("y"),
                section.getInt("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch"));
    }

    public static FileConfiguration getConfig() {
        return UserLogin.getPlugin().getConfig();
    }

    public static boolean normalMode() {
        return !(getConfig().getString("teleports.mode", "").toUpperCase().equals("SAVE-POSITION")
                || getConfig().getBoolean("teleports.save-position")
                || getConfig().getBoolean("teleports.savePosition"));
    }

    public static void joinAnnounce(Player player, String msg) {
        if (msg == null) return;
        for (Player onlinePlayer : UserLogin.getPlugin().getServer().getOnlinePlayers()) {
            if (player != onlinePlayer)
                player.sendMessage(msg);
        }
    }

    public static void debug(String s) {
        if(getConfig().getBoolean("debug"))
            log(s);
    }

    public static boolean sqlMode() {
        return getConfig().getBoolean("mysql.enabled");
    }

    public static void log(String msg) {
        UserLogin.getPlugin().getServer().getConsoleSender().sendMessage(
                "[UserLogin] " + Utils.color(msg));
    }

    public static void sendPluginMessage(Player player, String channel, String... args) {
        if (player == null)
            throw new NullPointerException("Player cannot be null");

        if (channel == null)
            throw new NullPointerException("Message channel cannot be null");

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        for (String arg : args)
            out.writeUTF(arg);

        player.sendPluginMessage(UserLogin.getPlugin(), channel, out.toByteArray());
    }

    public static void sendToServer(Player player, String server) {
        sendPluginMessage(player, "BungeeCord", "Connect", server);
    }

    public static void changeLoggedIn(Player player, Boolean loggedIn) {
        Utils.loggedIn.put(player.getUniqueId(), loggedIn);
        sendPluginMessage(player, "BungeeCord", "UserLogin", Boolean.toString(loggedIn));
    }

    public static void setTimeout(Player player) {
        if (!Utils.getConfig().getBoolean("timeout.enabled")) return;

        timeouts.put(player.getUniqueId(),
                UserLogin.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(
                        UserLogin.getPlugin(),
                        () -> player.kickPlayer(UserLogin.getPlugin().getMessage(Path.TIMEOUT)),
                        UserLogin.getPlugin().getConfig().getLong("timeout.time") * 20));
    }
}
