package io.github.mdsimmo.bomberman

import io.github.mdsimmo.bomberman.game.GameSettings
import io.github.mdsimmo.bomberman.utils.RefectAccess
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.io.File
import java.util.*
import javax.annotation.Nonnull

class BmSetting : ConfigurationSerializable {
    var schematicsBuiltin: String = "schematics/builtin"
    var schematicsCustom: String = "schematics/custom"
    var gameSaves: String = "games"
    var defaultGameSettings: GameSettings = GameSettings()
    var language: String = "builtin"
    fun gameSaves(): File {
        val file = File(Bomberman.instance.dataFolder, gameSaves)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun builtinSaves(): File {
        val file = File(Bomberman.instance.dataFolder, schematicsBuiltin)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun customSaves(): File {
        val file = File(Bomberman.instance.dataFolder, schematicsCustom)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    override fun serialize(): Map<String, Any> {
        return HashMap<String, Any>(java.util.Map.of(
                "schematics-save.builtin", schematicsBuiltin,
                "schematics-save.custom", schematicsCustom,
                "game-saves", gameSaves,
                "default-game-settings", defaultGameSettings,
                "language", language
        ))
    }

    companion object {
        @RefectAccess
        fun deserialize(data: Map<String?, Any?>): BmSetting {
            val settings = BmSetting()
            settings.schematicsBuiltin = data["schematics-save.builtin"] as String
            settings.schematicsCustom = data["schematics-save.custom"] as String
            settings.gameSaves = data["game-saves"] as String
            settings.defaultGameSettings = data["default-game-settings"] as GameSettings
            settings.language = data["language"] as String
            return settings
        }
    }
}