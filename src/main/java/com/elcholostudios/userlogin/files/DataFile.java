package com.elcholostudios.userlogin.files;

import com.elcholostudios.userlogin.api.Configuration;

public class DataFile extends Configuration {

    public DataFile() {
        super("playerData.yml");
    }

    @Override
    public void registerDefaults() { }
}
