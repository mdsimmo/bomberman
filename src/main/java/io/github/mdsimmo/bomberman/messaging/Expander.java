package io.github.mdsimmo.bomberman.messaging;

import org.bukkit.ChatColor;

import javax.annotation.CheckReturnValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

@CheckReturnValue
public final class Expander {

    private static Map<String, Formattable> functions = Map.of(
            "=", new Formattable.Equation(),
            "switch", new Formattable.Switch(),
            "map", new Formattable.MapExpander(),
            "list", new Formattable.ListExpander(),
            "heading", new Formattable.HeadingExpander(),
            "title", new Formattable.TitleExpander(),
            "raw", new Formattable.RawExpander()
    );

    private Expander() {
        // cannot be instantiated
    }

	/**
	 * Expands all braces in text. The number of open braces must be balanced with close braces
	 * @param text the text to expand
     * @param things Reference-able things
	 * @return the expanded text
	 */
	public static Message expand(String text, Map<String, Formattable> things) {
        Message expanded = Message.empty();
	    StringBuilder building = new StringBuilder();
        boolean ignoreNextSpecial = false;
	    for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);

            if (c == '{' && !ignoreNextSpecial) {
                // Add the basic text we have
                expanded = expanded.append(Message.of(building.toString()));
                building.setLength(0);

                // Expand and add the brace
                String subtext = toNext(text, '}', i)
						.orElseThrow(() -> new IllegalArgumentException("Braces unmatched: '" + text +"'"));
                Message expandedBrace = expandBrace(subtext, things);
                expanded = expanded.append(expandedBrace);
                i += subtext.length() - 1; // -1 because starting brace was already counted
            } else if (c == '\\' && !ignoreNextSpecial) {
                ignoreNextSpecial = true;
            } else {
                building.append(c);
                ignoreNextSpecial = false;
            }
        }

        // Add anything remaining
        expanded = expanded.append(Message.of(building.toString()));
        return expanded;
    }

	/**
	 * Expands a brace in a message.
	 * Each sub brace will be expanded with the same arguments as this message
	 * @param text the text to expands formatted as "{ key | arg1 | ... | argN }"
	 * @return the expanded string
	 */
	private static Message expandBrace(String text, Map<String, Formattable> things) {
        if (text.charAt(0) != '{' || text.charAt(text.length() - 1) != '}')
            throw new RuntimeException(
                    "expandBrace() must start and end with a brace");

        // Find the brace key
        String keyString = toNext(text, '|', 1)
                .orElseThrow(() -> new RuntimeException("Text bad: '" + text + "'"));

		// Get the arguments for the key (separated by '|')
        AtomicInteger i = new AtomicInteger(keyString.length());
        List<Message> args = new ArrayList<>();
        Stream.generate(() -> toNext(text, '|', i.get()))
				.takeWhile(Optional::isPresent)
				.map(Optional::orElseThrow)
				.forEach(subArg -> {
					args.add(expand(subArg.substring(1, subArg.length() - 1), things));
					i.addAndGet(subArg.length() - 1); // -1 because the first '|' would get counted twice
				});

        // remove the '|' and any whitespace from key
        keyString = keyString.substring(0, keyString.length()-1).trim().toLowerCase();

        // Try and look up a key, function or chat color to format with
        Formattable thing = things.get(keyString);
        if (thing == null)
            thing = functions.get(keyString);
        if (thing == null)
            try {
                thing = new Formattable.ColorWrapper(ChatColor.valueOf(keyString.toUpperCase()));
            } catch (IllegalArgumentException e) {
                thing = Message.of(text).color(ChatColor.RED);
            }

        return thing.format(args);
    }

    /**
     * Gets the substring of sequence from index to the next endingChar but takes into account brace skipping.
	 * The returned string will include both the start and end characters. If a closing brace
	 * is found before the wanted character, then the remaining to that brace is returned. If the end of sequece is
     * reached, then empty is returned
     */
    private static Optional<String> toNext(String sequence, char endingChar, int startIndex) {
        int size = sequence.length();
        int openBracesFound = 0;
        boolean ignoreNextSpecial = false;
        for (int i = startIndex + 1; i < size; i++) {

            if (ignoreNextSpecial) {
                ignoreNextSpecial = false;
            } else {
                char c = sequence.charAt(i);
                if (c == endingChar && openBracesFound == 0)
                    return Optional.of(sequence.substring(startIndex, i + 1));
                if (c == '{')
                    openBracesFound++;
                if (c == '}') {
                    openBracesFound--;
                    if (openBracesFound < 0)
                        return Optional.of(sequence.substring(startIndex, i+1));
                }
                if (c == '\\')
                    ignoreNextSpecial = true;
            }
        }
        return Optional.empty();
    }
}