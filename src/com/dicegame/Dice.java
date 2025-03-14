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
        return "[" + faces[0] + "," + faces[1] + "," + faces[2] + "," + faces[3] + "," + faces[4] + "," + faces[5] + "]";
    }
}