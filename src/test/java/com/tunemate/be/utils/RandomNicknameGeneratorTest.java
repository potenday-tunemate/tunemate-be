package com.tunemate.be.utils;


import com.tunemate.be.global.utils.RandomNicknameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RandomNicknameGeneratorTest {

    @Test
    public void testGenerateNickname() {
        String nickname = RandomNicknameGenerator.generateNickname();

        assertNotNull(nickname, "Nickname should not be null");

        assertFalse(nickname.trim().isEmpty(), "Nickname should not be empty");

        String[] parts = nickname.split(" ");

        assertEquals(2, parts.length, "Nickname should consist of two parts (adjective and noun)");

        assertFalse(parts[0].trim().isEmpty(), "Adjective part should not be empty");

        assertFalse(parts[1].trim().isEmpty(), "Noun part should not be empty");
    }
}
