package io.github.mdsimmo.bomberman.utils;

import javax.annotation.CheckReturnValue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

/**
 * <p>A super simple compression algorithm for saving/restoring elements from a string.</p>
 *
 * <p>
 * Data format example:
 * 	"<code>aaa!2;bbb;ccc!3</code>"
 *
 * 	expands to:
 *
 * 	"<code>aaa;aaa;bbb;ccc;ccc;ccc</code>"
 * 	</p>
 */
public final class CompressedList {

	private static final char separator = ';';
	private static final char multiple = '!';
	private static final char escape = '\\';

	/**
	 * Given a string that was created with {@link #encode(Iterator, Function)}, this will restore the original stream
	 * @param data the encoded data
	 * @param decoder how to restore each string value
	 * @param <T> the type of object being restored
	 * @return the decoded stream
	 */
	@CheckReturnValue
	public static <T> List<T> decode(String data, Function<String, ? extends T> decoder) {
		List<T> parts = new ArrayList<>();
		StringBuilder section = new StringBuilder();
		boolean ignoreNextSpecial = false; // caused by backslash
		int repeats = 1;

		for (int i = 0; i < data.length(); i++) {
			char c = data.charAt(i);
			if (ignoreNextSpecial) {
				section.append(c);
				ignoreNextSpecial = false;
			} else {
				switch (c) {
					case separator:
						T part = decoder.apply(section.toString());
						for (int j = 0; j < repeats; j++) {
							parts.add(part);
						}
						section.setLength(0);
						repeats = 1;
						break;

					case escape:
						ignoreNextSpecial = true;
						break;

					case multiple:
						// Read out the number
						StringBuilder number = new StringBuilder();
						for (++i; i < data.length(); ++i) {
							char c2 = data.charAt(i);
							if (c2 == separator) {
								--i; // place cursor before semi-colen
								break;
							} else {
								number.append(c2);
							}
						}
						repeats = Integer.parseInt(number.toString());
						break;
					default:
						section.append(c);
				}
			}
		}

		return parts;
	}

	/**
	 * Converts each element to a string (through the encoder) and combines them into a string. Compresses duplications
	 * into the output. The exact format of the output is not guaranteed.
	 * @param objects A collection of objects to compress. Equal objects are tested or by the "equals" method
	 * @param encoder a function to convert an individual object into a string. A matching decoder must be provided to
	 *                   the {@link #decode(String, Function)} method. All characters are valid for use
	 * @param <T> The type of object being converted
	 * @return A string encoding the objects
	 */
	@CheckReturnValue
	public static <T> String encode(Iterator<T> objects, Function<? super T, String> encoder) {

		StringBuilder result = new StringBuilder();
		T prev = null;
		int quantity = 0;
		while (objects.hasNext()) {
			T next = objects.next();
			if (next.equals(prev)) {
				++quantity;
			} else {
				appendEncode(result, encoder, prev, quantity);
				prev = next;
				quantity = 1;
			}
		}

		appendEncode(result, encoder, prev, quantity);
		return result.toString();
	}

	private static <T> void appendEncode(StringBuilder output, Function<T, String> encoder, T value, int amount) {
		if (amount <= 0)
			return;
		String encoded = encoder.apply(value)
				.replace(String.valueOf(escape), escape + "" + escape)
				.replace(String.valueOf(separator), escape + "" + separator)
				.replace(String.valueOf(multiple), escape + "" + multiple);
		output.append(encoded);
		if (amount > 1) {
			output.append(multiple).append(amount);
		}
		output.append(separator);
	}

}