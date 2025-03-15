package com.dicegame;

public class DiceSet {
    private Dice[] diceSets;

    public DiceSet(int[][] diceFaces) {
        diceSets = new Dice[diceFaces.length];
        for (int i = 0; i < diceFaces.length; i++) {
            diceSets[i] = new Dice(diceFaces[i]);
        }
    }

    public Dice getDice(int index) {
        return diceSets[index];
    }

    public int getNumberOfDiceSets() {
        return diceSets.length;
    }
}