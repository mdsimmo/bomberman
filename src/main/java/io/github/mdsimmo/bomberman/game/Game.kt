package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.Bomberman
import io.github.mdsimmo.bomberman.Config
import io.github.mdsimmo.bomberman.arena.ArenaTemplate
import io.github.mdsimmo.bomberman.arena.ArenaGenerator
import io.github.mdsimmo.bomberman.game.gamestate.GameRun
import io.github.mdsimmo.bomberman.game.gamestate.GameWaitingState
import io.github.mdsimmo.bomberman.messaging.Formattable
import io.github.mdsimmo.bomberman.messaging.*
import io.github.mdsimmo.bomberman.utils.Box
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

import java.io.File
import java.util.*

class Game : Formattable {
    private var name: String
    private var box: Box
    private var arena: ArenaTemplate
    private val settings = GameSettings()
    private val oldArena: ArenaTemplate
    private val protector: GameProtection
    private var run: GameRun

    constructor(name: String, box: Box) {
        name = name
        protector = GameProtection(this)
        oldArena = ArenaGenerator.from(box)
        run = GameRun()
    }

    /**
     * Destroys the game's state and deletes its save file. Also deletes the old game boards
     * save file. Does **not** switch the arena back to its original state. Use [.resetArena]
     * to do that.
     */
    fun destroy() {
        GameRegestry.remove(this)
        run.destroy()
        protector.destroy()
        val gameSave = File(plugin.dataFolder, "$name.game")
        gameSave.delete()
        ArenaGenerator.remove(oldArena.name)
        val boardsSave = File(plugin.dataFolder, "$name.old.arena")
        boardsSave.delete()
    }

    fun drop(l: Location, type: Material) {
        if (Math.random() < dropChance && arena!!.isDropping(type)) {
            var sum = 0
            for (stack in drops)
                sum += stack.getAmount()
            var rand = Math.random() * sum
            for (stack in drops) {
                rand -= stack.getAmount().toDouble()
                if (rand <= 0) {
                    val drop = stack.clone()
                    drop.setAmount(1)
                    l.world!!.dropItem(l, drop)
                    return
                }
            }
        }
    }

    // announce scores
    private fun winnersDisplay() {
        for (rep in observers) {
            Chat.sendMessage(getMessage(Text.SCORE_ANNOUNCE, rep.getPlayer()))

            for (i in 0 until winners.size()) {
                val repWinner = winners.get(i)
                val place = i + 1
                Chat.messageRaw(getMessage(Text.WINNERS_LIST, rep.getPlayer())
                        .put("player", repWinner).put("place", place))
            }
        }
    }

    //	public List<Message> scoreDisplay( CommandSender sender ) {
    //		List<Message> list = new ArrayList<Message>( players.size() );
    //		for ( GamePlayer rep : players )
    //			list.add( getMessage( Text.SCORE_DISPLAY, sender ).put( "player",
    //					rep ).put( "stats", getStats( rep ) ) );
    //		return list;
    //	}

    fun setSuddenDeath(started: Boolean) {
        if (started)
            for (rep in players)
                rep.getPlayer().setHealth(1.0)
        suddenDeathStarted = started
    }

    fun setSuddenDeath(time: Int) {
        suddenDeath = time
    }

    fun setTimeout(time: Int) {
        timeout = time
    }

    /**
     * Starts the game with a default delay of 3 seconds
     *
     * @return true if the game was started successfully
     */
    fun startGame(): Boolean {
        return startGame(3, true)
    }

    /**
     * Starts the game with a given delay
     *
     * @return true if the game was started successfully
     */
    private fun startGame(delay: Int, override: Boolean): Boolean {
        if (players.size() >= settings.minPlayers) {
            if (override) {
                if (countdownTimer != null)
                    countdownTimer.destroy()
                countdownTimer = GameStarter(delay)
            }
            if (countdownTimer == null)
                countdownTimer = GameStarter(delay)
            return true
        } else {
            return false
        }
    }

    /**
     * Stops the game and kicks all the players out. Does not give awards.
     */
    fun stop() {
        state = GameWaitingState()

        for (player in ArrayList<Any>(players))
            player.removeFromGame()
    }

    fun getMessage(phrase: Phrase, sender: CommandSender): Message {
        return Chat.getMessage(phrase, sender).put("game", this)
    }

    fun messagePlayers(text: Phrase, values: Map<String, Any>?) {
        var values = values
        if (values == null)
            values = HashMap()

        for (player in players)
            Chat.sendMessage(getMessage(text, player.player).put(values))
    }

    override fun format(message: Message, args: MutableList<String>): String? {
        if (args.size == 0)
            return name
        when (args[0]) {
            "name" -> return name
            "minplayers" -> return Integer.toString(settings.minPlayers)
            "maxplayers" -> {
                var size = 0
                for (list in arena!!.spawnPoints.values())
                    size += list.size
                return Integer.toString(size)
            }
            "arena" -> {
                args.removeAt(0)
                return arena!!.format(message, args)
            }
            "players" -> return Integer.toString(players.size())
            "power" -> return Integer.toString(power)
            "bombs" -> return Integer.toString(bombs)
            "lives" -> return Integer.toString(lives)
            "fare" -> {
                args.removeAt(0)
                return settings.fare.format(message, args)
            }
            "prize" -> {
                args.removeAt(0)
                return prize.format(message, args)
            }
            "timeout" -> return Integer.toString(getTimeout())
            "suddendeath" -> return Integer.toString(getSuddenDeath())
            "autostart" -> return Integer.toString(getAutostartDelay())
            "hasautostart" -> return java.lang.Boolean.toString(getAutostart())
            "x" -> return Integer.toString(box.loc.x)
            "y" -> return Integer.toString(box.loc.y)
            "z" -> return Integer.toString(box.loc.z)
            "state" -> return state.toString().toLowerCase()
            else -> return null
        }
    }

    companion object {

        private val plugin = Bomberman.instance
    }
}
