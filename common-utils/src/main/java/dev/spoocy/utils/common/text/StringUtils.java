package dev.spoocy.utils.common.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class StringUtils {

	public static boolean isNullOrEmpty(@Nullable String s) {
		return s == null || s.isEmpty();
	}

    public static String[] splitToLines(@NotNull String s) {
		return s.split("\n");
	}

    public static String repeat(@NotNull String s, int amount) {
        return s.repeat(Math.max(0, amount));
	}

    public static boolean isNumber(@NotNull String s) {
		try {
			Double.parseDouble(s);
			return true;
		} catch (Exception ex) {
			return false;
		}
	}

    public static String getAfterFirst(@NotNull String s, @NotNull String separator) {
		return Optional.of(s)
				.filter(name -> name.contains(separator))
				.map(name -> name.substring(name.indexOf(separator) + separator.length()))
				.orElse("");
	}

    public static String getAfterLast(@NotNull String s, @NotNull String separator) {
		return Optional.of(s)
				.filter(name -> name.contains(separator))
				.map(name -> name.substring(name.lastIndexOf(separator) + separator.length()))
				.orElse("");
	}

	public static String join(@NotNull List<String> strings, @NotNull String separator) {
		return join(strings.toArray(new String[0]), separator);
	}

	public static String join(@NotNull String[] strings, @NotNull String separator) {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < strings.length; i++) {
			builder.append(strings[i]);
			if (i < strings.length - 1) {
				builder.append(separator);
			}
		}
		return builder.toString();
	}

}
