package com.elchologamer.userlogin.listener

import com.elchologamer.userlogin.UserLogin.Companion.plugin
import org.bukkit.event.Listener

abstract class BaseListener : Listener {
    fun register() {
        plugin.server.pluginManager.registerEvents(this, plugin)
    }
}