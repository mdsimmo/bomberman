package io.github.mdsimmo.bomberman.utils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * <p>A super simple compression algorithm for saving/restoring elements from a string.</p>
 *
 * <p>
 * Data format example:
 * 	"<code>!2;aaa;bbb;!3;ccc</code>"
 *
 * 	expands to:
 *
 * 	"<code>aaa;aaa;bbb;ccc;ccc;ccc</code>"
 * 	</p>
 */
public final class CompressedList {

	private static final char separator = ';';
	private static final char multiple = '!';
	private static final char escape = '/';

	/**
	 * Given a string that was created with {@link #encode(Iterator, Function)}, this will restore the original stream
	 * @param data the encoded data
	 * @param decoder how to restore each string value
	 * @param <T> the type of object being restored
	 * @return the decoded stream
	 */
	public static <T> List<T> decode(String data, Function<String, ? extends T> decoder) {
		List<T> parts = new ArrayList<>();

		// Split the string into parts
		StringBuilder section = new StringBuilder();
		boolean ignoreNext = false;
		boolean firstChar = true;
		int repeats = 1;

		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (ignoreNext) {
				section.append(c);
				firstChar = false;
			} else {
				switch (c) {
					case separator:
						T part = decoder.apply(section.toString());
						for (int j = 0; j < repeats; j++) {
							parts.add(part);
						}
						section.setLength(0);
						repeats = 1;
						firstChar = true;
						break;

					case escape:
						ignoreNext = true;
						break;

					case multiple:
						// only apply at the start of a string
						if (firstChar) {
							// Read out the number
							StringBuilder number = new StringBuilder();
							for (++i; i < data.length(); ++i) {
								char c2 = data.charAt(i);
								if (c2 == separator)
									break;
								else
									number.append(c2);
							}
							repeats = Integer.parseInt(number.toString());
							break;
						}
					default:
						section.append(c);
						firstChar = true;
				}
			}
		}

		// Add the final element
		T lastPart = decoder.apply(section.toString());
		for (int i = 0; i < repeats; i++)
			parts.add(lastPart);

		// TODO CompressedList stores all elements when reading string
		return parts;
	}

	public static <T> String encode(Iterator<T> objects, Function<T, String> encoder) {
		StringBuilder result = new StringBuilder();
		T prev = null;
		int duplicates = 0;
		while (objects.hasNext()) {
			T next = objects.next();
			if (next.equals(prev)) {
				++duplicates;
			} else {
				if (prev != null && duplicates != 0) {
					String encoded = encoder.apply(prev);
					if (duplicates > 1) {
						result.append(multiple).append(duplicates).append(separator);
					}
					result.append(encoded);
				}
				duplicates = 0;
				prev = next;
			}
		}
		return result.toString();
	}

}