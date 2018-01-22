package com.github.winterweird.jpractice.japanese;

public class JapaneseTextProcessingUtilities {
    public static boolean isKanji(char ch) {
        return Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
    }
    public static boolean isKanji(int ch) {
        return Character.UnicodeBlock.of(ch) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS;
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
        return isKana(ch) || isKanji(ch);
    }
    public static boolean isJapanese(int ch) {
        return isKana(ch) || isKanji(ch);
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
