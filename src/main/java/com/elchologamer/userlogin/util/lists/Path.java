package com.elchologamer.userlogin.util.lists;

public class Path {

    // Sections
    private static final String COMMANDS = "commands.";
    private static final String OTHER = "other.";
    private static final String COMMAND_ERRORS = COMMANDS + "errors.";
    private static final String MESSAGES = "messages.";
    private static final String WELCOME = MESSAGES + "welcome.";
    private static final String USAGES = COMMANDS + "usages.";

    private static final String SQL_STATES = COMMANDS + "sqlStates.";
    private static final String SQL = OTHER + "mysql.";
    private static final String SQL_ERRORS = SQL + "errors.";

    // Command errors
    public static final String PLAYER_ONLY = COMMAND_ERRORS + "player-only";

    // Commands messages
    public static final String SET = COMMANDS + "set";
    public static final String RELOAD = COMMANDS + "reload";
    public static final String SQL_STATE = COMMANDS + "sql-state";
    public static final String SQL_DISABLED = SQL_STATES + "disabled";
    public static final String SQL_CONNECTED = SQL_STATES + "connected";
    public static final String SQL_DISCONNECTED = SQL_STATES + "disconnected";

    // MySQL messages and errors
    public static final String SQL_SAVE_ERROR = SQL_ERRORS + "save-failed";
    public static final String SQL_CONNECTION_SUCCESS = SQL + "connected";
    public static final String SQL_DATA_SAVED = SQL + "data-saved";
    public static final String SQL_CONNECTION_ERROR = SQL_ERRORS + "connection-error";

    // Command usages
    public static final String USERLOGIN_USAGE = USAGES + "userlogin";
    public static final String LOGIN_USAGE = USAGES + "login";
    public static final String REGISTER_USAGE = USAGES + "register";

    // Player messages
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
