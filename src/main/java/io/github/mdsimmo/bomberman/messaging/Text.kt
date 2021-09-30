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
    CONFIGURE_NAME("command.configure.name"),
    CONFIGURE_DESCRIPTION("command.configure.description"),
    CONFIGURE_USAGE("command.configure.usage"),
    CONFIGURE_EXAMPLE("command.configure.example"),
    CONFIGURE_EXTRA("command.configure.extra"),
    CONFIGURE_PROMPT_CREATIVE("command.configure.prompt-creative"),
    CONFIGURE_TITLE_MAIN("command.configure.title.main"),
    CONFIGURE_TITLE_GENERAL("command.configure.title.general"),
    CONFIGURE_TITLE_BLOCKS("command.configure.title.blocks"),
    CONFIGURE_TITLE_LOOT("command.configure.title.loot"),
    CONFIGURE_TITLE_INVENTORY("command.configure.title.inventory"),
    CONFIGURE_BACK("command.configure.back"),
    CONFIGURE_DESTRUCTIBLE("command.configure.blocks.destructible.name"),
    CONFIGURE_DESTRUCTIBLE_DESC("command.configure.blocks.destructible.description"),
    CONFIGURE_INDESTRUCTIBLE("command.configure.blocks.indestructible.name"),
    CONFIGURE_INDESTRUCTIBLE_DESC("command.configure.blocks.indestructible.description"),
    CONFIGURE_PASS_KEEP("command.configure.blocks.pass-keep.name"),
    CONFIGURE_PASS_KEEP_DESC("command.configure.blocks.pass-keep.description"),
    CONFIGURE_PASS_DESTROY("command.configure.blocks.pass-destroy.name"),
    CONFIGURE_PASS_DESTROY_DESC("command.configure.blocks.pass-destroy.description"),
    CONFIGURE_LIVES("command.configure.general.lives"),
    CONFIGURE_FUSE_TICKS("command.configure.general.fuse-ticks"),
    CONFIGURE_FIRE_TICKS("command.configure.general.fire-ticks"),
    CONFIGURE_IMMUNITY_TICKS("command.configure.general.immunity-ticks"),
    CONFIGURE_TNT_BLOCK("command.configure.general.tnt-block"),
    CONFIGURE_FIRE_ITEM("command.configure.general.fire-item"),
    CONFIGURE_LOOT_SLOT("command.configure.loot.slot"),
    CONFIGURE_LOOT_BLOCK("command.configure.loot.block"),
    CONFIGURE_LOOT_ITEM("command.configure.loot.item"),
    CONFIGURE_LOOT_WEIGHT("command.configure.loot.weight"),
    CREATE_NAME("command.create.name"),
    CREATE_DESCRIPTION("command.create.description"),
    CREATE_USAGE("command.create.usage"),
    CREATE_EXAMPLE("command.create.example"),
    CREATE_EXTRA("command.create.extra"),
    CREATE_GAME_EXISTS("command.create.game-exists"),
    CREATE_NEED_SELECTION("command.create.need-selection"),
    CREATE_SUCCESS("command.create.success"),
    CREATE_ERROR("command.create.error"),
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
    UNDO_NAME("command.undo.name"),
    UNDO_DESCRIPTION("command.undo.description"),
    UNDO_USAGE("command.undo.usage"),
    UNDO_EXAMPLE("command.undo.example"),
    UNDO_EXTRA("command.undo.extra"),
    UNDO_DELETED("command.undo.deleted"),
    UNDO_SUCCESS("command.undo.success"),
    UNDO_UNKNOWN_GAME("command.undo.unknown-game"),
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

            return if (text == null) {
                ErrorContext("{${path}}")
            } else {
                SimpleContext(text)
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
                // Instance may be null when testing
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