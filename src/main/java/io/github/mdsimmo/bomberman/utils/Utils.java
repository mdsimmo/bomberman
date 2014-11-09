package io.github.mdsimmo.bomberman.utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public class Utils {

	private static StringBuffer buffer = new StringBuffer(); 
	
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
	
	/**
	 * Turns the list into a string. Each argument is seperated by a space
	 * @param list the list to become a string
	 * @return the string list
	 */
	public static String listToString(List<String> list) {
		buffer.setLength(0);
		for (String s : list) {
			if (buffer.length() != 0)
				buffer.append(' ');
			buffer.append(s);
		}
		return buffer.toString();
	}
	
	public static Object[] insert(Object[] initial, Object ... inserts) {
		Object[] fin = new Object[initial.length + inserts.length];
		for (int i = 0; i < inserts.length; i++)
			fin[i] = inserts[i];
		for (int i = 0; i < initial.length; i++)
			fin[inserts.length + i] = initial[i];
		return fin;
	}
}
