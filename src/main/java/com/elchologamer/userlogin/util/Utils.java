package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Utils {

    private final static UserLogin plugin = UserLogin.getPlugin();

    public static void sendMessage(String path, CommandSender player) {
        sendMessage(path, player, new String[0], new String[0]);
    }

    public static void sendMessage(String path, CommandSender player, String[] replace, String[] replacement) {
        String msg = plugin.getMessage(path);
        if (msg == null || msg.equals("")) return;
        msg = color(msg);

        // Replace variables
        for (int i = 0; i < replace.length; i++) {
            msg = msg.replace("{" + replace[i] + "}", replacement[i]);
        }

        player.sendMessage(msg);
    }

    public static String color(String s) {
        try {
            Class.forName("net.md_5.bungee.api.ChatColor").getMethod("of", String.class);
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(s);

            while (matcher.find()) {
                String color = s.substring(matcher.start(), matcher.end());
                s = s.replace(color, net.md_5.bungee.api.ChatColor.of(color).toString());
                matcher = pattern.matcher(s);
            }
        } catch (ClassNotFoundException | NoSuchMethodException ignored) {
        }

        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static Location getLocation(String key) {
        return getLocation(key, null);
    }

    public static Location getLocation(String key, Location def) {
        ConfigurationSection section = plugin.getLocations().get().getConfigurationSection(key);
        if (section == null) return def;

        // Get coordinates
        String worldName = section.getString("world", "");
        World world = plugin.getServer().getWorld(worldName);
        if (world == null) world = plugin.getServer().getWorlds().get(0);

        Location spawn = world.getSpawnLocation();

        // Return actual location
        return new Location(
                world,
                section.getDouble("x", spawn.getX()),
                section.getDouble("y", spawn.getY()),
                section.getDouble("z", spawn.getZ()),
                (float) section.getDouble("yaw", spawn.getYaw()),
                (float) section.getDouble("pitch", spawn.getPitch()));
    }

    public static FileConfiguration getConfig() {
        return plugin.getConfig();
    }

    public static boolean normalMode() {
        // Backwards compatibility sucks lmao
        boolean oldCondition = getConfig()
                .getString("teleports.mode", "")
                .equalsIgnoreCase("SAVE-POSITION");
        boolean newCondition = getConfig().getBoolean(
                "teleports.savePosition",
                getConfig().getBoolean("teleports.save-position")
        );

        return !oldCondition && !newCondition;
    }

    public static void joinAnnounce(Player player, String msg) {
        if (msg == null || player == null) return;
        for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
            if (!player.equals(onlinePlayer)) player.sendMessage(msg);
        }
    }

    public static void debug(Object s) {
        if (getConfig().getBoolean("debug")) log(s);
    }

    public static void log(Object msg) {
        plugin.getServer().getConsoleSender().sendMessage(
                "[UserLogin] " + color(msg.toString())
        );
    }

    public static void sendPluginMessage(Player player, String channel, String... args) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        Arrays.stream(args).forEach(out::writeUTF);

        player.sendPluginMessage(plugin, channel, out.toByteArray());
    }

    public static String fetch(String url) {
        try {
            HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            InputStream stream = con.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(stream));

            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
