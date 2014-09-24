package io.github.mdsimmo.bomberman;

import java.util.Collection;
import java.util.Iterator;

public class Utils {

	public static <T> T random(Collection<T> collection) {
		int random = (int) (Math.random() * collection.size());
		Iterator<T> iterator = collection.iterator();
		int i = 0;
		T obj = null;
		while (iterator.hasNext()) {
			obj = iterator.next();
			i++;
			if (i >= random)
				return obj;
		}
		return null;
	}
}
