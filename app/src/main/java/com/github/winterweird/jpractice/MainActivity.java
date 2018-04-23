package com.github.winterweird.jpractice;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.content.Context;

// for debug purposes
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.data.Entry;

/**
 * The main activity of the application.
 *
 * This is the activity that gets called on startup, and is the first screen the
 * user of the application will see.
 */
public class MainActivity extends AppCompatActivity {
    /**
     * Called when the activity is first created.
     *
     * Sets they layout, the support toolbar, and sets up navigation listeners
     * for each of the buttons which start child activities.
     *
     * @param savedInstanceState The saved instance state
     */
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

    /**
     * Helper method: set the click listener of a navigation button.
     *
     * @param btn The button to add a listener to
     * @param cls The activity class to open when the button is clicked
     */
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

    /**
     * Helper method: use to fix a broken entry while debugging.
     *
     * Sometimes something that is not properly implemented or something that
     * I'm experimenting with can put an entry in a broken state. This method is
     * here to fix broken entries. It's particularly useful in the case when one
     * entry accidentally gets stuck with position -1, which has happened at
     * least once.
     *
     * @param broken The entry that is broken
     * @param fixed What the fixed entry looks like
     */
    private void fix(Entry broken, Entry fixed) {
        DatabaseHelper dbhelper = DatabaseHelper.getHelper(this);
        dbhelper.update(broken, fixed);
    }
}
