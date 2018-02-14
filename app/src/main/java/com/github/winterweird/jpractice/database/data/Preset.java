package com.github.winterweird.jpractice.database.data;

import android.content.ContentValues;

import com.github.winterweird.jpractice.database.FeedReaderContract;

public class Preset implements DatabaseEntryInterface {
    private int id;
    private int listname;
    private String algorithm;

    public Preset(int id, int listname, String algorithm) {
        this.id = id;
        this.listname = listname;
        this.algorithm = algorithm;
    }

    public int getId() {
        return id;
    }

    public int getListname() {
        return listname;
    }

    public String algorithm() {
        return algorithm;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FeedReaderContract.FeedPresets._ID, id);
        cv.put(FeedReaderContract.FeedPresets.COLUMN_NAME_LISTNAME, listname);
        cv.put(FeedReaderContract.FeedPresets.COLUMN_NAME_ALGORITHM, algorithm);
        return cv;
    }
}
