package com.github.winterweird.jpractice.fragments;

import android.annotation.SuppressLint;
import android.os.Looper;
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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.DividerItemDecoration;
import android.os.Handler;
import android.widget.Button;
import android.support.constraint.ConstraintSet;
import android.support.constraint.ConstraintLayout;

import android.util.Log;

import java.util.stream.Collectors;
import java.util.ArrayList;
import java.util.HashSet;

import com.github.winterweird.jpractice.R;
import com.github.winterweird.jpractice.ViewEntryActivity;
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.Entry;
import com.github.winterweird.jpractice.japanese.JapaneseTextProcessingUtilities;
import com.github.winterweird.jpractice.japanese.JishoAPIHelper;
import com.github.winterweird.jpractice.adapters.SharedKanjiListAdapter;
import com.github.winterweird.jpractice.adapters.SharedKanjiListAdapter.KanjiWordPair;
import com.github.winterweird.jpractice.util.ToastUtil;

@SuppressLint("ValidFragment")
public class ViewEntryPageFragmentOverview extends Fragment {
    private String kanji;
    private String reading;
    private String meanings;
    private int listname;
    private int position;
    private int tier;
    private int nMeanings;
    private boolean showMeanings = false;

    private EditText kanjiContent;
    private EditText readingContent;
    private EditText meaningsContent;
    private EditText listContent;
    private Button meaningsButton;
    private ConstraintLayout constraintLayout;

    private View nextButton;
    private View prevButton;

    @SuppressLint("ValidFragment")
    public ViewEntryPageFragmentOverview(int listname, String kanji) {
        this.kanji = kanji;
        this.listname = listname;
    }
    
    private Handler handler = new Handler();
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.view_entry_page_overview, container, false);
        if (this.kanji == null) return view;

        kanjiContent     = view.findViewById(R.id.viewEntryOverviewKanjiContent);
        readingContent   = view.findViewById(R.id.viewEntryOverviewReadingContent);
        meaningsContent  = view.findViewById(R.id.viewEntryOverviewMeaningsContent);
        meaningsButton   = view.findViewById(R.id.viewEntryOverviewShowHiddenMeaningsButton);
        listContent      = view.findViewById(R.id.viewEntryOverviewListContent);
        constraintLayout = view.findViewById(R.id.viewEntryOverviewConstraintLayout);

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

        HashSet<Integer> checkedKanji = new HashSet<>();
        final ArrayList<KanjiWordPair> sharedKanjiArr = new ArrayList<>();
        for (int i = 0; i < this.kanji.length(); i++) {
            Integer cp = this.kanji.codePointAt(i);
            if (JapaneseTextProcessingUtilities.isKanji(cp)) {
                if (!checkedKanji.contains(cp)) {
                    checkedKanji.add(cp);
                    for (Entry e : entries) {
                        if (e == actualEntry) continue;
                        if (e.getKanji().indexOf(cp) != -1) {
                            sharedKanjiArr.add(new KanjiWordPair(this.kanji.substring(i, i+1), e));
                        }
                    }
                }
            }
        }

        final Activity act = getActivity();
        RecyclerView rv = view.findViewById(R.id.viewEntryOverviewSharedRecyclerView);
        SharedKanjiListAdapter adapter = new SharedKanjiListAdapter(act, sharedKanjiArr);
        adapter.setOnItemClickListener(new SharedKanjiListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SharedKanjiListAdapter.ItemViewHolder viewHolder, int position) {
                navigateToViewEntry(sharedKanjiArr.get(position).getWord(), 0, 0);
            }
        });

        rv.setLayoutManager(new LinearLayoutManager(act));
        rv.addItemDecoration(new DividerItemDecoration(act, DividerItemDecoration.VERTICAL));
        rv.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            TextView noSharedKanji = view.findViewById(R.id.viewEntryOverviewNoSharedContentLabel);
            noSharedKanji.setText("No shared kanji");
        }

        prevButton = view.findViewById(R.id.buttonsWrapperLayoutLeft);
        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (position == 0) {
                    ToastUtil.show(act, "No previous list entry");
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
                    ToastUtil.show(act, "No next list entry");
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
        JishoAPIHelper.getBestMatch(this.kanji, this.reading, match -> {
            if (match != null) {
                nMeanings = match.meanings.size();
                String s = match.meanings.stream()
                    .map(l -> "- " + l.stream().collect(Collectors.joining(", ")))
                    .collect(Collectors.joining("\n"));
                handler.post(() -> {
                    meaningsContent.setText(s);
                    setMeaningsVisibility(showMeanings);
                });
            }
            else {
                ToastUtil.show(getContext(), "Error retrieving meanings");
            }
        });
        meaningsButton.setOnClickListener(v -> setMeaningsVisibility(!showMeanings));
        return view;
    }

    private void setMeaningsVisibility(boolean visible) {
        // determine plurality of word "meaning"
        String plurality = String.format("meaning%s", nMeanings != 1 ? "s" : "");
        meaningsButton.setText(visible ? String.format("Hide %s", plurality)
                                       : String.format("Show %d %s", nMeanings, plurality));
        
        // set visibility of meanings
        meaningsContent.setVisibility(visible ? View.VISIBLE : View.GONE);

        // set the constraints of the label below
        int bottomOf = visible ? R.id.viewEntryOverviewMeaningsContent
                               : R.id.viewEntryOverviewShowHiddenMeaningsButton;
        
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(constraintLayout);
        constraintSet.connect(R.id.viewEntryOverviewTagsLabel, ConstraintSet.TOP,
                bottomOf, ConstraintSet.BOTTOM, 0);
        constraintSet.applyTo(constraintLayout);
        
        // set the class variable indicating visibility
        showMeanings = visible;
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
            ToastUtil.show(getContext(), "Kanji cannot be empty");
            continueEditMode = true;
        }
        else if (!JapaneseTextProcessingUtilities.isValidWordKanji(k)) {
            ToastUtil.show(getContext(), "Not a valid word: must contain only Japanese characters and at least one kanji");
            continueEditMode = true;
        }
        
        String r = this.readingContent.getText().toString().replaceAll("\\s+", "");
        if (r.isEmpty()) {
            ToastUtil.show(getContext(), "Reading cannot be empty");
            continueEditMode = true;
        }
        else if (!JapaneseTextProcessingUtilities.isValidWordReading(r)) {
            ToastUtil.show(getContext(), "Not a valid reading: must only contain kana");
            continueEditMode = true;
        }

        if (!continueEditMode) {
            Entry oldEntry = new Entry(this.listname, this.kanji, this.reading,
                    this.position, this.tier);
            Entry newEntry = new Entry(this.listname, k, r, this.position, this.tier);
            
            DatabaseHelper dbhelper = DatabaseHelper.getHelper(getContext());
            if (dbhelper.exists(newEntry) && !k.equals(this.kanji)) {
                ToastUtil.show(getContext(), "Kanji already exists in list");
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
