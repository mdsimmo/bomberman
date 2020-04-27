package io.github.mdsimmo.bomberman.commands.game.set

import io.github.mdsimmo.bomberman.commands.Cmd
import io.github.mdsimmo.bomberman.commands.CommandGroup
import io.github.mdsimmo.bomberman.commands.Permission
import io.github.mdsimmo.bomberman.commands.Permissions
import io.github.mdsimmo.bomberman.messaging.Message
import io.github.mdsimmo.bomberman.messaging.Text

class SetCmd(parent: Cmd) : CommandGroup(parent) {
    override fun name(): Message {
        return context(Text.SET_NAME).format()
    }

    override fun permission(): Permission {
        return Permissions.SET
    }

    override fun description(): Message {
        return context(Text.SET_DESCRIPTION).format()
    }

    init {
        addChildren( //new Schema(this),
                SetLives(this),
                SetBlockTypes(this),
                SetInventory(this)
        )
    }
}