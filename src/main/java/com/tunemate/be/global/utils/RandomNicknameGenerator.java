package com.tunemate.be.global.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomNicknameGenerator {
    private static final Random RANDOM = new Random();
    private static final List<String> ADJECTIVES = new ArrayList<>();
    private static final List<String> NOUNS = new ArrayList<>();

    static {
        loadWords("nickname/" + "adjectives.txt", ADJECTIVES);
        loadWords("nickname/" + "nouns.txt", NOUNS);
    }

    private static void loadWords(String fileName, List<String> targetList) {
        try (InputStream is = RandomNicknameGenerator.class.getResourceAsStream("/" + fileName);
             InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(isr)) {

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    targetList.add(line);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static String generateNickname() {
        if (ADJECTIVES.isEmpty() || NOUNS.isEmpty()) {
            throw new IllegalStateException("형용사 혹은 명사 목록이 비어있습니다. 파일 로딩을 확인하세요.");
        }
        String adjective = ADJECTIVES.get(RANDOM.nextInt(ADJECTIVES.size()));
        String noun = NOUNS.get(RANDOM.nextInt(NOUNS.size()));
        return adjective + " " + noun;
    }
}
