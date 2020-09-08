package com.elchologamer.userlogin.util.files;

import com.elchologamer.userlogin.api.util.Configuration;

public class LocationsFile extends Configuration {

    public LocationsFile() {
        super("locations.yml");
    }

    @Override
    public void registerDefaults() {
        get().addDefault("login.x", 0);
        get().addDefault("login.y", 0);
        get().addDefault("login.z", 0);
        get().addDefault("login.yaw", 0);
        get().addDefault("login.pitch", 0);
        get().addDefault("login.world", "DEFAULT");
        get().addDefault("spawn.x", 0);
        get().addDefault("spawn.y", 0);
        get().addDefault("spawn.z", 0);
        get().addDefault("spawn.yaw", 0);
        get().addDefault("spawn.pitch", 0);
        get().addDefault("spawn.world", "DEFAULT");

        get().options().copyDefaults(true);
        save();
    }
}
