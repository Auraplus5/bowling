package com.kevin.bowling.entity;

import java.util.ArrayList;
import java.util.List;

public class BowlingFrame {
    private Integer number;
    private List<Integer> rolls = new ArrayList<>();

    public BowlingFrame(Integer number) {
        this.number = number;
    }

    public void addRoll(int pins) {
        rolls.add(pins);
    }

    public boolean isStrike() {
        return !rolls.isEmpty() && rolls.getFirst() == 10;
    }

    public boolean isSpare() {
        return rolls.size() >= 2 && rolls.getFirst() + rolls.get(1) == 10;
    }

    public boolean isTenth() {
        return number == 10;
    }

    public boolean isComplete() {
        if (isTenth()) {
            if (rolls.size() < 2) {
                return false;
            }
            return rolls.size() == 3 || (!isStrike() && !isSpare());
        }
        return isStrike() || rolls.size() == 2;
    }

    public int getNumber() {
        return number;
    }

    public int getRoll(int i) {
        return rolls.get(i);
    }

    public int rollCount() {
        return rolls.size();
    }
}
