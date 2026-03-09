package com.example.map;

import java.util.HashMap;
import java.util.Map;

// Flyweight factory - caches and reuses MarkerStyle instances
public class MarkerStyleFactory {

    private final Map<String, MarkerStyle> cache = new HashMap<>();

    public MarkerStyle get(String shape, String color, int size, boolean filled) {
        String key = shape + "|" + color + "|" + size + "|" + (filled ? "F" : "O");

        MarkerStyle existing = cache.get(key);
        if (existing != null) return existing;

        MarkerStyle style = new MarkerStyle(shape, color, size, filled);
        cache.put(key, style);
        return style;
    }

    public int cacheSize() {
        return cache.size();
    }
}