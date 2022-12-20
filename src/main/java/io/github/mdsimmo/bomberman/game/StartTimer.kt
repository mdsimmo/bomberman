package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.events.BmRunStartCountDownIntent
import io.github.mdsimmo.bomberman.events.BmRunStartedIntent
import io.github.mdsimmo.bomberman.events.BmRunStoppedIntent
import io.github.mdsimmo.bomberman.events.BmTimerCountedEvent
import io.github.mdsimmo.bomberman.messaging.Context
import io.github.mdsimmo.bomberman.messaging.Text
import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.HandlerList
import org.bukkit.event.Listener

class StartTimer private constructor(private val game: Game, private var time: Int) : Runnable, Listener {

    companion object {
        private val plugin = Bomberman.instance

        fun createTimer(game: Game, time: Int) {
            Bukkit.getPluginManager().registerEvents(
                    StartTimer(game, time),
                    plugin)
        }
    }

    private var killed = false
    private val taskID: Int = plugin.server.scheduler
            .scheduleSyncRepeatingTask(plugin, this, 1, 20)

    override fun run() {
        if (killed) {
            return
        }

        val e = BmTimerCountedEvent(game, time)
        Bukkit.getPluginManager().callEvent(e)
        time = e.count
        if (time > 0) {
            --time // the next count
        } else {
            BmRunStartedIntent.startRun(game)
            killSelf()
        }
    }

    private fun killSelf() {
        killed = true
        Bukkit.getScheduler().cancelTask(taskID)
        HandlerList.unregisterAll(this)
    }

    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onTimerStarted(e: BmRunStartCountDownIntent) {
        if (e.game != game)
            return
        if (e.override) {
            killSelf()
        } else {
            e.cancelBecause(Text.GAME_ALREADY_COUNTING.format(Context(false)
                    .plus("game", game)
                    .plus("time", time)))
        }
    }

    // Must be run before Game::onRunStoppedWhileNotRunning
    @EventHandler(ignoreCancelled = true, priority = EventPriority.LOW)
    fun onGameStop(e: BmRunStoppedIntent) {
        if (e.game != game)
            return
        killSelf()
        e.cancelFor(Text.STOP_TIMER_STOPPED.format(Context(false)
                .plus("time", time)
                .plus("game", game)))
    }
}