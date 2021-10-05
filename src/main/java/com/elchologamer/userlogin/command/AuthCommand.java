package com.elchologamer.userlogin.command;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.types.AuthType;
import com.elchologamer.userlogin.command.base.BaseCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public abstract class AuthCommand extends BaseCommand {

    private final AuthType type;
    private final int minArgs;
    private final UserLogin plugin = UserLogin.getPlugin();

    public AuthCommand(String name, AuthType type, int minArgs) {
        super(name, true);
        this.type = type;
        this.minArgs = minArgs;
    }

    protected abstract boolean authenticate(ULPlayer player, String[] args);

    public UserLogin getPlugin() {
        return plugin;
    }

    @Override
    public final boolean run(CommandSender sender, String label, String[] args) {
        ULPlayer ulPlayer = plugin.getPlayer((Player) sender);

        // Check if player is already logged in
        if (ulPlayer.isLoggedIn()) {
            ulPlayer.sendMessage("messages.already_logged_in");
            return true;
        }

        // Check usage
        if (args.length < minArgs) return false;

        // Authenticate player
        if (authenticate(ulPlayer, args)) ulPlayer.onAuthenticate(type);

        return true;
    }


}
