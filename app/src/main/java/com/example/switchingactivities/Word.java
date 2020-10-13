package com.example.switchingactivities;

public class Word {
    private String name = "";
    private int category = 0; //1-counts (>10 freq), 2-extra (1<freq<10), 3-bad
    private double frequency = 0;

    public Word(String nm, Double d) {//name and frequency
        name = nm;
        frequency = d;
        if (d>=10) {
            category = 1;
        } else if (d>=1 && d<10) {
            category = 2;
        } else {
            category = 3;
        }
    }

    public double getFrequency() {
        return frequency;
    }

    public int getCategory() {
        return category;
    }

    public String getName() {
        return name;
    }
}

