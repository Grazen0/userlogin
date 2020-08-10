package com.elcholostudios.userlogin.files;

import com.elcholostudios.userlogin.util.Configuration;
import com.elcholostudios.userlogin.util.lists.Path;

import java.util.ArrayList;
import java.util.List;

public class MessagesFile extends Configuration {

    public MessagesFile() {
        super("messages");
    }

    @Override
    public void registerDefaults() {
        this.get().addDefault(Path.PLAYER_ONLY, "&cThis is a player-only command");
        this.get().addDefault(Path.SET, "&eThe {type} location has been set at &bX: {x}, Y: {y}, Z: {z}, Y: {yaw}, P: {pitch}, &eat the world &b\"{world}\"");
        this.get().addDefault(Path.RELOAD, "&eConfigurations reloaded!");
        this.get().addDefault(Path.NOT_REGISTERED, "&cYou are not registered! Use &a/register <password> <password> &cto get registered!");
        this.get().addDefault(Path.ALREADY_REGISTERED, "&cYou are already registered!");
        this.get().addDefault(Path.INCORRECT_PASSWORD, "&4Incorrect password!");
        this.get().addDefault(Path.SHORT_PASSWORD, "&cYour password must have a minimum of &4{chars} &ccharacters");
        this.get().addDefault(Path.DIFFERENT_PASSWORDS, "&cYour passwords don't match!");
        this.get().addDefault(Path.ALREADY_LOGGED_IN, "&cYou are already logged in!");
        this.get().addDefault(Path.LOGGED_IN, "&9Login successful!");
        this.get().addDefault(Path.REGISTERED, "&9You have been registered!");
        this.get().addDefault(Path.WELCOME_LOGIN, "&6Welcome! Use &3/login <password> &6to enter the server");
        this.get().addDefault(Path.WELCOME_REGISTER, "&6Welcome! Use &3/register <password> <password> &6to register your account");
        this.get().addDefault(Path.CHAT_DISABLED, "&cYou must first log in to use the chat!");
        this.get().addDefault(Path.TIMEOUT, "You have stayed for too much time without logging in");

        List<String> help = new ArrayList<>();
        help.add("&a----------- | &2[UserLogin Help] &a| -----------");
        help.add("&2/ul help: &aShows this help list");
        help.add("&2/ul reload: &aShows this help list");
        help.add("&2/ul set <login>|<spawn>: &aSets the specified location at your position");
        help.add("&a-----------------------------------------------------");

        this.get().addDefault(Path.HELP, help);

        this.get().options().copyDefaults(true);
        this.save();
    }
}
