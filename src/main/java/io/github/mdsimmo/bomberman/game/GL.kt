package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.messaging.Formattable


/**
 * A Game or a Lobby
 *
 * Games and Lobbies are just refered to in events than actually used, thus, this class is more of
 * a "reference" than an implementation, thus it has no methods. There's code smell somewhere...
 */
// TODO need a better name for GL
interface GL : Formattable {
    fun name(): String
}