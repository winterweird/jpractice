package com.github.winterweird.jpractice.database;

import android.provider.BaseColumns;

public abstract class AbstractFeedReaderContract implements BaseColumns {
    public abstract String getTableName();
    public abstract String getCreate();
    public abstract String getDelete();
}
