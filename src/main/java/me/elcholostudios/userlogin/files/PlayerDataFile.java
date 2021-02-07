package me.elcholostudios.userlogin.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class PlayerDataFile {

    private static File playerData;
    private static FileConfiguration playerDataConfig;

    public static void setup() {
        playerData = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("UserLogin")).getDataFolder(), "playerData.yml");

        if(!playerData.exists()){
            try {
                playerData.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        playerDataConfig = YamlConfiguration.loadConfiguration(playerData);
    }

    public static FileConfiguration get() {
        return playerDataConfig;
    }

    public static void reloadPlayerData() {
        playerDataConfig = YamlConfiguration.loadConfiguration(playerData);
    }

    public static void save() {
        try{
            playerDataConfig.save(playerData);
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Could not save file");
        }
    }
}
