package com.elchologamer.userlogin.util;

import com.elchologamer.userlogin.UserLogin;
import com.elchologamer.userlogin.api.UserLoginAPI;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.context.ContextCalculator;
import net.luckperms.api.context.ContextConsumer;
import net.luckperms.api.context.ContextSet;
import net.luckperms.api.context.ImmutableContextSet;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;

public class LuckPermsContext implements ContextCalculator<Player> {
    public static final String key = "userlogin:has-logged-in";

    public static void registerLuckPermsContext(final UserLogin plugin) {
        final RegisteredServiceProvider<LuckPerms> provider = plugin.getServer().getServicesManager().getRegistration(LuckPerms.class);

        if (provider == null) {
            Utils.log("LuckPerms class was found, but provider was not registered yet?");
        } else {
            final LuckPermsContext contextCalculator = new LuckPermsContext();

            final LuckPerms luckPermsApi = provider.getProvider();
            luckPermsApi.getContextManager().registerCalculator(contextCalculator);

            Utils.log("Wow, LuckPerms! Contexts were registered");
        }
    }

    @Override
    public void calculate(final Player target, final ContextConsumer contextConsumer) {
        contextConsumer.accept(key, Boolean.toString(UserLoginAPI.isLoggedIn(target)));
    }

    @Override
    public ContextSet estimatePotentialContexts() {
        final ImmutableContextSet.Builder builder = ImmutableContextSet.builder().add(key, "true").add(key, "false");
        return builder.build();
    }

}