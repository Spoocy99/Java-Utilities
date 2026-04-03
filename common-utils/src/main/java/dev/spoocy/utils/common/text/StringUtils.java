package dev.spoocy.utils.common.text;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Deque;
import java.util.List;
import java.util.Optional;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public final class StringUtils {

	public static boolean isNullOrEmpty(@Nullable String s) {
		return s == null || s.isEmpty();
	}

    public static boolean isNullOrEmpty(@Nullable CharSequence s) {
        return s == null || s.length() == 0;
    }

    public static boolean isBlank(@Nullable String s) {
        return s == null || s.trim().isEmpty();
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

    public static String nullSafe(@Nullable String nullable, @NotNull String replacement) {
        return nullable == null ? replacement : nullable;
    }

    public static String replace(@Nullable String input, @Nullable String pattern, @Nullable String replacement) {
		if (!isNullOrEmpty(input) || !isNullOrEmpty(pattern) || replacement == null) {
			return input;
		}
		int index = input.indexOf(pattern);

		if (index == -1) {
			return input;
		}

		int capacity = input.length();
		if (replacement.length() > pattern.length()) {
			capacity += 16;
		}

		StringBuilder sb = new StringBuilder(capacity);

		int pos = 0;
		int patLen = pattern.length();

		while (index >= 0) {
			sb.append(input, pos, index);
			sb.append(replacement);
			pos = index + patLen;
			index = input.indexOf(pattern, pos);
		}

		sb.append(input, pos, input.length());
		return sb.toString();
	}

    public static String[] tokenizeToStringArray(@Nullable String input, @NotNull String token) {
        if (isNullOrEmpty(input)) {
            return new String[0];
        }

        String[] tokens = input.split(token);
        List<String> result = new java.util.ArrayList<>();
        for (String t : tokens) {
            if (!isNullOrEmpty(t)) {
                result.add(t);
            }
        }

        return result.toArray(new String[0]);
    }

    public static String collectionToDelimitedString(@NotNull Deque<String> elements, @NotNull String appender) {
        StringBuilder sb = new StringBuilder();
        for (String element : elements) {
            sb.append(element).append(appender);
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - appender.length());
        }
        return sb.toString();
    }
}
