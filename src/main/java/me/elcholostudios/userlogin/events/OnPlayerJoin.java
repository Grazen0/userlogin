package me.elcholostudios.userlogin.events;

import me.elcholostudios.userlogin.Essentials;
import me.elcholostudios.userlogin.UserLogin;
import me.elcholostudios.userlogin.files.MessageFile;
import me.elcholostudios.userlogin.files.PlayerDataFile;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.jetbrains.annotations.NotNull;

public class OnPlayerJoin implements Listener {

    final Essentials es = new Essentials();

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent e) {
        e.setJoinMessage("");
        Player player = e.getPlayer();

        //Teleport to loginSpawn if enabled
        boolean b = UserLogin.plugin.getConfig().getBoolean("teleport.loginTeleportOnJoin");
        if(b) {
            String world = UserLogin.plugin.getConfig().getString("loginSpawn.world", "default");
            assert world != null;
            if (world.equals("default")) {
                world = Bukkit.getServer().getWorlds().get(0).getName();
            }
            double x = UserLogin.plugin.getConfig().getDouble("loginSpawn.x");
            double y = UserLogin.plugin.getConfig().getDouble("loginSpawn.y");
            double z = UserLogin.plugin.getConfig().getDouble("loginSpawn.z");
            float yaw = (float) UserLogin.plugin.getConfig().getDouble("loginSpawn.yaw");
            float pitch = (float) UserLogin.plugin.getConfig().getDouble("loginSpawn.pitch");
            Location loc = new Location(Bukkit.getWorld(world), x, y, z, yaw, pitch);
            player.teleport(loc);
        }

        String uuid = player.getUniqueId().toString();

        //Disable operator permissions if enabled
        if(UserLogin.plugin.getConfig().getBoolean("restrictions.disableOpWhenQuit")){
            player.setOp(false);
        }

        //Set "player.isLoggedIn" to false
        if(!PlayerDataFile.get().contains(uuid+".isLoggedIn")){
            PlayerDataFile.get().createSection(uuid+".isLoggedIn");
        }
        PlayerDataFile.get().set(uuid+".isLoggedIn", false);
        PlayerDataFile.save();

        //Schedule timeout
        if(UserLogin.plugin.getConfig().getBoolean("timeOut.timeOutEnabled")) {
            long delay = es.getConfig().getLong("timeOut.timeOutSeconds") * 20;
            Bukkit.getScheduler().scheduleSyncDelayedTask(UserLogin.plugin, () -> kickPlayer(player), delay);
        }

        //Send welcome message
        if (!es.isRegistered(player)) {
            es.sendMessage(player, "display-messages.register-message",
                    new String[]{"{player}"}, new String[]{player.getName()});
        } else {
            es.sendMessage(player, "display-messages.login-message",
                    new String[]{"{player}"}, new String[]{player.getName()});
        }
    }

    private void kickPlayer(@NotNull Player player) {
        String uuid = player.getUniqueId().toString();
        long delay = UserLogin.plugin.getConfig().getLong("timeOut.timeOutSeconds") * 20;
        Bukkit.getScheduler().scheduleSyncDelayedTask(UserLogin.plugin, () -> {
            boolean isLoggedIn = PlayerDataFile.get().getBoolean(uuid+".isLoggedIn");
            if(!isLoggedIn) {
                String loginWorld = UserLogin.plugin.getConfig().getString("loginSpawn.world");
                if (loginWorld == null || loginWorld.equals("default")) {
                    loginWorld = Bukkit.getServer().getWorlds().get(0).getName();
                }
                if (player.getWorld().getName().equals(loginWorld)) {
                    String message = MessageFile.get().getString("display-messages.time-out");
                    assert message != null;
                    player.kickPlayer(es.color(message));
                }
            }
        }, delay);
    }
}
