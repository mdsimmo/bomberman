package io.github.mdsimmo.bomberman

import io.github.mdsimmo.bomberman.game.GameSettings
import io.github.mdsimmo.bomberman.utils.RefectAccess
import org.bukkit.configuration.serialization.ConfigurationSerializable
import java.io.File

class BmSetting : ConfigurationSerializable {
    private var schematicsBuiltin: String = "schematics/builtin"
    private var schematicsCustom: String = "schematics/custom"
    private var gameSaves: String = "games"
    private var tempGameData: String = "temp/game"
    private var tempPlayerData: String = "temp/player"
    private var defaultGameSettings: GameSettings = GameSettings()
    private var language: String = "builtin"

    fun gameSaves(): File {
        val file = File(Bomberman.instance.dataFolder, gameSaves)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun tempGameData(): File {
        val file = File(Bomberman.instance.dataFolder, tempGameData)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun tempPlayerData(): File {
        val file = File(Bomberman.instance.dataFolder, tempPlayerData)
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
        return mapOf(
                Pair("schematics-save.builtin", schematicsBuiltin),
                Pair("schematics-save.custom", schematicsCustom),
                Pair("game-saves", gameSaves),
                Pair("temp-data.game", tempGameData),
                Pair("temp-data.player", tempPlayerData),
                Pair("default-game-settings", defaultGameSettings),
                Pair("language", language)
        )
    }

    companion object {
        @RefectAccess
        fun deserialize(data: Map<String?, Any?>): BmSetting {
            val settings = BmSetting()
            (data["schematics-save.builtin"] as? String?)?.let { settings.schematicsBuiltin = it }
            (data["schematics-save.custom"] as? String?)?.let { settings.schematicsCustom = it }
            (data["game-saves"] as? String?)?.let { settings.gameSaves = it }
            (data["temp-data.game"] as? String?)?.let { settings.tempGameData = it }
            (data["temp-data.player"] as? String?)?.let { settings.tempPlayerData = it }
            (data["default-game-settings"] as? GameSettings?)?.let { settings.defaultGameSettings = it }
            (data["language"] as? String?)?.let { settings.language = it }
            return settings
        }
    }
}