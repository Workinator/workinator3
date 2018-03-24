package com.allardworks.workinator3.core;

import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class NameUtilityTests {
    @Test
    public void getName() {
        val name = NameUtility.getRandomName();
        assertNotNull(name);
        System.out.println(name);
    }
}
