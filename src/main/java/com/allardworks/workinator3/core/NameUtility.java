package com.allardworks.workinator3.core;

import lombok.val;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

public final class NameUtility {
    private NameUtility() {
    }

    /**
     * Gets a random name.
     * @return
     */
    public static String getRandomName() {
        val nameCount = 4725;
        val index = new Random().nextInt(nameCount);

        try {
            val resource = new ClassPathResource("names");
            val reader = new BufferedReader(new InputStreamReader(resource.getInputStream()));
            for (int i = 0; i < index -1; i++) {
                reader.readLine();
            }

            return reader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
