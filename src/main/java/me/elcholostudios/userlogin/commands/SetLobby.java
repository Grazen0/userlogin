package me.elcholostudios.userlogin.commands;

import me.elcholostudios.userlogin.Essentials;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class SetLobby implements CommandExecutor{

    final Essentials es = new Essentials();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            if(player.hasPermission("userLogin.setLogin")){
                if(args.length == 0) {
                    String world = player.getWorld().getName();
                    Location loc = player.getLocation();
                    double x = loc.getX();
                    double y = loc.getY();
                    double z = loc.getZ();
                    float yaw = loc.getYaw();
                    float pitch = loc.getPitch();

                    es.getConfig().set("lobbySpawn.world", world);
                    es.getConfig().set("lobbySpawn.x", x);
                    es.getConfig().set("lobbySpawn.y", y);
                    es.getConfig().set("lobbySpawn.z", z);
                    es.getConfig().set("lobbySpawn.yaw", yaw);
                    es.getConfig().set("lobbySpawn.pitch", pitch);
                    es.saveConfig();

                    es.sendMessage(player, "commands.lobby-set",
                            new String[]{"{x}", "{y}", "{z}", "{yaw}", "{pitch}", "{world}"},
                            new String[]{es.roundAndString(x), es.roundAndString(y), es.roundAndString(z),
                                    es.roundAndString(yaw), es.roundAndString(pitch), world});
                }else{
                    es.sendMessage(player, "command-errors.incorrect-usage", null, null);
                }
            }else{
                es.sendMessage(player, "command-errors.no-permission", null, null);
            }
        }else{
            es.sendMessage(null, "command-errors.player-only", null, null);
        }
        return true;
    }
}
