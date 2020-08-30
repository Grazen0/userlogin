package com.elchologamer.userlogin.util.files;

import com.elchologamer.userlogin.api.util.Configuration;

public class LocationsFile extends Configuration {

    public LocationsFile() {
        super("locations.yml");
    }

    @Override
    public void registerDefaults() {
        this.get().addDefault("login.x", 0);
        this.get().addDefault("login.y", 0);
        this.get().addDefault("login.z", 0);
        this.get().addDefault("login.yaw", 0);
        this.get().addDefault("login.pitch", 0);
        this.get().addDefault("login.world", "DEFAULT");
        this.get().addDefault("spawn.x", 0);
        this.get().addDefault("spawn.y", 0);
        this.get().addDefault("spawn.z", 0);
        this.get().addDefault("spawn.yaw", 0);
        this.get().addDefault("spawn.pitch", 0);
        this.get().addDefault("spawn.world", "DEFAULT");

        this.get().options().copyDefaults(true);
        this.save();
    }
}
