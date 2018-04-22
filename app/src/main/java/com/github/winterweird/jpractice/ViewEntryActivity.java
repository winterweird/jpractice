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
import android.support.v4.app.Fragment;
import android.os.Handler;

import android.util.Log;

import com.github.winterweird.jpractice.adapters.ViewEntryTabsPagerAdapter;
import com.github.winterweird.jpractice.components.DisableableViewPager;
import com.github.winterweird.jpractice.fragments.ViewEntryPageFragmentOverview;

public class ViewEntryActivity extends ToolbarBackButtonActivity {
    private String kanji;
    private int listname;
    private ViewEntryTabsPagerAdapter adapter;
    private DisableableViewPager pager;
    private TabLayout tabs;
    private FloatingActionButton editfab;
    private FloatingActionButton savefab;
    private FloatingActionButton cancelfab;
    
    private Handler handler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_entry);

        kanji = getIntent().getExtras().getString(
                getResources().getString(R.string.intentViewEntryKanji));
        listname = getIntent().getExtras().getInt(
                getResources().getString(R.string.intentViewEntryList));
        setTitle(kanji);

        tabs = findViewById(R.id.tabs);
        tabs.setSelectedTabIndicatorColor(getColor(R.color.primaryColor));
        tabs.setTabTextColors(Color.parseColor("#727272"), // gray
                getColor(R.color.primaryColor));
        pager = findViewById(R.id.activity_main_layout);
        
        adapter = new ViewEntryTabsPagerAdapter(this, getSupportFragmentManager(), listname, kanji);

        pager.setAdapter(adapter);
        tabs.setupWithViewPager(pager);

        editfab = findViewById(R.id.floatingActionButtonEdit);
        editfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setEditMode(true);
            }
        });

        cancelfab = findViewById(R.id.floatingActionButtonCancel);
        cancelfab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewEntryPageFragmentOverview f1 = (ViewEntryPageFragmentOverview) getFragment(0);
                f1.reset();
                setEditMode(false);
            }
        });
        
        savefab = findViewById(R.id.floatingActionButtonSave);
        savefab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewEntryPageFragmentOverview f1 = (ViewEntryPageFragmentOverview) getFragment(0);
                boolean continueEditMode = f1.commit();
                setEditMode(continueEditMode);
            }
        });
        
        setEditMode(false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.viewEntryActionEditEntry:
                setEditMode(true);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.view_entry_menu, menu);
        return true;
    }

    public void setEditMode(boolean active) {
        pager.setPagingEnabled(!active);
        
        if (active) {
            tabs.getTabAt(0).select();
            editfab.setVisibility(View.GONE);
            savefab.setVisibility(View.VISIBLE);
            cancelfab.setVisibility(View.VISIBLE);
        }
        else {
            editfab.setVisibility(View.VISIBLE);
            savefab.setVisibility(View.GONE);
            cancelfab.setVisibility(View.GONE);
        }
        
        ViewEntryPageFragmentOverview f1 = (ViewEntryPageFragmentOverview) getFragment(0);
        if (f1 != null)
            f1.setEditable(active);
        else
            handler.postDelayed(() -> setEditMode(active), 100);
    }

    private Fragment getFragment(int index) {
        return getSupportFragmentManager()
            .findFragmentByTag("android:switcher:" + R.id.activity_main_layout + ":" + index);
    }
}
