# FAQ

Frequently asked questions

<small>[Go back](/{{ site.github.repository_name }}/en)</small>

<hr />

## I changed the config, but no changes are taking effect!

Make sure to use the `/ul reload` command for config changes to take effect. If that doesn't work, check the server console for any errors.

## How can I make players be teleported back to their last position after logging in?

Enable `teleports.savePosition` in your config.

```yaml
teleports:
  # ...
  savePosition: true
  # ...
```

## The movement restriction isn't working!

Check your `config.yml` file. A common mistake is to put the following:

```yaml
restrictions:
  # ...
  movement: true # <-- THIS IS AN ERROR! You can't put values on section names!
    enabled: false
    warnFrequency: 20
  # ...
```

If your `restrictions` section looks like that, fix it by changing it to the following:

```yaml
restrictions:
  # ...
  movement: # <-- Remove any value you might have put here
    enabled: true # <-- Set this to true instead
    warnFrequency: 20
  # ...
```

## ¿How can I make premium players bypass the login?

UserLogin has support for the [FastLogin](https://www.spigotmc.org/resources/fastlogin.14153/) plugin, which provides auto-login for premium players. To enable this feature, simply add FastLogin to your server. Do note that FastLogin no longer releases new versions in the SpigotMC website directly, instead using an external site. You can download its latest version [here](https://ci.codemc.io/job/Games647/job/FastLogin/).

## Players have to log in again after using the `/reload` Spigot command!

There is no fix for this. The `/reload` command [is known to cause various issues and bugs, such as memory leaks, conflicts with some plugins, and instabilities](https://madelinemiller.dev/blog/problem-with-reload/). If you just want to reload the plugin, use `/ul reload` instead.

## How to I set up the plugin for BungeeCord?

Note that there's a difference between Spigot/Bukkit and BungeeCord plugins. UserLogin is a Spigot plugin. Therefore, you **have to add it to the plugins of one the network's servers, not in the plugins of the network itself**. For reference, you should put it in the server where you want users to log in.

To send players to another server after logging in, look for the `bungeeCord` section in your config:

```yaml
bungeeCord:
  enabled: true
  spawnServer: '[name of the destination server]'
```
