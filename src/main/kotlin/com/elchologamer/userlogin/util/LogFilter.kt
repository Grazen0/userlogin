package com.elchologamer.userlogin.util

import org.apache.logging.log4j.Level
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Marker
import org.apache.logging.log4j.core.Filter
import org.apache.logging.log4j.core.LifeCycle
import org.apache.logging.log4j.core.LogEvent
import org.apache.logging.log4j.core.Logger
import org.apache.logging.log4j.message.Message

internal object LogFilter : Filter {

    fun register() {
        val logger = LogManager.getRootLogger() as Logger
        logger.addFilter(this)
    }

    private fun filter(message: String): Filter.Result {
        return if (message.matches(Regex("(.+) issued server command: /(?i)(login|register|changepassword)(.*)"))) {
            Filter.Result.DENY
        } else {
            Filter.Result.NEUTRAL
        }
    }

    override fun getOnMismatch() = Filter.Result.NEUTRAL
    override fun getOnMatch() = Filter.Result.NEUTRAL
    override fun filter(logger: Logger, level: Level, marker: Marker, s: String, vararg objects: Any) = filter(s)
    override fun filter(logger: Logger, level: Level, marker: Marker, s: String, o: Any) = filter(s)
    override fun filter(logger: Logger, level: Level, marker: Marker, s: String, o: Any, o1: Any) = filter(s)

    override fun filter(
        logger: Logger,
        level: Level,
        marker: Marker,
        s: String,
        o: Any,
        o1: Any,
        o2: Any
    ) = filter(s)

    override fun filter(
        logger: Logger,
        level: Level,
        marker: Marker,
        s: String,
        o: Any,
        o1: Any,
        o2: Any,
        o3: Any
    ) = filter(s)

    override fun filter(
        logger: Logger,
        level: Level,
        marker: Marker,
        s: String,
        o: Any,
        o1: Any,
        o2: Any,
        o3: Any,
        o4: Any
    ) = filter(s)

    override fun filter(
        logger: Logger,
        level: Level,
        marker: Marker,
        s: String,
        o: Any,
        o1: Any,
        o2: Any,
        o3: Any,
        o4: Any,
        o5: Any
    ) = filter(s)

    override fun filter(
        logger: Logger,
        level: Level,
        marker: Marker,
        s: String,
        o: Any,
        o1: Any,
        o2: Any,
        o3: Any,
        o4: Any,
        o5: Any,
        o6: Any
    ) = filter(s)

    override fun filter(
        logger: Logger,
        level: Level,
        marker: Marker,
        s: String,
        o: Any,
        o1: Any,
        o2: Any,
        o3: Any,
        o4: Any,
        o5: Any,
        o6: Any,
        o7: Any
    ) = filter(s)

    override fun filter(
        logger: Logger,
        level: Level,
        marker: Marker,
        s: String,
        o: Any,
        o1: Any,
        o2: Any,
        o3: Any,
        o4: Any,
        o5: Any,
        o6: Any,
        o7: Any,
        o8: Any
    ) = filter(s)

    override fun filter(
        logger: Logger,
        level: Level,
        marker: Marker,
        s: String,
        o: Any,
        o1: Any,
        o2: Any,
        o3: Any,
        o4: Any,
        o5: Any,
        o6: Any,
        o7: Any,
        o8: Any,
        o9: Any
    ) = filter(s)

    override fun filter(logger: Logger, level: Level, marker: Marker, o: Any, throwable: Throwable) =
        filter(o.toString())

    override fun filter(
        logger: Logger,
        level: Level,
        marker: Marker,
        message: Message,
        throwable: Throwable
    ) = filter(message.formattedMessage)

    override fun filter(e: LogEvent): Filter.Result = filter(e.message.formattedMessage)

    override fun getState() = LifeCycle.State.STARTED

    override fun initialize() {}

    override fun start() {}
    override fun stop() {}

    override fun isStarted() = true
    override fun isStopped() = false
}