package com.github.winterweird.jpractice.fragments;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.os.Bundle;
import android.view.View;
import java.net.URLEncoder;
import android.webkit.WebView;
import java.io.UnsupportedEncodingException;
import android.widget.EditText;
import android.widget.TextView;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.widget.Toast;
import android.app.Activity;
import android.content.Intent;

import android.util.Log;

import java.util.ArrayList;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.ViewEntryActivity;
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.Entry;
import com.github.winterweird.jpractice.japanese.JapaneseTextProcessingUtilities;

public class ViewEntryPageFragmentOverview extends Fragment {
    private String kanji;
    private String reading;
    private int listname;
    private int position;
    private int tier;

    private EditText kanjiContent;
    private EditText readingContent;
    private EditText meaningsContent;
    private EditText listContent;

    private View nextButton;
    private View prevButton;
    
    public ViewEntryPageFragmentOverview(int listname, String kanji) {
        this.kanji = kanji;
        this.listname = listname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_entry_page_overview, container, false);

        kanjiContent    = view.findViewById(R.id.viewEntryOverviewKanjiContent);
        readingContent  = view.findViewById(R.id.viewEntryOverviewReadingContent);
        meaningsContent = view.findViewById(R.id.viewEntryOverviewMeaningsContent);
        listContent     = view.findViewById(R.id.viewEntryOverviewListContent);

        setEditable(kanjiContent, false);
        setEditable(readingContent, false);
        setEditable(meaningsContent, false);
        setEditable(listContent, false);
        
        DatabaseHelper dbhelper = DatabaseHelper.getHelper(getContext());
        String lname = dbhelper.getListname(this.listname);
        final ArrayList<Entry> entries = dbhelper.getEntries(lname);
        
        Entry matchEntry = new Entry(this.listname, this.kanji, "", 0);
        Entry actualEntry = entries.get(entries.indexOf(matchEntry));

        this.reading = actualEntry.getReading();
        this.position = actualEntry.getPosition();
        this.tier = actualEntry.getTier();

        prevButton = view.findViewById(R.id.buttonsWrapperLayoutLeft);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    Toast.makeText(getActivity(), "No previous list entry",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    navigateToViewEntry(entries.get(position-1), R.anim.enter_left,
                            R.anim.leave_right);
                }
            }
        });
        nextButton = view.findViewById(R.id.buttonsWrapperLayoutRight);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == entries.size()-1) {
                    Toast.makeText(getActivity(), "No next list entry",
                            Toast.LENGTH_LONG).show();
                }
                else {
                    navigateToViewEntry(entries.get(position+1), R.anim.enter_right,
                            R.anim.leave_left);
                }
            }
        });
        
        kanjiContent.setText(this.kanji);
        readingContent.setText(this.reading);
        listContent.setText(lname);
        // TODO: set meanings text
        return view;
    }

    public void reset() {
        kanjiContent.setText(this.kanji);
        readingContent.setText(this.reading);
        setEditable(false);
    }

    public boolean commit() {
        boolean continueEditMode = false;
        
        String k = this.kanjiContent.getText().toString().replaceAll("\\s+", "");
        if (k.isEmpty()) {
            Toast.makeText(getContext(), "Kanji cannot be empty", Toast.LENGTH_LONG).show();
            continueEditMode = true;
        }
        else if (!JapaneseTextProcessingUtilities.isValidWordKanji(k)) {
            Toast.makeText(getContext(), "Not a valid word: must contain only " +
                    "Japanese characters and at least one kanji", Toast.LENGTH_LONG).show();
            continueEditMode = true;
        }
        
        String r = this.readingContent.getText().toString().replaceAll("\\s+", "");
        if (r.isEmpty()) {
            Toast.makeText(getContext(), "Reading cannot be empty", Toast.LENGTH_LONG).show();
            continueEditMode = true;
        }
        else if (!JapaneseTextProcessingUtilities.isValidWordReading(r)) {
            Toast.makeText(getContext(), "Not a valid reading: must only contain kana",
                    Toast.LENGTH_LONG).show();
            continueEditMode = true;
        }

        if (!continueEditMode) {
            Entry oldEntry = new Entry(this.listname, this.kanji, this.reading,
                    this.position, this.tier);
            Entry newEntry = new Entry(this.listname, k, r, this.position, this.tier);
            
            DatabaseHelper dbhelper = DatabaseHelper.getHelper(getContext());
            if (dbhelper.exists(newEntry) && !k.equals(this.kanji)) {
                Toast.makeText(getContext(), "Kanji already exists in list",
                        Toast.LENGTH_LONG).show();
                continueEditMode = true;
            }
            else if (!k.equals(this.kanji) || !r.equals(this.reading)){
                dbhelper.update(oldEntry, newEntry);
            }
        }
        
        setEditable(continueEditMode);
        return continueEditMode;
    }

    public void setEditable(boolean editable) {
        setEditable(kanjiContent, editable);
        setEditable(readingContent, editable);
        nextButton.setClickable(!editable);
        prevButton.setClickable(!editable);
    }

    private void setEditable(EditText et, boolean editable) {
        et.setEnabled(editable);
        et.setFocusableInTouchMode(editable);
        et.setBackgroundTintList(ColorStateList.valueOf(
                    editable ? Color.BLACK
                             : Color.TRANSPARENT));
    }

    private void navigateToViewEntry(Entry e, int anim1, int anim2) {
        Activity act = getActivity();
        Intent intent = new Intent(act, ViewEntryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
        intent.putExtra(act.getString(R.string.intentViewEntryList), e.getListname());
        intent.putExtra(act.getString(R.string.intentViewEntryKanji), e.getKanji());
        startActivity(intent);
        act.overridePendingTransition(anim1, anim2);
        act.finish();
    }
}
