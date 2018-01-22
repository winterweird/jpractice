package com.github.winterweird.jpractice;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;

public class PracticeOverviewActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.practice_overview);

        Toolbar toolbar = (Toolbar)findViewById(R.id.genericToolbar);
        setSupportActionBar(toolbar);
    }
}
