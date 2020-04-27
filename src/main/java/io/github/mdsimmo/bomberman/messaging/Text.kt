package io.github.mdsimmo.bomberman.messaging

import io.github.mdsimmo.bomberman.Bomberman
import org.bukkit.configuration.InvalidConfigurationException
import org.bukkit.configuration.file.YamlConfiguration
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.Reader
import java.util.*

/**
 * A list of Contexteds that are defined in english.yml in the resource folder.
 */
enum class Text(path: String) : Contexted {
    MESSAGE_FORMAT("format.message"),
    HEADING_FORMAT("format.heading"),
    MAP_FORMAT("format.map"),
    LIST_FORMAT("format.list"),
    ITEM_FORMAT("format.item"),
    PLAYER_WON("game-play.player-won"),
    GAME_COUNT("game-play.count"),
    GAME_STARTED("game-play.started"),
    DENY_PERMISSION("command.deny-permission"),
    INCORRECT_USAGE("command.incorrect-usage"),
    UNKNOWN_COMMAND("command.unknown-command"),
    MUST_BE_PLAYER("command.must-be-player"),
    INVALID_NUMBER("command.invalid-number"),
    COMMAND_GROUP_HELP("command.group.help"),
    COMMAND_GROUP_USAGE("command.group.usage"),
    COMMAND_GROUP_EXAMPLE("command.group.example"),
    COMMAND_GROUP_EXTRA("command.group.extra"),
    COMMAND_HELP("command.help"),
    COMMAND_CANCELLED("command.cancelled"),
    BOMBERMAN_DESCRIPTION("command.bomberman.description"),
    START_NAME("command.start.name"),
    START_DESCRIPTION("command.start.description"),
    START_USAGE("command.start.usage"),
    START_EXAMPLE("command.start.example"),
    START_EXTRA("command.start.extra"),
    START_FLAG_OVERRIDE_DESC("command.start.flags.o.description"),
    START_FLAG_DELAY_DESC("command.start.flags.d.description"),
    START_FLAG_DELAY_EXT("command.start.flags.d.ext"),
    GAME_ALREADY_STARTED("command.start.already-started"),
    GAME_ALREADY_COUNTING("command.start.already-counting"),
    GAME_START_SUCCESS("command.start.success"),
    STOP_NAME("command.stop.name"),
    STOP_DESCRIPTION("command.stop.description"),
    STOP_USAGE("command.stop.usage"),
    STOP_EXAMPLE("command.stop.example"),
    STOP_EXTRA("command.stop.extra"),
    STOP_NOT_STARTED("command.stop.not-started"),
    STOP_SUCCESS("command.stop.success"),
    STOP_TIMER_STOPPED("command.stop.timer-stopped"),
    RELOAD_NAME("command.reload.name"),
    RELOAD_DESCRIPTION("command.reload.description"),
    RELOAD_USAGE("command.reload.usage"),
    RELOAD_EXAMPLE("command.reload.example"),
    RELOAD_EXTRA("command.reload.extra"),
    RELOAD_SUCCESS("command.reload.success"),
    RELOAD_CANNOT_LOAD("command.reload.cannot-load"),
    LIVES_NAME("command.lives.name"),
    LIVES_DESCRIPTION("command.lives.description"),
    LIVES_USAGE("command.lives.usage"),
    LIVES_EXAMPLE("command.lives.example"),
    LIVES_EXTRA("command.lives.extra"),
    LIVES_SUCCESS("command.lives.success"),
    INVENTORY_NAME("command.inventory.name"),
    INVENTORY_DESCRIPTION("command.inventory.description"),
    INVENTORY_USAGE("command.inventory.usage"),
    INVENTORY_EXAMPLE("command.inventory.example"),
    INVENTORY_EXTRA("command.inventory.extra"),
    INVENTORY_SUCCESS("command.inventory.success"),
    INVENTORY_NEED_CREATIVE("command.inventory.need-creative"),
    BLOCK_TYPES_NAME("command.block-types.name"),
    BLOCK_TYPES_DESCRIPTION("command.block-types.description"),
    BLOCK_TYPES_USAGE("command.block-types.usage"),
    BLOCK_TYPES_EXAMPLE("command.block-types.example"),
    BLOCK_TYPES_EXTRA("command.block-types.extra"),
    BLOCK_TYPES_SUCCESS("command.block-types.success"),
    BLOCK_TYPES_NEED_CREATIVE("command.block-types.need-creative"),
    SET_NAME("command.set.name"),
    SET_DESCRIPTION("command.set.description"),
    CREATE_NAME("command.create.name"),
    CREATE_DESCRIPTION("command.create.description"),
    CREATE_USAGE("command.create.usage"),
    CREATE_EXAMPLE("command.create.example"),
    CREATE_EXTRA("command.create.extra"),
    CREATE_GAME_EXISTS("command.create.game-exists"),
    CREATE_NEED_SELECTION("command.create.need-selection"),
    CREATE_SUCCESS("command.create.success"),
    CREATE_SCHEMA_NOT_FOUND("command.create.schema-not-found"),
    CREATE_FLAG_PLUGIN("command.create.flags.p.description"),
    CREATE_FLAG_SCHEMA("command.create.flags.f.description"),
    CREATE_FLAG_SKIP_AIR("command.create.flags.a.description"),
    CREATE_FLAG_VOID_TO_AIR("command.create.flags.v.description"),
    CREATE_FLAG_PLUGIN_EXT("command.create.flags.p.ext"),
    CREATE_FLAG_SCHEMA_EXT("command.create.flags.f.ext"),
    DELETE_NAME("command.delete.name"),
    DELETE_DESCRIPTION("command.delete.description"),
    DELETE_USAGE("command.delete.usage"),
    DELETE_EXAMPLE("command.delete.example"),
    DELETE_EXTRA("command.delete.extra"),
    DELETE_SUCCESS("command.delete.success"),
    GAMELIST_NAME("command.list.name"),
    GAMELIST_DESCRIPTION("command.list.description"),
    GAMELIST_USAGE("command.list.usage"),
    GAMELIST_EXAMPLE("command.list.example"),
    GAMELIST_EXTRA("command.list.extra"),
    GAMELIST_GAMES("command.list.games"),
    INFO_NAME("command.info.name"),
    INFO_DESCRIPTION("command.info.description"),
    INFO_USAGE("command.info.usage"),
    INFO_EXAMPLE("command.info.example"),
    INFO_EXTRA("command.info.extra"),
    INFO_DETAILS("command.info.details"),
    JOIN_NAME("command.join.name"),
    JOIN_DESCRIPTION("command.join.description"),
    JOIN_USAGE("command.join.usage"),
    JOIN_EXAMPLE("command.join.example"),
    JOIN_EXTRA("command.join.extra"),
    JOIN_GAME_STARTED("command.join.game-started"),
    JOIN_ALREADY_JOINED("command.join.already-joined"),
    JOIN_GAME_FULL("command.join.game-full"),
    JOIN_SUCCESS("command.join.success"),
    LEAVE_NAME("command.leave.name"),
    LEAVE_DESCRIPTION("command.leave.description"),
    LEAVE_USAGE("command.leave.usage"),
    LEAVE_EXAMPLE("command.leave.example"),
    LEAVE_EXTRA("command.leave.extra"),
    LEAVE_SUCCESS("command.leave.success"),
    LEAVE_NOT_JOINED("command.leave.not-joined");

