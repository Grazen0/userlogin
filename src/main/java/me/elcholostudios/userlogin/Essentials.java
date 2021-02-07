package me.elcholostudios.userlogin;

import me.elcholostudios.userlogin.files.MessageFile;
import me.elcholostudios.userlogin.files.PlayerDataFile;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Essentials {
    private final Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");

    public void sendMessage(@Nullable Player player, @NotNull String path, String @Nullable [] replace, String[] replacement){
        String message = color(Objects.requireNonNull(MessageFile.get().getString(path)));
        if(replace != null){
            for(int i=0;i<replace.length;i++){
                message = message.replace(replace[i], replacement[i]);
            }
        }

        if(player != null) {
            player.sendMessage(message);
        }else{
            Bukkit.getServer().getConsoleSender().sendMessage(message);
        }
    }

    public @NotNull String color(@NotNull String msg) {
        if(Bukkit.getVersion().contains("1.16")){
            Matcher match = pattern.matcher(msg);
            while(match.find()){
                String color = msg.substring(match.start(), match.end());
                msg = msg.replace(color, net.md_5.bungee.api.ChatColor.of(color) + "");
                match = pattern.matcher(msg);
            }
        }
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public @NotNull FileConfiguration getConfig() {
        return UserLogin.plugin.getConfig();
    }

    public void saveConfig() {
        UserLogin.plugin.saveConfig();
    }

    public void teleportToLobby(@NotNull Player player) {
        String world = getConfig().getString("lobbySpawn.world");

        assert world != null;
        if(world.equals("default")){
            world = Bukkit.getServer().getWorlds().get(0).getName();
        }

        double x = getConfig().getDouble("lobbySpawn.x");
        double y = getConfig().getDouble("lobbySpawn.y");
        double z = getConfig().getDouble("lobbySpawn.z");
        float yaw = (float) getConfig().getDouble("lobbySpawn.yaw");
        float pitch = (float) getConfig().getDouble("lobbySpawn.pitch");
        Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);

        player.teleport(loc, PlayerTeleportEvent.TeleportCause.PLUGIN);
    }

    public @NotNull String encrypt(@NotNull String message) {
        StringBuilder finalStr = new StringBuilder("§");


        for(int i = 0; i< message.length(); i++){
            String finalChar = message.split("")[i];
            finalChar = changeChar(finalChar);
            finalStr.append(finalChar);
        }

        return finalStr.toString();
    }

    public @NotNull String decrypt(@NotNull String message) {
        StringBuilder finalStr = new StringBuilder();
        String copied = color(message);


        for(int i=0;i<copied.length();i++){
            String finalChar = copied.split("")[i];
            finalChar = changeChar(finalChar);
            finalStr.append(finalChar);
        }

        return finalStr.toString();
    }

    public boolean isRegistered(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();
        return PlayerDataFile.get().contains(uuid + ".password");
    }

    public String roundAndString(double d) {
        DecimalFormat df = new DecimalFormat("#.##");
        df.setRoundingMode(RoundingMode.CEILING);
        double finalDouble = 0;
        for (Number n : Collections.singletonList(d)) {
            finalDouble = n.doubleValue();
        }
        return df.format(finalDouble);
    }

    private String changeChar(@NotNull String oldChar) {
        String finalChar;
        switch(oldChar){
            case "A":
                finalChar = "Z";
                break;
            case "B":
                finalChar = "Y";
                break;
            case "C":
                finalChar = "X";
                break;
            case "D":
                finalChar = "W";
                break;
            case "E":
                finalChar = "V";
                break;
            case "F":
                finalChar = "U";
                break;
            case "G":
                finalChar = "T";
                break;
            case "H":
                finalChar = "S";
                break;
            case "I":
                finalChar = "R";
                break;
            case "J":
                finalChar = "Q";
                break;
            case "K":
                finalChar = "P";
                break;
            case "L":
                finalChar = "O";
                break;
            case "M":
                finalChar = "N";
                break;
            case "N":
                finalChar = "M";
                break;
            case "O":
                finalChar = "L";
                break;
            case "P":
                finalChar = "K";
                break;
            case "Q":
                finalChar = "J";
                break;
            case "R":
                finalChar = "I";
                break;
            case "S":
                finalChar = "H";
                break;
            case "T":
                finalChar = "G";
                break;
            case "U":
                finalChar = "F";
                break;
            case "V":
                finalChar = "E";
                break;
            case "W":
                finalChar = "D";
                break;
            case "X":
                finalChar = "C";
                break;
            case "Y":
                finalChar = "B";
                break;
            case "Z":
                finalChar = "A";
                break;
            case "a":
                finalChar = "z";
                break;
            case "b":
                finalChar = "y";
                break;
            case "c":
                finalChar = "x";
                break;
            case "d":
                finalChar = "w";
                break;
            case "e":
                finalChar = "v";
                break;
            case "f":
                finalChar = "u";
                break;
            case "g":
                finalChar = "t";
                break;
            case "h":
                finalChar = "s";
                break;
            case "i":
                finalChar = "r";
                break;
            case "j":
                finalChar = "q";
                break;
            case "k":
                finalChar = "p";
                break;
            case "l":
                finalChar = "o";
                break;
            case "m":
                finalChar = "n";
                break;
            case "n":
                finalChar = "m";
                break;
            case "o":
                finalChar = "l";
                break;
            case "p":
                finalChar = "k";
                break;
            case "q":
                finalChar = "j";
                break;
            case "r":
                finalChar = "i";
                break;
            case "s":
                finalChar = "h";
                break;
            case "t":
                finalChar = "g";
                break;
            case "u":
                finalChar = "f";
                break;
            case "v":
                finalChar = "e";
                break;
            case "w":
                finalChar = "d";
                break;
            case "x":
                finalChar = "c";
                break;
            case "y":
                finalChar = "b";
                break;
            case "z":
                finalChar = "a";
                break;
            case "1":
                finalChar = "0";
                break;
            case "2":
                finalChar = "9";
                break;
            case "3":
                finalChar = "8";
                break;
            case "4":
                finalChar = "7";
                break;
            case "5":
                finalChar = "6";
                break;
            case "6":
                finalChar = "5";
                break;
            case "7":
                finalChar = "4";
                break;
            case "8":
                finalChar = "3";
                break;
            case "9":
                finalChar = "2";
                break;
            case "0":
                finalChar = "1";
                break;
            default:
                finalChar = oldChar;
                break;
        }

        return finalChar;
    }
}
