package me.Azz_9.unbounded_bundle;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static me.Azz_9.unbounded_bundle.Constants.MOD_NAME;

public class BundleLogger {
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);
    private static final String PREFIX = "[" + MOD_NAME + "] ";

    private BundleLogger() {
    }

    public static void info(String message, Object... args) {
        LOGGER.info(PREFIX + message, args);
    }

    public static void warn(String message, Object... args) {
        LOGGER.warn(PREFIX + message, args);
    }

    public static void error(String message, Object... args) {
        LOGGER.error(PREFIX + message, args);
    }

    public static void debug(String message, Object... args) {
        LOGGER.debug(PREFIX + message, args);
    }
}
