package com.github.winterweird.jpractice.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;

import java.util.ArrayList;
import java.util.Arrays;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 5;
    public static final String DATABASE_NAME = "kanji.db";
    private static DatabaseHelper helper;
    private static final String[] EMPTY_STRING_ARRAY = new String[]{};

    public static synchronized DatabaseHelper getHelper(Context context) {
        if (helper == null)
            helper = new DatabaseHelper(context);
        return helper;
    }
    
    private DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FeedReaderContract.FeedLists.CREATE);
        db.execSQL(FeedReaderContract.FeedEntries.CREATE);
        db.execSQL(FeedReaderContract.FeedPresets.CREATE);
        db.execSQL(FeedReaderContract.FeedTags.CREATE);
        db.execSQL(FeedReaderContract.FeedTaggedWords.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: make this better
        db.execSQL(FeedReaderContract.FeedLists.DELETE);
        db.execSQL(FeedReaderContract.FeedEntries.DELETE);
        db.execSQL(FeedReaderContract.FeedPresets.DELETE);
        db.execSQL(FeedReaderContract.FeedTags.DELETE);
        db.execSQL(FeedReaderContract.FeedTaggedWords.DELETE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: make this better
        onUpgrade(db, oldVersion, newVersion);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.setForeignKeyConstraintsEnabled(true);
        }
    }

    public Cursor getLists() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedLists.TABLE_NAME,
                EMPTY_STRING_ARRAY);
    }

    public Cursor getEntries() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedEntries.TABLE_NAME,
                EMPTY_STRING_ARRAY);
    }

    public Cursor getEntries(String listname) {
        return getEntries(new String[]{listname});
    }

    public Cursor getEntries(String[] listnames) {
        if (listnames == null || listnames.length == 0)
            throw new IllegalArgumentException("Argument must be a non-empty list of strings");
        SQLiteDatabase db = getReadableDatabase();
        String cnm = FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME;
        StringBuilder whereClause = new StringBuilder(" WHERE L." + cnm + " = ?");
        for (int i = 1; i < listnames.length; i++) {
            whereClause.append(" OR L." + cnm + " = ?");
        }

        return db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedEntries.TABLE_NAME +
                " AS E INNER JOIN " + FeedReaderContract.FeedLists.TABLE_NAME + " AS L ON " +
                "L." + FeedReaderContract.FeedLists._ID + " = " + "E." +
                FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME +
                whereClause.toString(), listnames);
    }

    public Cursor getPresets() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedPresets.TABLE_NAME,
                EMPTY_STRING_ARRAY);
    }

    public Cursor getTags() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedTags.TABLE_NAME,
                EMPTY_STRING_ARRAY);
    }

    public Cursor getTaggedWords() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedTaggedWords.TABLE_NAME,
                EMPTY_STRING_ARRAY);
    }

    public Cursor getTaggedWords(String listname) {
        return getTaggedWords(new String[]{listname});
    }

    public Cursor getTaggedWords(String[] listnames) {
        return getTaggedWords(listnames, EMPTY_STRING_ARRAY, EMPTY_STRING_ARRAY);
    }

    public Cursor getTaggedWords(String[] listnames, String[] whiteFilters, String[] blackFilters) {
        // THIS IS NOT WORKING RIGHT TODO
        SQLiteDatabase db = getReadableDatabase();
        
        StringBuilder whereClauseWhite = new StringBuilder();
        StringBuilder whereClauseBlack = new StringBuilder();
        StringBuilder whereClauseList = new StringBuilder();
        
        String cnm = FeedReaderContract.FeedTaggedWords.COLUMN_NAME_KANJI_ID;

        String baseQuery = "SELECT * FROM " + FeedReaderContract.FeedTaggedWords.TABLE_NAME +
            " NATURAL JOIN " + FeedReaderContract.FeedEntries.TABLE_NAME;

        StringBuilder whereClause = new StringBuilder(" WHERE (").append(join(listnames, " OR ", " = ?")).append(")");
        
        String whereWhite = join(whiteFilters, " OR ", " = ?");
        if (!whereWhite.isEmpty()) whereClause.append(" AND (").append(whereWhite).append(")");
        
        String whereBlack = join(blackFilters, " AND ", " = ?");
        if (!whereBlack.isEmpty()) whereClause.append(" AND (").append(whereBlack).append(")");
        
        ArrayList<String> both = new ArrayList<String>(Arrays.asList(listnames));
        if (whiteFilters != null)
            both.addAll(Arrays.asList(whiteFilters));
        if (blackFilters != null)
            both.addAll(Arrays.asList(blackFilters));

        String[] whereArgs = new String[both.size()];
        both.toArray(whereArgs);
        
        return db.rawQuery(baseQuery + whereClause, whereArgs);
    }

    // TODO FIX THIS THIS IS WRONG
    /**
     * Helper method to join arrays with prefixes and postfixes.
     *
     * @param prefix added before every word except the first, or between every word
     * @param postfix added after every word
     */
    private String join(String[] words, String prefix, String postfix) {
        if (words == null)
            return "";
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < words.length; i++) {
            if (i != 0)
                sb.append(prefix);
            sb.append(words[i]);
            sb.append(postfix);
        }
        return sb.toString();
    }

    public int entryCount(String listname) {
        Cursor cursor = getEntries(listname);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    public int idOf(String table, String column) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        int id = -1;
        switch (table) {
            case FeedReaderContract.FeedLists.TABLE_NAME:
                cursor = db.rawQuery("SELECT " + FeedReaderContract.FeedLists._ID +" FROM " + table +
                        " WHERE " + FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME + " = ?",
                        new String[]{column});
                cursor.moveToFirst();
                id = cursor.getInt(cursor.getColumnIndexOrThrow( FeedReaderContract.FeedLists._ID));
                cursor.close();
                break;
            case FeedReaderContract.FeedEntries.TABLE_NAME:
                throw new IllegalArgumentException("No single column uniquely identifies " + table);
            case FeedReaderContract.FeedPresets.TABLE_NAME:
                throw new IllegalArgumentException("No single column uniquely identifies " + table);
            case FeedReaderContract.FeedTags.TABLE_NAME:
                cursor = db.rawQuery("SELECT " + FeedReaderContract.FeedTags._ID + " FROM " + table +
                        " WHERE " + FeedReaderContract.FeedTags.COLUMN_NAME_TAG + " = ?",
                        new String[]{column});
                cursor.moveToFirst();
                id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedTags._ID));
                cursor.close();
                break;
            case FeedReaderContract.FeedTaggedWords.TABLE_NAME:
                throw new IllegalArgumentException("No single column uniquely identifies " + table);
            default:
                throw new IllegalArgumentException("Unknown table name: " + table);
        }
        return id;
    }
}
