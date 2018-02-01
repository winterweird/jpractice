package com.github.winterweird.jpractice.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Arrays;

import android.util.Log;

import com.github.winterweird.jpractice.database.data.*;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 7;
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

    public ArrayList<List> getLists() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedLists.TABLE_NAME,
                EMPTY_STRING_ARRAY);
        
        ArrayList<List> arr = new ArrayList<>();
        String cnmListname = FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME;
        
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String listname = cursor.getString(cursor.getColumnIndexOrThrow(cnmListname));
            arr.add(new List(listname));
        }
        cursor.close();
        return arr;
    }

    public ArrayList<Entry> getEntries() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedEntries.TABLE_NAME,
                EMPTY_STRING_ARRAY);

        ArrayList<Entry> arr = new ArrayList<>();
        String cnmListname = FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME;
        String cnmKanji = FeedReaderContract.FeedEntries.COLUMN_NAME_KANJI;
        String cnmReading = FeedReaderContract.FeedEntries.COLUMN_NAME_READING;
        String cnmTier = FeedReaderContract.FeedEntries.COLUMN_NAME_TIER;
        String cnmPosition = FeedReaderContract.FeedEntries.COLUMN_NAME_POSITION;
        
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int listname = cursor.getInt(cursor.getColumnIndexOrThrow(cnmListname));
            String kanji = cursor.getString(cursor.getColumnIndexOrThrow(cnmKanji));
            String reading = cursor.getString(cursor.getColumnIndexOrThrow(cnmReading));
            int tier = cursor.getInt(cursor.getColumnIndexOrThrow(cnmTier));
            int position = cursor.getInt(cursor.getColumnIndexOrThrow(cnmPosition));
            arr.add(new Entry(listname, kanji, reading, position, tier));
        }
        cursor.close();
        return arr;
    }

    public ArrayList<Entry> getEntries(String listname) {
        return getEntries(new String[]{listname});
    }

    public ArrayList<Entry> getEntries(String[] listnames) {
        if (listnames == null || listnames.length == 0)
            throw new IllegalArgumentException("Argument must be a non-empty list of strings");
        SQLiteDatabase db = getReadableDatabase();
        String cnm = FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME;
        StringBuilder whereClause = new StringBuilder(" WHERE L." + cnm + " = ?");
        for (int i = 1; i < listnames.length; i++) {
            whereClause.append(" OR L." + cnm + " = ?");
        }

        Cursor cursor = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedEntries.TABLE_NAME +
                " AS E INNER JOIN " + FeedReaderContract.FeedLists.TABLE_NAME + " AS L ON " +
                "L." + FeedReaderContract.FeedLists._ID + " = " + "E." +
                FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME +
                whereClause.toString() + " ORDER BY E." +
                FeedReaderContract.FeedEntries.COLUMN_NAME_POSITION, listnames);

        ArrayList<Entry> arr = new ArrayList<>();
        String cnmListname = FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME;
        String cnmKanji = FeedReaderContract.FeedEntries.COLUMN_NAME_KANJI;
        String cnmReading = FeedReaderContract.FeedEntries.COLUMN_NAME_READING;
        String cnmTier = FeedReaderContract.FeedEntries.COLUMN_NAME_TIER;
        String cnmPosition = FeedReaderContract.FeedEntries.COLUMN_NAME_POSITION;
        
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int listname = cursor.getInt(cursor.getColumnIndexOrThrow(cnmListname));
            String kanji = cursor.getString(cursor.getColumnIndexOrThrow(cnmKanji));
            String reading = cursor.getString(cursor.getColumnIndexOrThrow(cnmReading));
            int tier = cursor.getInt(cursor.getColumnIndexOrThrow(cnmTier));
            int position = cursor.getInt(cursor.getColumnIndexOrThrow(cnmPosition));
            Log.d("entryPos", "entry "  + kanji + " is #" + position);
            arr.add(new Entry(listname, kanji, reading, position, tier));
        }
        cursor.close();
        return arr;
    }

    public int swapEntries(Entry entry1, Entry entry2) {
        SQLiteDatabase db = getWritableDatabase();
        
        String tnm     = FeedReaderContract.FeedEntries.TABLE_NAME;
        String cnmPos  = FeedReaderContract.FeedEntries.COLUMN_NAME_POSITION;
        String cnmList = FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME;
        String cnmKan  = FeedReaderContract.FeedEntries.COLUMN_NAME_KANJI;

        String fmtString = "UPDATE %s SET %s = %d WHERE %s = %d AND %s = '%s'";
        db.execSQL(String.format(fmtString,
                    tnm,
                    cnmPos,  -1,
                    cnmList, entry1.getListname(),
                    cnmKan,  entry1.getKanji()));
        
        db.execSQL(String.format(fmtString,
                    tnm,
                    cnmPos,  entry1.getPosition(),
                    cnmList, entry2.getListname(),
                    cnmKan,  entry2.getKanji()));
        
        db.execSQL(String.format(fmtString,
                    tnm,
                    cnmPos,  entry2.getPosition(),
                    cnmList, entry1.getListname(),
                    cnmKan,  entry1.getKanji()));

        return 1;
    }

    public ArrayList<Preset> getPresets() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedPresets.TABLE_NAME,
                EMPTY_STRING_ARRAY);
        
        ArrayList<Preset> arr = new ArrayList<>();
        String cnmListname = FeedReaderContract.FeedPresets.COLUMN_NAME_LISTNAME;
        String cnmAlgorithm = FeedReaderContract.FeedPresets.COLUMN_NAME_ALGORITHM;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int listname = cursor.getInt(cursor.getColumnIndexOrThrow(cnmListname));
            String algorithm = cursor.getString(cursor.getColumnIndexOrThrow(cnmAlgorithm));
            arr.add(new Preset(listname, algorithm));
        }
        cursor.close();
        return arr;
    }

    public ArrayList<Tag> getTags() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedTags.TABLE_NAME,
                EMPTY_STRING_ARRAY);

        ArrayList<Tag> arr = new ArrayList<>();
        String cnmTag = FeedReaderContract.FeedTags.COLUMN_NAME_TAG;
        
        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            String tag = cursor.getString(cursor.getColumnIndexOrThrow(cnmTag));
            arr.add(new Tag(tag));
        }
        cursor.close();
        return arr;
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
        ArrayList<Entry> entries = getEntries(listname);
        int count = entries.size();
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
