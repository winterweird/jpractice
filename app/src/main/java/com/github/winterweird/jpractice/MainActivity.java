package com.github.winterweird.jpractice;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;

public class MainActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Toolbar toolbar = (Toolbar)findViewById(R.id.mainToolbar);
        setSupportActionBar(toolbar);

        Button practiceBtn = (Button)findViewById(R.id.mainButtonPractice);
        Button listBtn = (Button)findViewById(R.id.mainButtonList);
        Button findBtn = (Button)findViewById(R.id.mainButtonFind);

        navigateListener(practiceBtn, PracticeOverviewActivity.class);
        navigateListener(listBtn, NamedListsActivity.class);
        navigateListener(findBtn, FindWordsActivity.class);
    }

    // Helper method to avoid unnecessary repetition
    private void navigateListener(Button btn, Class<?> cls) {
        final Class<?> clsFinal = cls;
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, clsFinal);
                startActivity(intent);
            }
        });
    }
}
