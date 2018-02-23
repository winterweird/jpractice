package com.github.winterweird.jpractice.japanese;

public class JapaneseTextProcessingUtilities {
    public static boolean isIdeographicIterationMark(char ch) {
        return ch == '々';
    }
    public static boolean isIdeographicIterationMark(int ch) {
        return ch == Character.codePointAt("々", 0); // tbh this is the easiest way for me to know this works
    }
    public static boolean isKanji(char ch) {
        return Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
    }
    public static boolean isKanji(int ch) {
        return Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ch == Character.codePointAt("々", 0);
    }
    public static boolean isHiragana(char ch) {
        return Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.HIRAGANA;
    }
    public static boolean isHiragana(int ch) {
        return Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.HIRAGANA;
    }
    public static boolean isKatakana(char ch) {
        return Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.KATAKANA;
    }
    public static boolean isKatakana(int ch) {
        return Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.KATAKANA;
    }
    public static boolean isKana(char ch) {
        return isHiragana(ch) || isKatakana(ch);
    }
    public static boolean isKana(int ch) {
        return isHiragana(ch) || isKatakana(ch);
    }
    public static boolean isJapanese(char ch) {
        return isKana(ch) || isKanji(ch) || isIdeographicIterationMark(ch);
    }
    public static boolean isJapanese(int ch) {
        return isKana(ch) || isKanji(ch) || isIdeographicIterationMark(ch);
    }

    public static boolean isJapanese(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!isJapanese(s.codePointAt(i))) return false;
        }
        return true;
    }

    public static boolean isValidWordKanji(String s) {
        boolean haskanji = false;
        for (int i = 0; i < s.length(); i++) {
            int cp = s.codePointAt(i);
            if (!isJapanese(cp)) return false;
            if (isKanji(cp)) haskanji = true;
        }
        return haskanji;
    }

    public static boolean isValidWordReading(String s) {
        for (int i = 0; i < s.length(); i++) {
            int cp = s.codePointAt(i);
            if (!isKana(cp)) return false;
        }
        return true;
    }
}
