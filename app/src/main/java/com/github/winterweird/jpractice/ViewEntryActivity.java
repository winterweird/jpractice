package com.github.winterweird.jpractice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.Menu;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.support.v4.view.ViewPager;
import android.support.design.widget.TabLayout;
import android.graphics.Color;

import android.util.Log;

import com.github.winterweird.jpractice.adapters.ViewEntryTabsPagerAdapter;

public class ViewEntryActivity extends ToolbarBackButtonActivity {
    private String kanji;
    private int listname;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_entry);

        kanji = getIntent().getExtras().getString(
                getResources().getString(R.string.intentViewEntryKanji));
        listname = getIntent().getExtras().getInt(
                getResources().getString(R.string.intentViewEntryList));
        setTitle(kanji);

        TabLayout tabs = findViewById(R.id.tabs);
        tabs.setSelectedTabIndicatorColor(getColor(R.color.primaryColor));
        tabs.setTabTextColors(Color.parseColor("#727272"), // gray
                getColor(R.color.primaryColor));
        ViewPager pager = findViewById(R.id.activity_main_layout);
        
        ViewEntryTabsPagerAdapter adapter = new ViewEntryTabsPagerAdapter(
                this, getSupportFragmentManager(), listname, kanji);

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        FloatingActionButton fab = findViewById(R.id.floatingActionButtonEdit);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Test", "hello floating action button edit mode");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewEntryActionEditEntry:
                Log.d("Test", "hello edit mode");
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_entry_menu, menu);
        return true;
    }
}
