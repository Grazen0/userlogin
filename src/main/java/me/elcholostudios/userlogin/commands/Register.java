package me.elcholostudios.userlogin.commands;

import me.elcholostudios.userlogin.Essentials;
import me.elcholostudios.userlogin.UserLogin;
import me.elcholostudios.userlogin.files.PlayerDataFile;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Register implements CommandExecutor {

    final Essentials es = new Essentials();

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {
        if(sender instanceof Player){
            Player player = (Player) sender;
            String uuid = player.getUniqueId().toString();
            if(!es.isRegistered(player)) {
                if (args.length == 2) {
                    if(args[0].equals(args[1])){
                        if(args[0].length() >= UserLogin.plugin.getConfig().getInt("passwords.minimumCharacters")) {
                            //Create and set paths for player data
                            PlayerDataFile.get().createSection(uuid + ".name");
                            PlayerDataFile.get().set(uuid + ".name", player.getName());
                            PlayerDataFile.get().createSection(uuid + ".password");
                            String password = args[0];
                            if (UserLogin.plugin.getConfig().getBoolean("passwords.encryptPasswords")) {
                                password = es.encrypt(password);
                            }
                            PlayerDataFile.get().set(uuid + ".password", password);
                            PlayerDataFile.get().createSection(uuid + ".isOp");
                            PlayerDataFile.get().set(uuid + ".isOp", false);

                            PlayerDataFile.get().set(uuid+".isLoggedIn", true);
                            PlayerDataFile.save();

                            PlayerDataFile.save();

                            es.sendMessage(player, "display-messages.register-successful", null, null);

                            boolean tp = UserLogin.plugin.getConfig().getBoolean("teleport.lobbyTeleportOnLogin");
                            if (tp) {
                                es.teleportToLobby(player);
                            }
                        }else{
                            String min = Integer.toString(UserLogin.plugin.getConfig().getInt("passwords.minimumCharacters"));
                            es.sendMessage(player, "command-errors.few-characters",
                                    new String[]{"{min}"}, new String[]{min});
                        }
                        }else{
                            es.sendMessage(player, "command-errors.non-matching-passwords", null, null);
                        }
                    }else{
                        es.sendMessage(player, "command-errors.incorrect-usage", null, null);
                    }
            }else{
                es.sendMessage(player, "command-errors.already-registered", null, null);
            }
        }else{
            es.sendMessage(null, "command-errors.player-only", null, null);
        }
        return true;
    }
}
