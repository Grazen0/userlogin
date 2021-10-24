package com.elchologamer.userlogin.listener;

import com.elchologamer.userlogin.ULPlayer;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.UUID;

public class VelocityMessageListener implements PluginMessageListener, Listener {

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] message) {
        if (!channel.equals("userlogin:main")) return;
        ByteArrayDataInput in = ByteStreams.newDataInput(message);
        String uuid = in.readUTF();
        ULPlayer.get(UUID.fromString(uuid)).onJoin(true);
    }
}
