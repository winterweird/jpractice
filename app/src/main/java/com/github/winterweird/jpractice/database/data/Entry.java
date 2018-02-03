package com.github.winterweird.jpractice.database.data;

import android.content.ContentValues;

import com.github.winterweird.jpractice.database.FeedReaderContract;

public class Entry implements DatabaseEntryInterface {
    private int listname;
    private String kanji;
    private String reading;
    private int tier;
    private int position;
    public Entry(int listname, String kanji, String reading, int position) {
        this.listname = listname;
        this.kanji = kanji;
        this.reading = reading;
        this.position = position;
        this.tier = 5; // default tier
    }
    
    public Entry(int listname, String kanji, String reading, int position, int tier) {
        this.listname = listname;
        this.kanji = kanji;
        this.reading = reading;
        this.position = position;
        this.tier = tier;
    }

    public int getListname() {
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

    @Override
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME, listname);
        cv.put(FeedReaderContract.FeedEntries.COLUMN_NAME_KANJI,    kanji);
        cv.put(FeedReaderContract.FeedEntries.COLUMN_NAME_READING,  reading);
        cv.put(FeedReaderContract.FeedEntries.COLUMN_NAME_TIER,     tier);
        cv.put(FeedReaderContract.FeedEntries.COLUMN_NAME_POSITION, position);
        return cv;
    }

    public Entry setPosition(int newPosition) {
        return new Entry(listname, kanji, reading, newPosition, tier);
    }
}
