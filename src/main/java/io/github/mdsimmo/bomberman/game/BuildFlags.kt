package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.utils.RefectAccess
import org.bukkit.configuration.serialization.ConfigurationSerializable

class BuildFlags : ConfigurationSerializable {
    var skipAir = false
    var deleteVoid = false

    override fun serialize(): Map<String, Any> {
        return mapOf(
                Pair("skip-air", skipAir),
                Pair("delete-void", deleteVoid)
        )
    }

    companion object {
        @JvmStatic
        @RefectAccess
        fun deserialize(data: Map<String, Any?>): BuildFlags {
            val buildFlags = BuildFlags()
            (data["skip-air"] as? Boolean)?.let { buildFlags.skipAir = it }
            (data["delete-void"] as? Boolean)?.let { buildFlags.deleteVoid = it }
            return buildFlags
        }
    }
}