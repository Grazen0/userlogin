package com.elcholostudios.userlogin.files;

import com.elcholostudios.userlogin.api.util.Configuration;

public class DataFile extends Configuration {

    public DataFile() {
        super("playerData.yml");
    }

    @Override
    public void registerDefaults() { }
}
