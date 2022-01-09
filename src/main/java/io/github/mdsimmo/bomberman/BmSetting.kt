package io.github.mdsimmo.bomberman

import io.github.mdsimmo.bomberman.game.GameSettings
import org.bukkit.configuration.file.FileConfiguration
import java.nio.file.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists

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
    private var tempGameData: String = "temp/game"
    private var tempPlayerData: String = "temp/player"
    private var defaultGameSettings: GameSettings = GameSettings()
    private var language: String = "messages.yml"

    fun gameSaves(): Path {
        val dir = Bomberman.instance.dataFolder.toPath().resolve(gameSaves)
        if (!dir.exists()) {
            dir.createDirectories()
        }
        return dir
    }

    fun tempGameData(): Path {
        val dir = Bomberman.instance.dataFolder.toPath().resolve(tempGameData)
        if (!dir.exists()) {
            dir.createDirectories()
        }
        return dir
    }

    fun tempPlayerData(): Path {
        val dir = Bomberman.instance.dataFolder.toPath().resolve(tempPlayerData)
        if (!dir.exists()) {
            dir.createDirectories()
        }
        return dir
    }

    fun language(): Path {
        return Bomberman.instance.dataFolder.toPath().resolve(language)
    }

    fun defaultGameSettings(): GameSettings {
        return defaultGameSettings.clone()
    }
}