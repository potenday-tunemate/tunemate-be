package com.tunemate.be.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class StreamGobbler extends Thread {
    private final BufferedReader reader;
    private final String streamType;

    public StreamGobbler(java.io.InputStream inputStream, String streamType) {
        this.reader = new BufferedReader(new InputStreamReader(inputStream));
        this.streamType = streamType;
    }

    @Override
    public void run() {
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if ("ERROR".equals(streamType)) {
                    System.err.println("[" + streamType + "] " + line);
                } else {
                    System.out.println("[" + streamType + "] " + line);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}