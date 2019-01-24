package com.github.winterweird.jpractice.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.content.Context;
import android.content.ContentValues;
import android.database.sqlite.SQLiteException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

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

    public static synchronized DatabaseHelper getHelper() {
        if (helper == null)
            throw new IllegalStateException("DatabaseHelper context not set");
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
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedLists._ID));
            String listname = cursor.getString(cursor.getColumnIndexOrThrow(cnmListname));
            arr.add(new List(listname));
        }
        cursor.close();
        return arr;
    }

    public String getListname(int listId) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedLists.TABLE_NAME
                + " WHERE " + FeedReaderContract.FeedLists._ID + " = " + listId,
                EMPTY_STRING_ARRAY);
        String lnm = null;
        if (cursor.getCount() == 1) {
            String cnmListname = FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME;
            cursor.moveToFirst();
            lnm = cursor.getString(cursor.getColumnIndexOrThrow(cnmListname));
        }
        cursor.close();
        return lnm;
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

        Cursor cursor = db.rawQuery("SELECT E.* FROM " + FeedReaderContract.FeedEntries.TABLE_NAME +
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
            arr.add(new Entry(listname, kanji, reading, position, tier));
        }
        cursor.close();
        return arr;
    }

    public int swapEntries(Entry entry1, Entry entry2) {
        if (!(entry1.getListname() == entry2.getListname())) {
            throw new IllegalArgumentException("entry1.getListname() == entry2.getListname()");
        }

        SQLiteDatabase db = getWritableDatabase();
        
        String tnm     = FeedReaderContract.FeedEntries.TABLE_NAME;
        String cnmPos  = FeedReaderContract.FeedEntries.COLUMN_NAME_POSITION;
        String cnmList = FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME;
        String cnmKan  = FeedReaderContract.FeedEntries.COLUMN_NAME_KANJI;
        int lnm = entry1.getListname();
        int pos1 = entry1.getPosition();
        int pos2 = entry2.getPosition();

        String fmtString = "UPDATE %s SET %s = %d WHERE %s = %d AND %s = '%s'";

        
        db.execSQL(String.format(fmtString,
                    tnm,
                    cnmPos,  -1,
                    cnmList, lnm,
                    cnmKan,  entry1.getKanji()));

        db.execSQL(String.format(fmtString,
                    tnm,
                    cnmPos,  entry1.getPosition(),
                    cnmList, lnm,
                    cnmKan,  entry2.getKanji()));

        db.execSQL(String.format(fmtString,
                    tnm,
                    cnmPos,  entry2.getPosition(),
                    cnmList, lnm,
                    cnmKan,  entry1.getKanji()));

        return 1;
    }

    public ArrayList<Preset> getPresets() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedPresets.TABLE_NAME,
                EMPTY_STRING_ARRAY);
        
        ArrayList<Preset> arr = new ArrayList<>();
        String cnmId = FeedReaderContract.FeedPresets._ID;
        String cnmListname = FeedReaderContract.FeedPresets.COLUMN_NAME_LISTNAME;
        String cnmAlgorithm = FeedReaderContract.FeedPresets.COLUMN_NAME_ALGORITHM;

        for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(cnmId));
            int listname = cursor.getInt(cursor.getColumnIndexOrThrow(cnmListname));
            String algorithm = cursor.getString(cursor.getColumnIndexOrThrow(cnmAlgorithm));
            arr.add(new Preset(id, listname, algorithm));
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

    /**
     * These methods are broken. TODO: Fix.
     */
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

    public void insert(List l) {
        ContentValues cv = l.getContentValues();
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(FeedReaderContract.FeedLists.TABLE_NAME, null, cv);
    }
    public void insert(Entry e) {
        ContentValues cv = e.getContentValues();
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(FeedReaderContract.FeedEntries.TABLE_NAME, null, cv);
    }
    public void insert(Preset p) {
        ContentValues cv = p.getContentValues();
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(FeedReaderContract.FeedPresets.TABLE_NAME, null, cv);
    }
    public void insert(Tag t) {
        ContentValues cv = t.getContentValues();
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(FeedReaderContract.FeedTags.TABLE_NAME, null, cv);
    }
    public void insert(TaggedWord tw) {
        ContentValues cv = tw.getContentValues();
        SQLiteDatabase db = getWritableDatabase();
        db.insertOrThrow(FeedReaderContract.FeedTaggedWords.TABLE_NAME, null, cv);
    }

    public int entryCount(String listname) {
        ArrayList<Entry> entries = getEntries(listname);
        int count = entries.size();
        return count;
    }

    public void delete(List l) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(FeedReaderContract.FeedLists.TABLE_NAME,
                FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME + " = ?",
                new String[]{l.getListname()});
    }
    
    public void delete(Entry e) {
        SQLiteDatabase db = getWritableDatabase();
        int deleted = db.delete(FeedReaderContract.FeedEntries.TABLE_NAME,
                FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME + " = ? AND " +
                FeedReaderContract.FeedEntries.COLUMN_NAME_KANJI + " = ?",
                new String[]{String.valueOf(e.getListname()), e.getKanji()});

        boolean keepGoing = true;
        int listNm = e.getListname();
        
        // never tell anyone I said it's okay to write for loops like this
        for (int i = e.getPosition() + 1; keepGoing; i++) {
            ContentValues cv = new ContentValues();
            cv.put(FeedReaderContract.FeedEntries.COLUMN_NAME_POSITION, i-1);
            keepGoing = db.update(FeedReaderContract.FeedEntries.TABLE_NAME, cv,
                    FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME + " = ? AND " +
                    FeedReaderContract.FeedEntries.COLUMN_NAME_POSITION + " = ?",
                    new String[]{String.valueOf(listNm), String.valueOf(i)}) == 1;
        }
    }
    
    public void delete(Preset p) {
        // TODO
    }
    
    public void delete(Tag t) {
        // TODO
    }
    
    public void delete(TaggedWord tw) {
        // TODO
    }

    public boolean exists(List l) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedLists.TABLE_NAME +
                FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME + " = ?",
                new String[]{l.getListname()});
        boolean itDoes = c.getCount() == 1;
        c.close();
        return itDoes;
    }

    public boolean exists(Entry e) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedEntries.TABLE_NAME +
                " WHERE " + FeedReaderContract.FeedEntries.COLUMN_NAME_KANJI + " = ? AND " +
                FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME + " = ?",
                new String[]{e.getKanji(), String.valueOf(e.getListname())});
        boolean itDoes = c.getCount() == 1;
        c.close();
        return itDoes;
    }

    public boolean exists(Preset p) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedPresets.TABLE_NAME +
                " WHERE " + FeedReaderContract.FeedPresets._ID + " = ?",
                new String[]{String.valueOf(p.getId())});
        boolean itDoes = c.getCount() == 1;
        c.close();
        return itDoes;
    }

    public boolean exists(Tag t) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedTags.TABLE_NAME +
                " WHERE " + FeedReaderContract.FeedTags.COLUMN_NAME_TAG + " = ?",
                new String[]{t.getTag()});
        boolean itDoes = c.getCount() == 1;
        c.close();
        return itDoes;
    }

    public boolean exists(TaggedWord tw) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + FeedReaderContract.FeedTaggedWords.TABLE_NAME +
                " WHERE " + FeedReaderContract.FeedTaggedWords.COLUMN_NAME_TAG + " = ? AND " +
                FeedReaderContract.FeedTaggedWords.COLUMN_NAME_KANJI_ID + " = ?",
                new String[]{String.valueOf(tw.getTag()), String.valueOf(tw.getKanjiId())});
        boolean itDoes = c.getCount() == 1;
        c.close();
        return itDoes;
    }

    public void update(Entry oldEntry, Entry newEntry) {
        SQLiteDatabase db = getReadableDatabase();
        db.update(FeedReaderContract.FeedEntries.TABLE_NAME, newEntry.getContentValues(),
                FeedReaderContract.FeedEntries.COLUMN_NAME_KANJI + " = ? AND " +
                FeedReaderContract.FeedEntries.COLUMN_NAME_LISTNAME + " = ?",
                new String[]{oldEntry.getKanji(), String.valueOf(oldEntry.getListname())});
    }

    public String createEntryCSVText(String[] lists) {
        StringBuilder sb = new StringBuilder();
        ArrayList<Entry> entries = getEntries(lists);
        for (Entry e : entries) {
            sb.append(getListname(e.getListname()));
            sb.append(",");
            sb.append(e.getKanji());
            sb.append(",");
            sb.append(e.getReading());
            sb.append(",");
            sb.append(e.getPosition());
            sb.append(",");
            sb.append(e.getTier());
            sb.append("\n");
        }
        return sb.toString();
    }

    /**
     * Create database entries based on CSV text.
     * 
     * Makes assumptions about the format of the CSV, and gracefully returns
     * error messages if its assumptions aren't met. Examples of assumptions:
     * - There needs to be exactly n fields in each CSV record, and comma can be
     *   part of none of the fields
     * - Every CSV record is on its own separate line, and every line is a CSV
     *   record
     * - Every field is in its expected position in the CSV
     *   - Format of record: listname,word,reading,position_in_list,tier
     *
     * Tries to operate individually on each line, and to insert the required
     * values, and records an error message on every failure. Returns a result
     * containing the general status of the operation, as well as error messages
     * for any operations that failed, or any criteria that weren't met. If an
     * operation produces an error, none of the changes should actually be
     * performed on the database.
     *
     * @param csv A newline-separated list of CSV records representing the data
     *
     * @return A DbUpdateResult object describing the result
     */
    public DbUpdateResult insertFromCSVText(String csv) {
        DbUpdateResult res = new DbUpdateResult();

        int FIELDS_PER_RECORD = 5;
        
        SQLiteDatabase db = getWritableDatabase();
        String[] lines = csv.split("\n");
        for (int i = 0; i < lines.length; i++) {
            String ln = lines[i];
            String[] fields = ln.split(",");

            DbUpdateResult.Success successRec = new DbUpdateResult.Success(ln, i);

            // check that record has correct number of fields
            if (fields.length != 5) {
                String shortMsg = String.format("Wrong field number (%d)", fields.length);
                String longMsg = String.format("FORMAT ERROR - %s:%d: number of fields must be %d but was %d", 
                        ln, i, FIELDS_PER_RECORD, fields.length);
                res.errors.add(new DbUpdateResult.Error(ln, i, shortMsg, longMsg));
                continue;
            }

            // try to execute the required changes
            db.beginTransaction();
            try {
                List l = new List(fields[0]);
                int listId = l.id();
                
                if (listId == -1) { // create a new list
                    insert(l);
                    listId = l.id(); // update the id
                    successRec.add(String.format("List %s (%d) created", l.getListname(), listId));
                    successRec.createdList = l;
                }
                
                Entry e = new Entry(
                        listId,
                        fields[1],
                        fields[2],
                        Integer.parseInt(fields[3]),
                        Integer.parseInt(fields[4]));
                insert(e);

                db.setTransactionSuccessful();
                successRec.add(String.format("Word %s (%s) inserted",
                            e.getKanji(), e.getReading()));
                res.successes.add(successRec);
            } catch (SQLiteException e) {
                String shortMsg = "SQLite insertion error";
                String longMsg = e.getMessage();
                res.errors.add(new DbUpdateResult.Error(ln, i, shortMsg, longMsg));
            } catch (NumberFormatException e) {
                String shortMsg = "Integer parsing error";
                String longMsg = String.format("%s:%d: Failure to parse integer %s", ln, i, e.getMessage());
                res.errors.add(new DbUpdateResult.Error(ln, i, shortMsg, longMsg));
            } finally {
                db.endTransaction();
            }
        }
        return res;
    }

    /**
     * A record containing aggregate result info as well as individual errors.
     *
     * Should be easy to query.
     */
    public static class DbUpdateResult {
        public ArrayList<Error> errors = new ArrayList<>();
        public ArrayList<Success> successes = new ArrayList<>();
        public static class Error {
            public String line;
            public int lineNo;
            public String shortMsg;
            public String longMsg;
            public Error(String ln, int lnNo, String s, String l) {
                line = ln; lineNo = lnNo; shortMsg = s; longMsg = l;
            }
            public String toString() {
                return String.format("Error:%s:%d: %s", line, lineNo, shortMsg);
            }
        }
        public static class Success {
            public String line;
            public int lineNo;
            public ArrayList<String> actions = new ArrayList<>();
            public List createdList = null;
            public Success(String ln, int lnNo) {
                line = ln; lineNo = lnNo;
            }
            public String toString() {
                return String.format("Success:%s:%d: %s", line, lineNo,
                        actions.stream().collect(Collectors.joining("; ")));
            }

            public void add(String action) {
                actions.add(action);
            }
        }
        
        public ArrayList<List> createdLists() {
            return new ArrayList<>(successes.stream()
                .filter(s -> s.createdList != null)
                .map(s -> s.createdList)
                .collect(Collectors.toList()));
        }
        
        
        public String errorsToString() {
            return errors.stream()
                .map(e -> e.toString())
                .collect(Collectors.joining("\n"));
        }
        public String successesToString() {
            return successes.stream()
                .map(s -> s.toString())
                .collect(Collectors.joining("\n"));
        }
        public String toString() {
            return String.format("Database operation completed with %d errors and %d successes",
                    errors.size(), successes.size());
        }
    }

    /**
     * Get id of a given data entry.
     *
     * TODO: Update with overload for each of the different
     * DatabaseEntryInterface implementing classes.
     */
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

    public int idOf(List l) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + FeedReaderContract.FeedLists._ID + " FROM "
                + FeedReaderContract.FeedLists.TABLE_NAME + " WHERE "
                + FeedReaderContract.FeedLists.COLUMN_NAME_LISTNAME + " = ?",
                new String[]{l.getListname()});
        
        int id = -1;
        try {
            cursor.moveToFirst();
            if (!cursor.isAfterLast())
                id = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedLists._ID));
        } catch (SQLiteException e) {
            e.printStackTrace();
        } finally {
            cursor.close();
        }
        return id;
    }
}
