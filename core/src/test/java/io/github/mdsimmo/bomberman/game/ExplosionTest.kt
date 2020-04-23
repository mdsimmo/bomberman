package io.github.mdsimmo.bomberman.game

import io.github.mdsimmo.bomberman.game.Explosion.Companion.lootSelect
import org.junit.Assert
import org.junit.Test
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer

class ExplosionTest {
    @Test
    fun lootSelectWithSingleItemAlwaysGetThatItem() {
        for (i in 0..99) {
            val loot = mapOf(Pair("diamond", 1))
            val got = lootSelect(loot)
            Assert.assertEquals(setOf("diamond"), got)
        }
    }

    @Test
    fun lootSelectWithNoLootGetsNothing() {
        for (i in 0..99) {
            val loot = mapOf<String, Number>()
            val got = lootSelect(loot)
            Assert.assertEquals(setOf<String>(), got)
        }
    }

    @Test
    fun lootSelectIsWeightedUnbiased() {
        val diamonds = AtomicInteger()
        val dirt = AtomicInteger()
        val loot = mapOf(
                Pair(diamonds, 1),
                Pair(dirt, 3)
        )

        for (i in 0..999) {
            val got = lootSelect(loot)
            got.forEach(Consumer { obj: AtomicInteger -> obj.incrementAndGet() })
        }

        Assert.assertEquals(diamonds.get().toFloat(), 250f, 40f)
        Assert.assertEquals(dirt.get().toFloat(), 750f, 40f)
    }
}