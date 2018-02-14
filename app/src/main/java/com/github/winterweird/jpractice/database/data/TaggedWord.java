package com.github.winterweird.jpractice.database.data;

import android.content.ContentValues;

import java.util.Objects;

import com.github.winterweird.jpractice.database.FeedReaderContract;

public class TaggedWord implements DatabaseEntryInterface {
    private int tag;
    private int kanjiID;
    public TaggedWord(int tag, int kanjiID) {
        this.tag = tag;
        this.kanjiID = kanjiID;
    }

    public int getTag() {
        return tag;
    }
    
    public int getKanjiId() {
        return kanjiID;
    }

    @Override
    public ContentValues getContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(FeedReaderContract.FeedTaggedWords.COLUMN_NAME_TAG, tag);
        cv.put(FeedReaderContract.FeedTaggedWords.COLUMN_NAME_KANJI_ID, kanjiID);
        return cv;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TaggedWord)) return false;
        TaggedWord other = (TaggedWord)o;
        return this.tag == other.tag && this.kanjiID == other.kanjiID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(new Integer(this.tag), new Integer(this.kanjiID));
    }
}
