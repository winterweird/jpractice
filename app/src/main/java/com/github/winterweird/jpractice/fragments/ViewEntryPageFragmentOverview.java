package com.github.winterweird.jpractice.fragments;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import java.net.URLEncoder;
import android.webkit.WebView;
import java.io.UnsupportedEncodingException;

import com.github.winterweird.jpractice.R;

public class ViewEntryPageFragmentOverview extends Fragment {
    private String kanji;
    private int listname;
    
    public ViewEntryPageFragmentOverview(int listname, String kanji) {
        this.kanji = kanji;
        this.listname = listname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_entry_page_jisho, container, false);
        return view;
    }
}
