package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.ChatColor;
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

    private static final UserLogin plugin = UserLogin.getPlugin();

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

    public static void debug(Object s) {
        if (plugin.getConfig().getBoolean("debug")) log(s);
    }

    public static void log(Object msg) {
        plugin.getServer().getConsoleSender().sendMessage(
                "[" + plugin.getName() + "] " + color(msg.toString())
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
