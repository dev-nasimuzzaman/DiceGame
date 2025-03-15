package com.dicegame;

import java.security.SecureRandom;

public class Dice {
    private int[] faces;

    public Dice(int[] faces) {
        this.faces = faces;
    }

    public int roll(SecureRandom random) {
        return faces[random.nextInt(faces.length)];
    }

    public String format() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < faces.length; i++) {
            sb.append(faces[i]);
            if (i < faces.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}