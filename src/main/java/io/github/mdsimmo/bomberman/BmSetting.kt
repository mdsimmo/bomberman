package io.github.mdsimmo.bomberman

import io.github.mdsimmo.bomberman.game.GameSettings
import org.bukkit.configuration.file.FileConfiguration
import java.io.File

class BmSetting private constructor() {

    companion object {
        @JvmStatic
        fun load(data: FileConfiguration): BmSetting {
            val settings = BmSetting()
            (data["schematics-save.builtin"] as? String?)?.let { settings.schematicsBuiltin = it }
            (data["schematics-save.custom"] as? String?)?.let { settings.schematicsCustom = it }
            (data["game-saves"] as? String?)?.let { settings.gameSaves = it }
            (data["temp-data.game"] as? String?)?.let { settings.tempGameData = it }
            (data["temp-data.player"] as? String?)?.let { settings.tempPlayerData = it }
            (data["default-game-settings"] as? GameSettings?)?.let {
                settings.defaultGameSettings = it
            }
            (data["language"] as? String?)?.let { settings.language = it }
            return settings
        }
    }

    private var schematicsBuiltin: String = "schematics/builtin"
    private var schematicsCustom: String = "schematics/custom"
    private var gameSaves: String = "games"
    private var lobbySaves: String = "lobby"
    private var tempGameData: String = "temp/game"
    private var tempPlayerData: String = "temp/player"
    private var defaultGameSettings: GameSettings = GameSettings()
    private var language: String = "messages.yml"

    fun gameSaves(): File {
        val file = File(Bomberman.instance.dataFolder, gameSaves)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    fun lobbySaves(): File {
        val file = File(Bomberman.instance.dataFolder, lobbySaves)
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

    fun language(): File {
        return File(Bomberman.instance.dataFolder, language)
    }

    fun defaultGameSettings(): GameSettings {
        return defaultGameSettings.clone()
    }
}