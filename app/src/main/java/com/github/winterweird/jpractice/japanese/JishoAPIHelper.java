package com.github.winterweird.jpractice.japanese;

import java.io.BufferedInputStream;
import javax.net.ssl.HttpsURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.io.InputStream;
import java.io.IOException;
import android.util.Log;
import java.net.MalformedURLException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.io.ByteArrayOutputStream;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import java.util.ArrayList;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Helper class for the Jisho REST API.
 */
public class JishoAPIHelper {
    /**
     * Retrieve the best match of the given kanji and reading using the Jisho
     * API, perform the callback on the result.
     *
     * The priorities of "best match" are as follows:
     * - kanji and reading are both equal to the result
     * - kanji is equal but reading is different (will choose first reading)
     * - kanji is not equal but reading is (will choose first kanji)
     * - neither kanji nor reading is equal, but there are results (will choose
     *   first result)
     * - there were no results (returns null)
     *
     * @param kanji The word to match with
     * @param reading The reading to match with
     * @param callback The action to be performed on the retrieved Result
     */
    public static void getBestMatch(String kanji, String reading, Callback callback) {
        new Thread(() -> {
            Result r = getBestMatchBlocking(kanji, reading);
            callback.callback(r);
        }).start();
    }

    /**
     * Retrieve the best match of the given kanji and reading using the Jisho
     * API, and return the result.
     *
     * The priorities of "best match" are as follows:
     * - kanji and reading are both equal to the result
     * - kanji is equal but reading is different (will choose first reading)
     * - kanji is not equal but reading is (will choose first kanji)
     * - neither kanji nor reading is equal, but there are results (will choose
     *   first result)
     * - there were no results (returns null)
     *
     * @param kanji The word to match with
     * @param reading The reading to match with
     *
     * @return The result which is the best match, or null if there were no
     * matching results
     */
    public static Result getBestMatchBlocking(String kanji, String reading) {
        Result res = null;
        URL url = null;
        
        // try creating the url
        try  {
            String file = "api/v1/search/words?keyword="  + URLEncoder.encode(kanji, "UTF-8");
            Log.d("Test", file);
            url = new URL("https://jisho.org/" + file);
        } catch (Exception e) {
            //throw new RuntimeException(e);
            Log.e("Test", "Error: ", e);
            return null; // early return, won't be able to use the API anyways at this point
        }
        
        // try retrieving the result
        try (InputStream is = new BufferedInputStream(url.openStream())){
            JSONObject json = toJSON(is);
            JSONArray data = json.getJSONArray("data");
            int dataSize = data.length();
            for (int i = 0; i < dataSize; i++) {
                JSONObject dataObj = data.getJSONObject(i);
                
                List<Result> resList = Result.fromJSON(dataObj);
                for (Result r : resList) {
                    if (r.closer(res, kanji, reading)) {
                        res = r;
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Test", "Error: ", e);
        }
        
        return res;
    } 

    /**
     * Callback interface of action to perform when results arrive.
     */
    public static interface Callback {
        /**
         * The callback to perform.
         *
         * Usage: r -&gt; &lt;callback action&gt;
         */
        void callback(Result r);
    }

    /**
     * Result record.
     * 
     * Contains information about the kanji, reading and meanings of the term.
     */
    public static class Result {
        public String kanji;
        public String reading;
        public List<List<String>> meanings;

        /**
         * Retrieve a list of results based on a data object, which differ in
         * word or reading.
         *
         * @param json The JSON data object to retrieve the result from
         * @return A list of results which differ in word and/or reading, but
         * not in meanings
         *
         * @throws JSONException if there was an error with the expected JSON
         * format
         */
        public static List<Result> fromJSON(JSONObject json) throws JSONException {
            List<Result> results = new ArrayList<>();
            if (!json.has("japanese")) return results;
            if (!json.has("senses"))   return results;

            // all results of same data obj have senses in common
            List<List<String>> senses = new ArrayList<>();
            JSONArray sensesArr = json.getJSONArray("senses");
            for (int i = 0; i < sensesArr.length(); i++) {
                List<String> senseList = new ArrayList<>();
                JSONArray english = sensesArr.getJSONObject(i).getJSONArray("english_definitions");
                for (int j = 0; j < english.length(); j++) {
                    senseList.add(english.getString(j));
                }
                senses.add(senseList);
            }

            // get the words in separate results
            JSONArray japaneseArr = json.getJSONArray("japanese");
            for (int i = 0; i < japaneseArr.length(); i++) {
                JSONObject wObj = japaneseArr.getJSONObject(i);
                if (!wObj.has("word")) continue;
                if (!wObj.has("reading")) continue;
                
                Result r = new Result();
                r.kanji = wObj.getString("word");
                r.reading = wObj.getString("reading");
                r.meanings = senses;
                results.add(r);
            }
            return results;
        }

        /**
         * Determine whether the result is closer than the other result in terms
         * of how well it matches the desired kanji and desired reading.
         *
         * An object is closer if its kanji is closer to the desired kanji, or
         * if not, if its reading is closer. Here, we'll assume any string that
         * is not equal to the string it's compared to is not "closer" to that
         * string than any other.
         *
         * @param r The result to compare to
         * @param desiredKanji The word we're aiming for
         * @param desiredReading The reading we're aiming for
         *
         * @return true if this is closer than r, false otherwise
         */
        public boolean closer(Result r, String desiredKanji, String desiredReading) {
            return r == null
                || desiredKanji.equals(kanji) && !desiredKanji.equals(r.kanji)
                || desiredReading.equals(reading) && !desiredReading.equals(r.reading);
        }

        @Override
        public String toString() {
            return String.format("{kanji: %s, reading: %s, meanings: %s}", kanji, reading, meanings);
        }
    }
    
    /**
     * Get a JSON object from a valid JSON string from the input stream.
     *
     * @param inputStream The stream to get the object from
     * @return A JSONObject based on the stream
     *
     * @throws IOException if there is an IO error
     * @throws JSONException if the JSON object couldn't be constructed
     */
    private static JSONObject toJSON(InputStream inputStream) throws IOException, JSONException {
        try(ByteArrayOutputStream result = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }

            return new JSONObject(result.toString("UTF-8"));
        }
    }

    /**
     * Helper method: translate the stack trace to a string.
     * 
     * @param e The stacktrace to translate
     * @return A string version of the stacktrace, with the stack elements
     * separated by newlines
     */
    private static String stacktraceAsString(Exception e) {
        return Arrays.stream(e.getStackTrace())
                     .map(s->s.toString())
                     .collect(Collectors.joining("\n"));
    }
}
