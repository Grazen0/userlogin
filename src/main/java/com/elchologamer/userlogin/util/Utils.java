package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final Map<UUID, Boolean> loggedIn = new HashMap<>();
    public static final Map<UUID, Integer> timeouts = new HashMap<>();
    public static final Map<UUID, String> playerIP = new HashMap<>();

    public void cancelTimeout(@NotNull Player player) {
        Integer id = timeouts.get(player.getUniqueId());
        if (id != null)
            Bukkit.getServer().getScheduler().cancelTask(id);
    }

    public void sendMessage(@NotNull String path, @NotNull CommandSender player) {
        sendMessage(path, player, new String[0], new String[0]);
    }

    public void sendMessage(@NotNull String path, @NotNull CommandSender player, String @NotNull [] replace, String[] replacement) {
        String msg = UserLogin.messagesFile.get().getString(path);
        if (msg == null || msg.equals("")) return;
        msg = color(msg);

        // Replace variables
        for (int i = 0; i < replace.length; i++) {
            msg = msg.replace("{" + replace[i] + "}", replacement[i]);
        }

        player.sendMessage(msg);
    }

    public @NotNull String color(@NotNull String s) {
        if (Bukkit.getVersion().contains("1.16")) {
            Pattern pattern = Pattern.compile("#[0-9a-fA-F]{6}");
            Matcher match = pattern.matcher(s);
            while (match.find()) {
                String color = s.substring(match.start(), match.end());
                s = s.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                match = pattern.matcher(s);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public void updatePasswords(boolean encrypt) {
        for (String key : UserLogin.dataFile.get().getKeys(false)) {
            String password = UserLogin.dataFile.get().getString(key + ".password");
            if (password == null) continue;
            String newPassword = password;
            if (encrypt && !password.startsWith("§"))
                newPassword = encrypt(password);
            else if (password.startsWith("§"))
                newPassword = decrypt(password);

            UserLogin.dataFile.get().set(key + ".password", newPassword);
        }
        UserLogin.dataFile.save();
    }

    public String encrypt(String password) {
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

    public @NotNull String decrypt(@NotNull String password) {
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

    public boolean isRegistered(@NotNull Player player) {
        UUID uuid = player.getUniqueId();
        return (!sqlMode() && UserLogin.dataFile.get().getKeys(true).contains(uuid.toString() + ".password")) ||
                (sqlMode() && UserLogin.sql.data.containsKey(uuid));
    }

    public void updateName(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();
        if (!UserLogin.dataFile.get().getKeys(false).contains(uuid)) return;

        UserLogin.dataFile.get().set(uuid + ".name", player.getName());
        UserLogin.dataFile.save();
    }

    public void reloadWarn(@Nullable Player player) {
        String msg = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "[UserLogin: " +
                ChatColor.RESET.toString() + ChatColor.DARK_RED.toString() +
                "A reload has been detected. We advise you to restart the server, as this command is very " +
                "unstable, and will definitely cause trouble with this plugin." +
                ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "]";

        consoleLog(msg);
        if (player != null) {
            player.sendMessage(msg);
            return;
        }

        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (onlinePlayer.isOp()) onlinePlayer.sendMessage(msg);
        }
    }

    public @Nullable Location getLocation(@NotNull String location) {
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
            return Bukkit.getWorlds().get(0).getSpawnLocation();

        // Return actual location
        return new Location(
                Bukkit.getWorld(worldName),
                section.getInt("x"),
                section.getInt("y"),
                section.getInt("z"),
                (float) section.getDouble("yaw"),
                (float) section.getDouble("pitch"));
    }

    public @NotNull FileConfiguration getConfig() {
        return UserLogin.plugin.getConfig();
    }

    public boolean normalMode() {
        return !Objects.requireNonNull(getConfig().
                getString("teleports.mode")).toUpperCase().equals("SAVE-POSITION");
    }

    public void joinAnnounce(@NotNull Player player, @Nullable String msg) {
        if (msg == null) return;
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (player != onlinePlayer)
                player.sendMessage(msg);
        }
    }

    public boolean sqlMode() {
        return getConfig().getBoolean("mysql.enabled");
    }

    public void consoleLog(@NotNull String msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }

    public void sendToServer(@NotNull Player player, @NotNull String server) {

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bout);
        try {

            out.writeUTF("Connect");
            out.writeUTF(server);

            player.sendPluginMessage(UserLogin.plugin, "BungeeCord", bout.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
