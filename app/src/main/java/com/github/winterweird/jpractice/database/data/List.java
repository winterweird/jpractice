package com.github.winterweird.jpractice.database.data;

import android.content.ContentValues;

import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.database.DatabaseHelper;

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

    @Override
    public String toString() {
        return listname;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (o == null) return false;
        if (!(o instanceof List)) return false;
        List ol = (List)o;
        return ol.listname.equals(this.listname);
    }

    @Override
    public int hashCode() {
        return listname.hashCode();
    }

    @Override
    public int id() {
        return DatabaseHelper.getHelper().idOf(this);
    }
}
