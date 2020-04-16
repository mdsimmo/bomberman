package io.github.mdsimmo.bomberman.game;

import org.junit.Test;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.Assert.assertEquals;

public class ExplosionTest {

    @Test
    public void lootSelectWithSingleItemAlwaysGetThatItem() {
        for (int i = 0; i < 100; i++) {
            Map<String, Number> loot = Map.of("diamond", 1);
            var got = Explosion.lootSelect(loot);

            assertEquals(Set.of("diamond"), got);
        }
    }

    @Test
    public void lootSelectWithNoLootGetsNothing() {
        for (int i = 0; i < 100; i++) {
            Map<String, Number> loot = Map.of();
            var got = Explosion.lootSelect(loot);

            assertEquals(Set.of(), got);
        }
    }

    @Test
    public void lootSelectIsWeightedUnbiased() {
        AtomicInteger diamonds = new AtomicInteger();
        AtomicInteger dirt = new AtomicInteger();
        var loot = Map.of(diamonds, 1, dirt, 3);

        for (int i = 0; i < 1000; i++) {
            var got = Explosion.lootSelect(loot);
            got.forEach(AtomicInteger::incrementAndGet);
        }

        assertEquals(diamonds.get(), 250, 30);
        assertEquals(dirt.get(), 750, 30);
    }
}