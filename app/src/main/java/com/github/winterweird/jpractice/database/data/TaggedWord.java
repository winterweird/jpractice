package com.github.winterweird.jpractice.database.data;

import android.content.ContentValues;

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
}
