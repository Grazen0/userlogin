package com.elchologamer.userlogin.listener;

import com.elchologamer.userlogin.ULPlayer;
import com.elchologamer.userlogin.UserLogin;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class VelocityMsgListener extends JoinQuitListener implements PluginMessageListener, Listener {
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("userlogin:uuid")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String uuid = in.readUTF();
        String returned = in.readUTF();
        ULPlayer.get(UUID.fromString(uuid)).onJoin(returned.equals("true"));
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (UserLogin.getPlugin().getConfig().getBoolean("disableVanillaJoinMessages")) {
            event.setJoinMessage(null);
        }
    }
}
