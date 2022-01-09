package io.github.mdsimmo.bomberman.utils

import org.bukkit.configuration.serialization.ConfigurationSerializable

/**
 * Stores the data passed into it from a configuration section.
 * Useful for retrieving data from classes that do not exist anymore
 */
class DataRestorer @RefectAccess constructor(val data: Map<String?, Any?>) : ConfigurationSerializable {

    override fun serialize(): Map<String, Any> {
        return mapOf()
    }

}