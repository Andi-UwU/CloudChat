package application.utils;

import java.time.format.DateTimeFormatter;

/**
 * All constants used
 */
public class Constants {
    /**
     * Used to format LocalDateTime entities
     */
    public static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

}
