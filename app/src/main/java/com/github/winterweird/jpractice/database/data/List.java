package com.github.winterweird.jpractice.database.data;

import android.content.ContentValues;

import com.github.winterweird.jpractice.database.FeedReaderContract;

public class List implements DatabaseEntryInterface {
    private String listname;
    public List(String listname) {
        this.listname = listname;
    }
    
    public String getListname() {
        return listname;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME, listname);
        return cv;
    }
}