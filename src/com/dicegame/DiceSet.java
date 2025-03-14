package com.dicegame;

public class DiceSet {
    private Dice[] diceSets;

    public DiceSet() {
        diceSets = new Dice[]{
                new Dice(new int[]{2, 2, 4, 4, 9, 9}),
                new Dice(new int[]{6, 8, 1, 1, 8, 6}),
                new Dice(new int[]{7, 5, 3, 7, 5, 3})
        };
    }

    public Dice getDice(int index) {
        return diceSets[index];
    }

    public int getNumberOfDiceSets() {
        return diceSets.length;
    }
}