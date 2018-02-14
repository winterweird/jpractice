package com.github.winterweird.jpractice.database.data;

import android.content.ContentValues;

import com.github.winterweird.jpractice.database.FeedReaderContract;

public class Tag implements DatabaseEntryInterface {
    private String tag;
    public Tag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FeedReaderContract.FeedTags.COLUMN_NAME_TAG, tag);
        return cv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag)) return false;
        Tag other = (Tag)o;
        return this.tag.equals(other.tag);
    }

    @Override
    public int hashCode() {
        return this.tag.hashCode();
    }
}
