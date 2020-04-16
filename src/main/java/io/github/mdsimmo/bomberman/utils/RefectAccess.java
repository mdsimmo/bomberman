package io.github.mdsimmo.bomberman.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * States that the item is designed to be accessed by reflection. Can be used to tell the IDE warnings to shut up
 */
@Retention(value= RetentionPolicy.SOURCE)
public @interface RefectAccess {
}
