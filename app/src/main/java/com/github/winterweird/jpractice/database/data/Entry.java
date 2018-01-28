package com.github.winterweird.jpractice.database.data;

public class Entry {
    private String listname;
    private String kanji;
    private String reading;
    private int tier;
    private int position;
    public Entry(String listname, String kanji, String reading, int position) {
        this.listname = listname;
        this.kanji = kanji;
        this.reading = reading;
        this.position = position;
        this.tier = 5; // default tier
    }
    
    public Entry(String listname, String kanji, String reading, int position, int tier) {
        this.listname = listname;
        this.kanji = kanji;
        this.reading = reading;
        this.position = position;
        this.tier = tier;
    }

    public String getListname() {
        return listname;
    }

    public String getKanji() {
        return kanji;
    }

    public String getReading() {
        return reading;
    }

    public int getPosition() {
        return position;
    }
    
    public int getTier() {
        return tier;
    }
}
