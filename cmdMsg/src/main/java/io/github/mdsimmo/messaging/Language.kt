package io.github.mdsimmo.messaging

import io.github.mdsimmo.bomberman.messaging.Expander
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.cmdmsg.Arg
import io.github.mdsimmo.cmdmsg.Msg
import org.bukkit.Location
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

interface Formatted { // Message
    override fun toString(): String
}

interface Thingy { // Formattable
    fun format(args: List<Formatted>)
}

class PlayerF(player: Player): Thingy { // PlayerWraper

    override fun format(args: List<Formatted>) {
        TODO("Not yet implemented")
    }
}
fun Player.wrap(): PlayerF {
    return PlayerF(this)
}

interface Unexpanded { // Contexted
    val text: String
    val context: Map<String, Thingy>
}

interface Phrase { // ?
    val name: String
    val default: String
    val context: Map<String, Thingy>
}

interface Language {
    fun translate(phrase: Phrase): Unexpanded
}


class Messenger(private val lang: Language) {
    fun send(phrase: Phrase, target: CommandSender) {
        val translated = lang.translate(phrase)
        val expanded: Formatted = Expander.expand(translated.text, translated.context).toString()
        target.sendMessage(expanded.asText())
    }
}

fun makePhrase(path: String, default: String): Phrase {
    return object : Phrase {
        override val name: String
            get() = path
        override val default: String
            get() = default
        override val context: Map<String, Thingy>
            get() = mapOf()
    }
}

inline fun <reified A: Thingy> makePhrase(path: String, default: String, aName: String): (A) -> Phrase {
    return { a->
        object : Phrase {
            override val name: String
                get() = path
            override val default: String
                get() = default
            override val context: Map<String, Thingy>
                get() = mapOf(Pair(aName, a))
        }
    }
}

fun <A: Any, B: Any> makePhrase(path: String, default: String, aName: String, bName: String): (A, B) -> Phrase {
    return { a ,b ->
        object : Phrase {
            override val name: String
                get() = path
            override val default: String
                get() = default
            override val context: Map<String, Any>
                get() = mapOf(Pair(aName, a), Pair(bName, b))
        }
    }
}

data class Cmd<T: CommandSender> (
        val location: Location,
        val target: T,
        val output: Messenger
) {
    fun send(msg: Phrase, target: CommandSender) {
        output.send(msg, target)
    }
}

data class CmdResult(
        val success: Boolean,
        val msg: Message
) {
    companion object {
        fun success(msg: Phrase)
    }
}

@Cmd("bm leave")
fun create(cmd: Cmd<Player>): CmdResult {

    @Msg("A Greeting")
    @Arg("player", "Who left")
    val leftEarly = makePhrase<PlayerF>("hello.world", "Greetings, {player}", "player")

    val player: Player = cmd.target
    cmd.send(leftEarly(player.wrap()), player)
    CmdResult.success(leftEarly(player.wrap()))
}

@Cmd("bm load")
fun load(cmd: Cmd<Player>, @Arg("bm") plugin: String, @Arg file: String): CmdResult {

    @Msg("A Greeting")
    @Arg("player", "Who left")
    val leftEarly = makePhrase<PlayerF>("hello.world", "Greetings, {player}", "player")

    val player: Player = cmd.target
    cmd.send(leftEarly(player.wrap()), player)
    CmdResult.success(leftEarly(player.wrap()))
}

@Cmd("bm create")
fun leave(cmd: Cmd<Player>): CmdResult {

    @Msg("A Greeting")
    @Arg("player", "Who left")
    val leftEarly = makePhrase<PlayerF>("hello.world", "Greetings, {player}", "player")

    val player: Player = cmd.target
    cmd.send(leftEarly(player.wrap()), player)
    CmdResult.success(leftEarly(player.wrap()))
}