package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Utils {

    public static final Map<UUID, Boolean> loggedIn = new HashMap<>();
    public static final Map<UUID, Integer> timeouts = new HashMap<>();
    public static final Map<UUID, String> playerIP = new HashMap<>();

    public static void cancelTimeout(@NotNull Player player) {
        Integer id = timeouts.get(player.getUniqueId());
        if (id != null)
            UserLogin.getPlugin().getServer().getScheduler().cancelTask(id);
    }

    public static void sendMessage(@NotNull String path, @NotNull CommandSender player) {
        sendMessage(path, player, new String[0], new String[0]);
    }

    public static void sendMessage(@NotNull String path, @NotNull CommandSender player, String @NotNull [] replace, String[] replacement) {
        String msg = UserLogin.messagesFile.get().getString(path);
        if (msg == null || msg.equals("")) return;
        msg = color(msg);

        // Replace variables
        for (int i = 0; i < replace.length; i++) {
            msg = msg.replace("{" + replace[i] + "}", replacement[i]);
        }

        player.sendMessage(msg);
    }

    public static @NotNull String color(@NotNull String s) {
        try {
            net.md_5.bungee.api.ChatColor.class.getMethod("of", Color.class);
            Pattern pattern = Pattern.compile("#[0-9a-fA-F]{6}");
            Matcher match = pattern.matcher(s);
            while (match.find()) {
                String color = s.substring(match.start(), match.end());
                s = s.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                match = pattern.matcher(s);
            }
        } catch (NoSuchMethodException ignored) {
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static void updatePasswords(boolean encrypt) {
        for (String key : UserLogin.dataFile.get().getKeys(false)) {
            String password = UserLogin.dataFile.get().getString(key + ".password");
            if (password == null) continue;
            String newPassword;
            if (encrypt)
                newPassword = encrypt(password);
            else
                newPassword = decrypt(password);

            UserLogin.dataFile.get().set(key + ".password", newPassword);
        }
        UserLogin.dataFile.save();
    }

    public static String encrypt(String password) {
        if (password.startsWith("§")) return password;
        try {
            ByteArrayOutputStream io = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(io);

            os.writeObject(password);
            os.flush();

            return "§" + Base64.getEncoder().encodeToString(io.toByteArray());
        } catch (IOException e) {
            System.out.println("Error while trying to encrypt a password");
            return password;
        }
    }

    public static  @NotNull String decrypt(@NotNull String password) {
        if (!password.startsWith("§")) return password;
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(password.replaceFirst("§", "")));
            ObjectInputStream is = new ObjectInputStream(in);
            return (String) is.readObject();
        } catch (@NotNull IOException | ClassNotFoundException e) {
            System.out.println("Error while trying to decrypt a password");
            e.printStackTrace();
            return password;
        }
    }

    public static boolean isRegistered(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        return (!sqlMode() && UserLogin.dataFile.get().getKeys(true).contains(uuid.toString() + ".password")) ||
                (sqlMode() && UserLogin.sql.data.containsKey(uuid));
    }

    public static void updateName(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();
        if (!UserLogin.dataFile.get().getKeys(false).contains(uuid)) return;

        UserLogin.dataFile.get().set(uuid + ".name", player.getName());
        UserLogin.dataFile.save();
    }

    public static @Nullable Location getLocation(@NotNull String location) {
        // Get section
        ConfigurationSection section = UserLogin.locationsFile.get().getConfigurationSection(location);
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

    public static  @NotNull FileConfiguration getConfig() {
        return UserLogin.getPlugin().getConfig();
    }

    public static boolean normalMode() {
        return !getConfig().getString("teleports.mode").toUpperCase().equals("SAVE-POSITION")
                || !getConfig().getBoolean("teleports.mode");
    }

    public static void joinAnnounce(@NotNull Player player, @Nullable String msg) {
        if (msg == null) return;
        for (Player onlinePlayer : UserLogin.getPlugin().getServer().getOnlinePlayers()) {
            if (player != onlinePlayer)
                player.sendMessage(msg);
        }
    }

    public static boolean sqlMode() {
        return getConfig().getBoolean("mysql.enabled");
    }

    public static void log(@NotNull String msg) {
        UserLogin.getPlugin().getServer().getConsoleSender().sendMessage(msg);
    }

    public static void sendToServer(@NotNull Player player, @NotNull String server) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        try {
            out.writeUTF("Connect");
            out.writeUTF(server);

            player.sendPluginMessage(UserLogin.getPlugin(), "BungeeCord", bout.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String fetch(String link) {
        try {
            URL url = new URL(link);

            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            StringBuilder result = new StringBuilder();
            while ((line = in.readLine()) != null) {
                result.append(line);
            }

            return result.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
