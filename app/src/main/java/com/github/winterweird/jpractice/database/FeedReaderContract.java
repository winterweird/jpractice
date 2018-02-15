package com.github.winterweird.jpractice.database;

public final class FeedReaderContract {
    private FeedReaderContract() {}
    public static class FeedLists extends AbstractFeedReaderContract {
        public static final String COLUMN_NAME_LISTNAME = "listname";
        public static final String TABLE_NAME = "Lists";
        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + "(" +
                _ID + " INTEGER PRIMARY KEY," +
                COLUMN_NAME_LISTNAME + " TEXT UNIQUE)";
        public static final String DELETE = "DROP TABLE " + TABLE_NAME;

        @Override
        public String getTableName() {
            return TABLE_NAME;
        }
        @Override
        public String getCreate() {
            return CREATE;
        }
        @Override
        public String getDelete() {
            return DELETE;
        }
    }
    public static class FeedEntries extends AbstractFeedReaderContract {
        public static final String TABLE_NAME           = "Entries";
        public static final String COLUMN_NAME_LISTNAME = "listname";
        public static final String COLUMN_NAME_KANJI    = "kanji";
        public static final String COLUMN_NAME_READING  = "reading";
        public static final String COLUMN_NAME_POSITION = "position";
        public static final String COLUMN_NAME_TIER     = "tier";

        public static final String CREATE =
            "CREATE TABLE "      + TABLE_NAME + "("        +
            _ID                  + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_LISTNAME + " INTEGER NOT NULL,"    +
            COLUMN_NAME_KANJI    + " TEXT NOT NULL,"       +
            COLUMN_NAME_READING  + " TEXT NOT NULL,"       +
            COLUMN_NAME_POSITION + " INTEGER NOT NULL,"    +
            COLUMN_NAME_TIER     + " INTEGER NOT NULL,"    +
            "FOREIGN KEY(" + COLUMN_NAME_LISTNAME + ") "   +
             "REFERENCES " + FeedLists.TABLE_NAME + "("    + 
                             FeedLists._ID        + ") "   +
                             "ON UPDATE CASCADE " +
                             "ON DELETE CASCADE," +
            "CONSTRAINT wordUniqueInList UNIQUE(" +
                             COLUMN_NAME_LISTNAME + ", "   +
                             COLUMN_NAME_KANJI    + "),"   +
            "CONSTRAINT posUniqueInList UNIQUE("  +
                             COLUMN_NAME_LISTNAME + ", "   +
                             COLUMN_NAME_POSITION + "))";
        
        public static final String DELETE = "DROP TABLE " + TABLE_NAME;
        
        @Override
        public String getTableName() {
            return TABLE_NAME;
        }
        @Override
        public String getCreate() {
            return CREATE;
        }
        @Override
        public String getDelete() {
            return DELETE;
        }
    }

    public static class FeedPresets extends AbstractFeedReaderContract {
        public static final String TABLE_NAME                        = "Presets";
        public static final String COLUMN_NAME_LISTNAME              = "listname";
        public static final String COLUMN_NAME_ALGORITHM             = "algorithm";
        public static final String RANDOM_WORD_ALGORITHM             = "random";
        public static final String WEIGHTED_RANDOM_ALGORITHM         = "weighted random";
        public static final String EVERY_WORD_ONCE_ALGORITHM         = "every word once";
        public static final String EVERY_WORD_CORRECT_ONCE_ALGORITHM = "every word correct once";

        public static final String CREATE =
            "CREATE TABLE " + TABLE_NAME  + "(" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_LISTNAME  + " INTEGER NOT NULL,"     +
            COLUMN_NAME_ALGORITHM + " TEXT NOT NULL,"        +
            "FOREIGN KEY(" + COLUMN_NAME_LISTNAME   + ") "   +
             "REFERENCES " + FeedLists.TABLE_NAME   + "("    +
                             FeedLists._ID          + ") "   +
                             "ON UPDATE CASCADE "   +
                             "ON DELETE CASCADE,"   +
            "CONSTRAINT checkValidAlg CHECK(algorithm IN ('" +
                RANDOM_WORD_ALGORITHM     + "','"   +
                WEIGHTED_RANDOM_ALGORITHM + "','"   +
                EVERY_WORD_ONCE_ALGORITHM + "','"   +
                EVERY_WORD_CORRECT_ONCE_ALGORITHM   + "')))";
        
        public static final String DELETE = "DROP TABLE " + TABLE_NAME;
        
        @Override
        public String getTableName() {
            return TABLE_NAME;
        }
        @Override
        public String getCreate() {
            return CREATE;
        }
        @Override
        public String getDelete() {
            return DELETE;
        }
    }

    public static class FeedTags extends AbstractFeedReaderContract {
        public static final String TABLE_NAME      = "Tags";
        public static final String COLUMN_NAME_TAG = "tag";

        public static final String CREATE =
            "CREATE TABLE " + TABLE_NAME + "(" +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_TAG + " TEXT UNIQUE)";
        
        public static final String DELETE = "DROP TABLE " + TABLE_NAME;
        
        @Override
        public String getTableName() {
            return TABLE_NAME;
        }
        @Override
        public String getCreate() {
            return CREATE;
        }
        @Override
        public String getDelete() {
            return DELETE;
        }
    }

    public static class FeedTaggedWords extends AbstractFeedReaderContract {
        public static final String TABLE_NAME           = "TaggedWords";
        public static final String COLUMN_NAME_TAG      = "tag";
        public static final String COLUMN_NAME_KANJI_ID = "kanjiId";

        public static final String CREATE = 
            "CREATE TABLE "      + TABLE_NAME + "("  +
            _ID + " INTEGER PRIMARY KEY," +
            COLUMN_NAME_TAG      + " INTEGER NOT NULL," +
            COLUMN_NAME_KANJI_ID + " INTEGER NOT NULL," +
            "FOREIGN KEY(" + COLUMN_NAME_TAG            + ") REFERENCES " +
                          FeedTags.TABLE_NAME           + "("             +
                          FeedTags._ID                  + ") "            +
                          "ON UPDATE CASCADE "          +
                          "ON DELETE CASCADE,"          +
            "FOREIGN KEY(" + COLUMN_NAME_KANJI_ID       + ") REFERENCES " +
                          FeedEntries.TABLE_NAME        + "("             +
                          FeedEntries._ID               + ") "            +
                          "ON UPDATE CASCADE "          +
                          "ON DELETE CASCADE,"          +
            "CONSTRAINT tagKanjiCombinationUnique UNIQUE( "               +
                          COLUMN_NAME_TAG               + ", "            +
                          COLUMN_NAME_KANJI_ID          +"))";
        
        public static final String DELETE = "DROP TABLE " + TABLE_NAME;
        
        @Override
        public String getTableName() {
            return TABLE_NAME;
        }
        @Override
        public String getCreate() {
            return CREATE;
        }
        @Override
        public String getDelete() {
            return DELETE;
        }
    }
}
