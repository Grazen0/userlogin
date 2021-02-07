package me.elcholostudios.userlogin.commands;

import me.elcholostudios.userlogin.Essentials;
import me.elcholostudios.userlogin.UserLogin;
import me.elcholostudios.userlogin.files.MessageFile;
import me.elcholostudios.userlogin.files.PlayerDataFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ReloadLogin implements CommandExecutor {

    final Essentials es = new Essentials();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(player.hasPermission("userLogin.reload")){
                update(player);
            }else{
                es.sendMessage(player, "command-errors.no-permission", null, null);
            }
        }else{
            update(null);
        }
        return true;
    }

    private void update(Player player) {
        try {
            PlayerDataFile.reloadPlayerData();
            MessageFile.reloadMessagesData();
            UserLogin.plugin.reloadConfig();
            UserLogin.updatePasswords();

            es.sendMessage(player, "commands.reload-successful", null, null);
        }catch(IllegalArgumentException e){
            es.sendMessage(player, "command-errors.reload-failed", null, null);
            e.printStackTrace();
        }
    }
}
