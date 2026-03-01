package com.example.metrics;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Thread-safe, lazy-initialised Singleton using the static-holder idiom.
 *
 * Defences:
 *  - Private constructor (blocks direct instantiation)
 *  - Reflection guard (throws if a second instance is attempted)
 *  - readResolve() (preserves singleton across serialization)
 */
public class MetricsRegistry implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    // flag to detect reflection-based second construction
    private static boolean alreadyCreated = false;

    private final Map<String, Long> counters = new HashMap<>();

    // Private constructor — also blocks reflection attacks
    private MetricsRegistry() {
        if (alreadyCreated) {
            throw new IllegalStateException("Cannot create a second MetricsRegistry instance via reflection");
        }
        alreadyCreated = true;
    }

    // ---- Static-holder idiom for lazy, thread-safe initialisation ----

    private static class Holder {
        private static final MetricsRegistry INSTANCE = new MetricsRegistry();
    }

    public static MetricsRegistry getInstance() {
        return Holder.INSTANCE;
    }

    // ---- Serialization guard ----

    @Serial
    private Object readResolve() {
        return getInstance();
    }

    // ---- Public API (unchanged) ----

    public synchronized void setCount(String key, long value) {
        counters.put(key, value);
    }

    public synchronized void increment(String key) {
        counters.put(key, getCount(key) + 1);
    }

    public synchronized long getCount(String key) {
        return counters.getOrDefault(key, 0L);
    }

    public synchronized Map<String, Long> getAll() {
        return Collections.unmodifiableMap(new HashMap<>(counters));
    }
}
