package com.elcholostudios.userlogin.util.lists;

public class Path {

    // Sections
    private static final String COMMANDS = "commands.";
    private static final String COMMAND_ERRORS = COMMANDS + "errors.";
    private static final String MESSAGES = "messages.";
    private static final String WELCOME = MESSAGES + "welcome.";
    public static final String USAGES = COMMANDS + "usages.";

    // Command errors
    public static final String PLAYER_ONLY = COMMAND_ERRORS + "player-only";

    // Commands
    public static final String SET = COMMANDS + "set";
    public static final String RELOAD = COMMANDS + "reload";

    // Usages
    public static final String USERLOGIN_USAGE = USAGES + "userlogin";
    public static final String LOGIN_USAGE = USAGES + "login";
    public static final String REGISTER_USAGE = USAGES + "register";

    // Messages
    public static final String NOT_REGISTERED = MESSAGES + "not-registered";
    public static final String ALREADY_REGISTERED = MESSAGES + "already-registered";
    public static final String ALREADY_LOGGED_IN = MESSAGES + "already-logged-in";
    public static final String INCORRECT_PASSWORD = MESSAGES + "incorrect-password";
    public static final String SHORT_PASSWORD = MESSAGES + "short-password";
    public static final String DIFFERENT_PASSWORDS = MESSAGES + "different-passwords";
    public static final String LOGGED_IN = MESSAGES + "logged-in";
    public static final String REGISTERED = MESSAGES + "registered";
    public static final String CHAT_DISABLED = MESSAGES + "chat-disabled";
    public static final String TIMEOUT = MESSAGES + "timeout";
    public static final String HELP = MESSAGES + "help";
    public static final String LOGIN_ANNOUNCEMENT = MESSAGES + "login-announcement";

    // Welcome messages
    public static final String WELCOME_LOGIN = WELCOME + "registered";
    public static final String WELCOME_REGISTER = WELCOME + "unregistered";
}
