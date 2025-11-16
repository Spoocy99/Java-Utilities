package dev.spoocy.utils.common.text;

import org.jetbrains.annotations.NotNull;

/**
 * @author Spoocy99 | GitHub: Spoocy99
 */

public class FormatUtils {

    public static String formatDuration(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        StringBuilder stringBuilder = new StringBuilder();
        if (hours > 0) stringBuilder.append(String.format("%dh", hours)).append(' ');
        if (minutes >= 0) stringBuilder.append(String.format("%dm", minutes)).append(' ');
        if (seconds >= 0) stringBuilder.append(String.format("%ds", seconds));
        return stringBuilder.toString();
    }

    public static String twoDigitFormat(int number) {
        return String.format("%02d", number);
    }

    public static <N extends Number> String formatNumber(@NotNull N number) {
        if (number instanceof Integer || number instanceof Short || number instanceof Byte) {
            return String.format("%,d", number.intValue());

        } else if (number instanceof Long) {
            return String.format("%,d", number.longValue());

        } else if (number instanceof Float || number instanceof Double) {
            return String.format("%,.2f", number.doubleValue());
        }

        return String.valueOf(number);
    }



}
