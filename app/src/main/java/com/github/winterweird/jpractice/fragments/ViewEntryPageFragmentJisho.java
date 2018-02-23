package com.github.winterweird.jpractice.fragments;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import java.net.URLEncoder;
import android.webkit.WebView;
import java.io.UnsupportedEncodingException;
import android.widget.Toast;

import com.github.winterweird.jpractice.R;
import android.os.Handler;

public class ViewEntryPageFragmentJisho extends Fragment {
    String kanji;
    Handler handler = new Handler();

    public ViewEntryPageFragmentJisho(String kanji) {
        this.kanji = kanji;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_entry_page_jisho, container, false);
        WebView wv = view.findViewById(R.id.jishoWebView);
        setUrl(wv);
        return view;
    }

    private void setUrl(WebView wv) {
        try {
            wv.loadUrl("https://jisho.org/search/"+URLEncoder.encode(kanji, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            Toast.makeText(getContext(), "Error loading search for " + kanji,
                    Toast.LENGTH_LONG).show();
        }
    }
}
