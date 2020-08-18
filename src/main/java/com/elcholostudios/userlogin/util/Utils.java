package com.elcholostudios.userlogin.util;

import com.elcholostudios.userlogin.UserLogin;
import com.elcholostudios.userlogin.util.lists.Path;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static final Map<UUID, Boolean> loggedIn = new HashMap<>();
    public static final Map<UUID, Integer> timeouts = new HashMap<>();

    public void cancelTimeout(Player player) {
        Integer id = timeouts.get(player.getUniqueId());
        if (id != null)
            Bukkit.getServer().getScheduler().cancelTask(id);
    }

    public void sendMessage(String path, CommandSender player) {
        sendMessage(path, player, new String[0], new String[0]);
    }

    public void sendMessage(String path, CommandSender player, String[] replace, String[] replacement) {
        String msg = UserLogin.messagesFile.get().getString(path);
        if (msg == null || msg.equals("")) return;
        msg = color(msg);

        // Replace variables
        for (int i = 0; i < replace.length; i++) {
            msg = msg.replace("{" + replace[i] + "}", replacement[i]);
        }

        player.sendMessage(msg);
    }

    public String color(String s) {
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

    public String decrypt(String password) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(Base64.getDecoder().decode(password.replaceFirst("§", "")));
            ObjectInputStream is = new ObjectInputStream(in);
            return (String) is.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error while trying to decrypt a password");
            e.printStackTrace();
            return password;
        }
    }

    public boolean isRegistered(Player player) {
        UUID uuid = player.getUniqueId();
        return (!sqlMode() && UserLogin.dataFile.get().getKeys(true).contains(uuid.toString() + ".password")) ||
                (sqlMode() && UserLogin.sql.data.containsKey(uuid));
    }

    public void updateName(Player player) {
        String uuid = player.getUniqueId().toString();
        if (!UserLogin.dataFile.get().getKeys(false).contains(uuid)) return;

        UserLogin.dataFile.get().set(uuid + ".name", player.getName());
        UserLogin.dataFile.save();
    }

    public void reloadWarn(Player player) {
        String msg = ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "[UserLogin: " +
                ChatColor.RESET.toString() + ChatColor.DARK_RED.toString() +
                "A reload has been detected. We advise you to restart the server, as this command is very " +
                "unstable, and will definitely cause trouble with this plugin." +
                ChatColor.GRAY.toString() + ChatColor.ITALIC.toString() + "]";

        consoleLog(msg);
        if (player != null)
            player.sendMessage(msg);
        else {
            for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
                if (onlinePlayer.isOp()) onlinePlayer.sendMessage(msg);
            }
        }
    }

    public Location getLocation(String location) {
        ConfigurationSection section = UserLogin.locationsFile.get().getConfigurationSection(location);
        if (section == null) return null;

        String worldName = section.getString("world");
        Location loc;
        if (worldName == null || worldName.equals("DEFAULT"))
            loc = Bukkit.getWorlds().get(0).getSpawnLocation();
        else {
            loc = new Location(
                    Bukkit.getWorld(worldName),
                    section.getInt("x"),
                    section.getInt("y"),
                    section.getInt("z"),
                    (float) section.getDouble("yaw"),
                    (float) section.getDouble("pitch"));
        }
        return loc;
    }

    public FileConfiguration getConfig() {
        return UserLogin.plugin.getConfig();
    }

    public boolean normalMode() {
        return !Objects.requireNonNull(getConfig().getString("teleports.mode")).toUpperCase().equals("SAVE-POSITION");
    }

    public void joinAnnounce(Player player) {
        for (Player onlinePlayer : Bukkit.getServer().getOnlinePlayers()) {
            if (player == onlinePlayer) continue;
            sendMessage(Path.LOGIN_ANNOUNCEMENT, onlinePlayer,
                    new String[]{"player"}, new String[]{player.getName()});
        }
    }

    public boolean sqlMode() {
        return getConfig().getBoolean("mysql.enabled");
    }

    public void consoleLog(String msg) {
        Bukkit.getServer().getConsoleSender().sendMessage(msg);
    }

    public void sendToServer(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(getConfig().getString("bungeeCord.spawnServer"));

        player.sendPluginMessage(UserLogin.plugin, "BungeeCord", out.toByteArray());
    }
}
