package io.github.mdsimmo.bomberman.utils

import javax.annotation.CheckReturnValue

@CheckReturnValue
class Dim(val x: Int, val y: Int, val z: Int) {

    override fun hashCode(): Int {
        var hash = 31
        hash = hash * 31 + x
        hash = hash * 31 + y
        hash = hash * 31 + z
        return hash
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Dim) {
            other.x == x && other.y == y && other.z == z
        } else {
            false
        }
    }

    override fun toString(): String {
        return "Dim{$x, $y, $z}"
    }

    fun volume(): Int {
        return x * y * z
    }

}