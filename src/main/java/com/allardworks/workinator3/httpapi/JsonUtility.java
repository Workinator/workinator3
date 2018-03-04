package com.allardworks.workinator3.httpapi;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by jaya on 2/28/18.
 * k?
 */
public class JsonUtility {
    private final static ObjectMapper json = new ObjectMapper();
    private JsonUtility() {
    }

    public static ObjectMapper json() {
        return json;
    }
}
