package com.everlastingutils

import com.everlastingutils.command.CommandTester
import com.everlastingutils.config.ConfigTester
import com.everlastingutils.gui.GuiTester
import com.everlastingutils.scheduling.SchedulerManager
import com.everlastingutils.utils.LogDebugTester
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object EverlastingUtils : ModInitializer {
    private val logger = LoggerFactory.getLogger("everlastingutils")
    const val MOD_ID = "everlastingutils"
    const val VERSION = "1.1.6"

    object Colors {
        private const val ESC = "\u001B"
        private const val BOLD = "${ESC}[1m"

        private fun color(text: String, colorCode: String): String {
            return "$colorCode$text${ESC}[39m${if (colorCode.contains(BOLD)) "${ESC}[22m" else ""}"
        }

        fun boldPurple(text: String) = color(text, "${ESC}[1;35m")
        fun boldBrightPink(text: String) = color(text, "${ESC}[1;95m")
        fun brightBlack(text: String) = color(text, "${ESC}[90m")
        fun boldYellow(text: String) = color(text, "${ESC}[1;33m")
        fun boldGreen(text: String) = color(text, "${ESC}[1;32m")
        fun boldRed(text: String) = color(text, "${ESC}[1;31m")
        fun brightPurple(text: String) = color(text, "${ESC}[95m")
        fun white(text: String) = color(text, "${ESC}[0m")
    }

    private fun getModName(): String {
        return Colors.boldBrightPink("everlasting") + Colors.boldPurple("utils")
    }

    private fun getStatusBadge(passed: Boolean): String {
        return if (passed) Colors.boldGreen("[PASS]") else Colors.boldRed("[FAIL]")
    }

    override fun onInitialize() {
        val timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"))

        ServerLifecycleEvents.SERVER_STARTING.register {
            SchedulerManager.onServerStarting(it)
        }

        ServerLifecycleEvents.SERVER_STOPPING.register {
            SchedulerManager.shutdownAll()
        }

        val configPass = ConfigTester.runAllTests().values.all { it }
        val guiPass = GuiTester.runAllTests().values.all { it }
        val cmdPass = CommandTester.runAllTests().values.all { it }
        val logPass = LogDebugTester.runAllTests().values.all { it }

        val runtime = Runtime.getRuntime()
        val usedMem = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024
        val maxMem = runtime.maxMemory() / 1024 / 1024

        logger.info("")
        logger.info("${Colors.boldPurple("╔")} ${getModName()} ${Colors.brightPurple("v$VERSION")} ${Colors.boldPurple("══════════════════════════════════════════════════")}")
        logger.info("${Colors.boldPurple("║")} ${Colors.boldYellow("Systems Check")} :: " +
                "Config:${getStatusBadge(configPass)} " +
                "GUI:${getStatusBadge(guiPass)} " +
                "Cmds:${getStatusBadge(cmdPass)} " +
                "Logs:${getStatusBadge(logPass)} " +
                "Sched:${Colors.boldGreen("[READY]")}")
        logger.info("${Colors.boldPurple("║")} ${Colors.boldYellow("Environment")}   :: " +
                Colors.brightBlack("Java: ${System.getProperty("java.version")} | ") +
                Colors.brightBlack("Mem: ${usedMem}MB/${maxMem}MB | ") +
                Colors.brightBlack("Time: $timestamp"))
        logger.info("${Colors.boldPurple("║")} ${Colors.brightBlack("https://github.com/Hysocs/everlastingutils")} ${Colors.boldPurple("|")} ${Colors.brightBlack("https://discord.gg/MP78vg7tJs")}")
        logger.info("${Colors.boldPurple("╚═══════════════════════════════════════════════════════════════════════════")}")
        logger.info("")
    }
}
