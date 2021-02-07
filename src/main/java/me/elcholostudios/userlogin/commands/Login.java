package me.elcholostudios.userlogin.commands;

import me.elcholostudios.userlogin.Essentials;
import me.elcholostudios.userlogin.UserLogin;
import me.elcholostudios.userlogin.files.PlayerDataFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class Login implements CommandExecutor {

    private final Essentials es = new Essentials();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();
            if(es.isRegistered(player)){
                boolean isLoggedIn = PlayerDataFile.get().getBoolean(uuid+".isLoggedIn");
                if(!isLoggedIn) {
                    if (args.length == 1) {
                        String password = PlayerDataFile.get().getString(uuid + ".password");
                        if (UserLogin.plugin.getConfig().getBoolean("passwords.encryptPasswords")) {
                            assert password != null;
                            password = es.decrypt(password);
                        }
                        if (args[0].equals(password)) {
                            //Update name if necessary
                            if(!Objects.equals(UserLogin.plugin.getConfig().getString(uuid + ".name"), player.getName())) {
                                PlayerDataFile.get().set(uuid + ".name", player.getName());
                            }

                            //Set operator permissions if enabled
                            if (es.getConfig().getBoolean("restrictions.disableOpWhenQuit") && PlayerDataFile.get().getBoolean(uuid + ".isOp")) {
                                player.setOp(true);
                            }

                            //Set "isLoggedIn" as true
                            PlayerDataFile.get().set(uuid + ".isLoggedIn", true);
                            PlayerDataFile.save();

                            es.sendMessage(player, "display-messages.login-successful", null, null);

                            //Teleport to lobby if enabled
                            boolean tp = UserLogin.plugin.getConfig().getBoolean("teleport.lobbyTeleportOnLogin");
                            if (tp) {
                                es.teleportToLobby(player);
                            }
                        } else {
                            es.sendMessage(player, "command-errors.incorrect-password", null, null);
                        }
                    } else {
                        es.sendMessage(player, "command-errors.incorrect-usage", null, null);
                    }
                }else{
                    es.sendMessage(player, "command-errors.already-logged", null, null);
                }
            }else{
                es.sendMessage(player, "command-errors.not-registered", null, null);
            }
        }else{
            es.sendMessage(null, "command-errors.player-only", null, null);
        }
        return true;
    }
}