    private val text: String

    init {
        // any error's here should always get caught during development
        text = YAMLLanguage.server?.getString(path)
                ?: YAMLLanguage.builtin.getString(path)
                ?: throw RuntimeException("No default message for text: $path")
    }

    override fun with(key: String, thing: Formattable): Contexted {
        return object : Contexted {
            var things: MutableMap<String, Formattable> = HashMap()

            override fun with(key: String, thing: Formattable): Contexted {
                things[key] = thing
                return this
            }

            override fun format(): Message {
                return Expander.expand(text, things)
            }
        }.with(key, thing)
    }

    override fun format(): Message {
        return Expander.expand(text, mapOf())
    }

    companion object {
        fun getSection(path: String): Contexted {
            val text = YAMLLanguage.server?.getString(path)
                    ?: YAMLLanguage.builtin.getString(path)
            val things = mutableMapOf<String, Formattable>()
            return object : Contexted {
                override fun with(key: String, thing: Formattable): Contexted {
                    things[key] = thing
                    return this
                }

                override fun format(): Message {
                    return if (text == null) {
                        Message.error("{${path}}")
                    } else {
                        Expander.expand(text, things)
                    }
                }

            }
        }
    }

    // class to read the English language text from
    private object YAMLLanguage {
        val builtin: YamlConfiguration
        val server: YamlConfiguration?

        init {
            val input = Text::class.java.classLoader.getResourceAsStream("english.yml")!!
            val reader: Reader =
                    BufferedReader(InputStreamReader(input))
            builtin = YamlConfiguration()
            server = YamlConfiguration()
            try {
                builtin.load(reader)
                Bomberman.instance?.let {
                    val custom = it.settings.language()
                    if (custom.exists())
                        server.load(custom)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (e: InvalidConfigurationException) {
                e.printStackTrace()
            }
        }
    }
}