package io.github.mdsimmo.bomberman.commands

import io.github.mdsimmo.bomberman.commands.game.*
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text
import org.apache.commons.lang.math.DoubleRange
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.command.*
import org.bukkit.entity.Entity
import org.bukkit.entity.Player
import org.bukkit.util.StringUtil
import java.lang.RuntimeException

class BaseCommand : CommandGroup(null), TabCompleter, CommandExecutor {

    init {
        addChildren(
                DevInfo(this),
                Configure(this),
                GameCreate(this),
                GameInfo(this),
                GameJoin(this),
                GameLeave(this),
                GameDelete(this),
                RunStart(this),
                RunStop(this),
                GameList(this),
                GameReload(this)
        )
    }

    override fun name(): Message {
        return Message.of("bomberman")
    }

    override fun permission(): Permission {
        return Permissions.BASE
    }

    override fun description(): Message {
        return context(Text.BOMBERMAN_DESCRIPTION).format()
    }

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        val (arguments, flags) = separateFlags(args)
        if (flags.containsKey("?")) {
            help(sender, arguments, flags)
        } else {
            val target = flags["as"]
            if (target == null) {
                execute(sender, arguments, flags)
            } else if (target.startsWith("@")) {
                val targets = expandSelector(sender, target)
                if (targets == null) {
                    context(Text.COMMAND_BAD_SELECTOR).with("selector", target).sendTo(sender)
                    return true
                }
                for (selected in targets) {
                    execute(selected, arguments, flags)
                }
            } else {
                val player = Bukkit.matchPlayer(target).firstOrNull()
                if (player == null) {
                    context(Text.COMMAND_PLAYER_NOT_FOUND).with("player", target).sendTo(sender)
                    return true
                }
                execute(player, arguments, flags)
            }
        }
        return true
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<String>): List<String> {
        val (arguments, flags) = separateFlags(args)
        val currentlyTyping = args.last() // Will always have one.
        val allOptions = if (currentlyTyping.startsWith("-")) {
            val splitIndex = currentlyTyping.indexOf('=')
            if (splitIndex == -1) {
                flags(sender, arguments, flags).map { "-$it" } + "-?"
            } else {
                val key = currentlyTyping.substring(1, splitIndex)
                flagOptions(sender, key, arguments, flags)
                        .map { "-$key=$it" }
            }
        } else {
            options(sender, arguments)
        }
        return allOptions.filter {
            StringUtil.startsWithIgnoreCase(it, currentlyTyping)
        }.toList()
    }

    private fun separateFlags(args: Array<String>) : Pair<List<String>, Map<String, String>> {
        // Strip out options
        val (flagStrings, arguments) = args.partition {
            it.startsWith("-")
        }
        val flags = flagStrings.map {
            val separator = it.indexOf('=', 0)
            if (separator == -1) {
                Pair(it.substring(1), "")
            } else {
                // +1s are to skip "-" and "="
                Pair(it.substring(1, separator), it.substring(separator+1))
            }
        }.toMap()
        return Pair(arguments, flags)
    }

    private fun expandSelector(sender: CommandSender, target: String): List<CommandSender>? {
        if (!target.startsWith("@"))
            return null
        val type = target.getOrNull(1) ?: return null
        val selectors = if (target.length <= 2) {
            emptyMap()
        } else {
            if (target[2] != '[' || target.last() != ']')
                return null
            expandTargetSelectors(target.substring(2, target.length - 1)) ?: return null
        }
        val result: Pair<Collection<CommandSender>, Map<String, String>> = when (type) {
            's' -> Pair(listOf(sender), mapOf())
            'a' -> Pair(Bukkit.getOnlinePlayers(), mapOf())
            'p' -> Pair(Bukkit.getOnlinePlayers(), mapOf(Pair("sort", "nearest"), Pair("limit", "1")))
            'r' -> Pair(Bukkit.getOnlinePlayers(), mapOf(Pair("sort", "random"), Pair("limit", "1")))
            'e' -> Pair(Bukkit.getWorlds().flatMap{ it.entities }.toList(), emptyMap())
            else -> return null
        }
        val (targets, defaults) = result
        val alteredSelectors = selectors.toMutableMap()
        defaults.forEach { (k, v) -> alteredSelectors.putIfAbsent(k, v) }
        return filterTargets(sender, targets, alteredSelectors)
    }

    private fun filterTargets(source: CommandSender, targets: Collection<CommandSender>, selectors: Map<String, String>): List<CommandSender>? {
        // Grab the source location
        val sourceLocation = location(source)
        selectors.forEach {(key, value) ->
            when (key) {
                "x", "y", "z" -> {
                    val pos = value.toDoubleOrNull() ?: return null
                    when (key) {
                        "x" -> sourceLocation.x = pos
                        "y" -> sourceLocation.y = pos
                        "z" -> sourceLocation.z = pos
                    }
                }
            }
        }

        // Construct a list of filters
        val preFilters: Set<(CommandSender) -> Boolean> = selectors.mapNotNull { (key, value) ->
            val result: ((CommandSender) -> Boolean)? = when (key) {
                "distance" -> {
                    val range = ranged(value) ?: return null
                    { sender: CommandSender ->
                        val senderLoc = location(sender)
                        if (sourceLocation.world != senderLoc.world) {
                            false
                        } else {
                            val distance = sourceLocation.distance(senderLoc)
                            range.containsDouble(distance)
                        }
                    }
                }
                "dx", "dy", "dz" -> {
                    val dp = value.toDoubleOrNull() ?: return null
                    { sender: CommandSender ->
                        val senderLoc = location(sender)
                        when (value) {
                            "dx" -> sourceLocation.x <= senderLoc.x  && senderLoc.x <= sourceLocation.x+dp
                            "dy" -> sourceLocation.y <= senderLoc.y  && senderLoc.y <= sourceLocation.y+dp
                            "dz" -> sourceLocation.z <= senderLoc.z  && senderLoc.z <= sourceLocation.z+dp
                            else -> throw RuntimeException("Should never happen")
                        }
                    }
                }
                "scores" -> {
                    TODO("How to allow sub '='?")
                }
                "team" -> {
                    val inverse = value.startsWith("!")
                    val teamName = value.removePrefix("!")
                    val team = Bukkit.getScoreboardManager()?.mainScoreboard?.getTeam(teamName)?.entries
                    { it: CommandSender ->
                        (team != null && team.contains(it.name)) != inverse
                    }
                }
                "level" -> {
                    val range = ranged(value) ?: return null
                    { it: CommandSender ->
                        it is Player && range.containsDouble(it.level)
                    }
                }
                "gamemode" -> {
                    val inverse = value.startsWith("!")
                    val gameMode = value.removePrefix("!");
                    { it: CommandSender ->
                        it is Player && inverse != (it.gameMode.name.equals(gameMode, ignoreCase = true))
                    }
                }
                "name" -> {
                    val inverse = value.startsWith("!")
                    val name = value.removePrefix("!");
                    { it: CommandSender ->
                        (it.name == name) != inverse
                    }
                }
                "x_rotation", "y_rotation" -> {
                    val range = ranged(value) ?: return null
                    { it: CommandSender ->
                        val loc = location(it)
                        range.containsDouble(when(key) {
                            "x_rotation" -> loc.pitch
                            "y_rotation" -> loc.yaw
                            else -> throw RuntimeException("This should never happen")
                        })
                    }
                }
                "type" -> {
                    val inverse = value.startsWith("!")
                    val name = value.removePrefix("!").removePrefix("minecraft:");
                    { it: CommandSender ->
                        it is Entity && inverse != name.equals(it.type.getName(), ignoreCase = true)
                    }
                }
                "tag" -> {
                    val inverse = value.startsWith("!")
                    val name = value.removePrefix("!");
                    { it: CommandSender ->
                        it is Entity && inverse != it.scoreboardTags.contains(name)
                    }
                }
                "nbt" -> {
                    TODO("Cannot get NBT through Bukkit")
                }
                "advancements" -> {
                    TODO("Haven't bothered to implement advancement selection yet")
                }
                "predicate" -> {
                    TODO("Predicates not accessible through Bukkit?")
                }
                else -> null
            }
            result
        }.toSet()

        // Determine how to sort
        val sorter: Comparator<CommandSender>? = selectors.mapNotNull { (key, value) ->
            when (key) {
                "sort" -> {
                    val result: Comparator<CommandSender>? = when(value) {
                        "nearest" -> Comparator.comparing { sender: CommandSender -> sourceLocation.distanceSquared(location(sender))}
                        "furthest" -> Comparator.comparing{ sender: CommandSender -> sourceLocation.distanceSquared(location(sender))}.reversed()
                        "random" -> Comparator.comparing { _: CommandSender -> Math.random() }
                        "arbitrary" -> null
                        else -> null
                    }
                    result
                }
                else -> null
            }
        }.firstOrNull()

        val limit = selectors["limit"]?.toIntOrNull()

        var selected = targets
                .filter { sender -> preFilters.all { filter -> filter(sender) } }
        if (sorter != null)
            selected = selected.sortedWith(sorter)
        if (limit != null)
            selected = selected.take(limit)
        return selected.toList()
    }

    fun location(sender: CommandSender): Location {
        return if (sender is BlockCommandSender) {
            sender.block.location
        } else if (sender is Entity) {
            sender.location
        } else {
            Location(Bukkit.getWorlds().first(), 0.0, 0.0, 0.0)
        }
    }

    private fun ranged(arg: String): DoubleRange? {
        val range = arg.split("..")
        return when (range.size) {
            1 -> {
                val min = range[0].toDoubleOrNull() ?: return null
                DoubleRange(min, min)
            }
            2 -> {
                val s_min = range[0]
                val s_max = range[1]
                val min = if (s_min == "") Double.MIN_VALUE else s_min.toDoubleOrNull() ?: return null
                val max = if (s_max == "") Double.MAX_VALUE else s_max.toDoubleOrNull() ?: return null
                DoubleRange(min, max)
            }
            else -> null
        }
    }

    private fun expandTargetSelectors(selectors: String): Map<String, String>? {
        return selectors.split(',')
                .map { selector ->
                    val firstEquals = selector.indexOf('=', 0)
                    if (firstEquals == -1)
                        return null
                    Pair(selector.substring(0, firstEquals), selector.substring(firstEquals+1))
                }.toMap()
    }

}