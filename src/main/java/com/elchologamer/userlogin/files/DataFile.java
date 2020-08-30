package com.elchologamer.userlogin.files;

import com.elchologamer.userlogin.api.util.Configuration;

public class DataFile extends Configuration {

    public DataFile() {
        super("playerData.yml");
    }

    @Override
    public void registerDefaults() {
    }
}
