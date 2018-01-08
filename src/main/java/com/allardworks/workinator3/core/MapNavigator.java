package com.allardworks.workinator3.core;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class MapNavigator {
    private final Map<String, Object> map;

    public MapNavigator getMap(final String name) {
        return new MapNavigator((Map<String, Object>)get(name));
    }

    public String getString(final String name) {
        return (String)get(name);
    }

    public Object get(final String name) {
        return map.get(name);
    }
}
