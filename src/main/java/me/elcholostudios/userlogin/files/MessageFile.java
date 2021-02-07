package me.elcholostudios.userlogin.files;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class MessageFile {

    private static File messages;
    private static FileConfiguration messagesConfig;

    public static void setup() {
        messages = new File(Objects.requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("UserLogin")).getDataFolder(), "messages.yml");

        if(!messages.exists()){
            try {
                messages.createNewFile();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
        messagesConfig = YamlConfiguration.loadConfiguration(messages);
    }

    public static FileConfiguration get() {
        return messagesConfig;
    }

    public static void reloadMessagesData() {
        messagesConfig = YamlConfiguration.loadConfiguration(messages);
    }

    public static void save() {
        try{
            messagesConfig.save(messages);
        }catch(IOException e){
            e.printStackTrace();
            System.out.println("Could not save file");
        }
    }
}
