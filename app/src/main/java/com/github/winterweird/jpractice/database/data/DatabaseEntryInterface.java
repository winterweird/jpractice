package com.github.winterweird.jpractice.database.data;

import android.content.ContentValues;

public interface DatabaseEntryInterface {
    ContentValues getContentValues();
    int id();
}
