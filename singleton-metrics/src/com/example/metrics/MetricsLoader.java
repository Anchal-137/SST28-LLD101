package com.example.metrics;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads default metric keys from a properties file into the singleton registry.
 *
 * Fixed: uses MetricsRegistry.getInstance() instead of "new MetricsRegistry()".
 */
public class MetricsLoader {

    public MetricsRegistry loadFromFile(String path) throws IOException {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(path)) {
            props.load(fis);
        }

        // use the singleton — not a fresh instance
        MetricsRegistry registry = MetricsRegistry.getInstance();

        for (String key : props.stringPropertyNames()) {
            String raw = props.getProperty(key, "0").trim();
            long v;
            try {
                v = Long.parseLong(raw);
            } catch (NumberFormatException e) {
                v = 0L;
            }
            registry.setCount(key, v);
        }
        return registry;
    }
}
