package io.github.mdsimmo.bomberman.utils;

import javax.annotation.CheckReturnValue;

@CheckReturnValue
public final class Dim {
    public final int x, y, z;

    public Dim(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public int hashCode() {
        int hash = 31;
        hash = hash * 31 + x;
        hash = hash * 31 + y;
        hash = hash * 31 + z;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Dim) {
            Dim other = (Dim) obj;
            return other.x == x && other.y == y && other.z == z;
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return "Dim{" + x + ", " + y + ", " + z + '}';
    }

    public int volume() {
        return x * y * z;
    }
}
