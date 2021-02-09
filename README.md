# UserLogin

![Banner][banner]

## About

Prevent players from having their accounts be used by other players! This plugin will add an authentication layer of security to your server in no time, and with just a bit of configuration.

## Features

- Easy player authentication.
- Password encryption for maximum security.
- FastLogin support for premium login.
- Fully customizable login and spawn coordinates.
- Fully customizable messages.
- Time-out functionality to kick AFK players.
- Supports a wide variety of databases.
- Togglable restrictions for non-logged in players, such as movement and chat.
- Multi-language support
- Full MySQL database support!
- Decent BungeeCord support

## Player commands

- **/login &lt;password&gt;**: If registered and the password is correct, it will teleport the player to the spawn location
- **/register &lt;password&gt; &lt;password&gt;**: Registers the player with the given password.

## Op-only commands

- **/ul help**: Shows the help list for all admin commands
- **/ul set [login|spawn]**: Sets the specified location at the player's exact position.
- **/ul reload**: Reloads the plugin and all the configuration files.
- **/ul unregister &lt;player&gt;**: Unregisters the specified player, deleting their password.

## Permissions

Permission nodes can be configured with the format ul.[command], or ul.\* for every permission.

## Configuration

The latest **config.yml** version can be found [**here**][config].

## bStats graph

![bStats graph][bstats]

## Support

A support [Discord server][support] is open for reporting any issues or suggestions.

[banner]: https://i.imgur.com/B91xrjs.png
[config]: https://github.com/ElCholoGamer/userlogin/blob/master/src/main/resources/config.yml
[bstats]: https://bstats.org/signatures/bukkit/UserLogin.svg
[support]: https://discord.gg/gbjaEDzRXU
