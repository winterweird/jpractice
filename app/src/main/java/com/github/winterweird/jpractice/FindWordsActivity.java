package com.github.winterweird.jpractice;

// damn
import android.app.Activity;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebChromeClient;
import android.webkit.WebViewClient;
import android.view.Window;
import android.view.WindowManager;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.util.DisplayMetrics;
import android.widget.Toast;
import android.os.Build;
import android.content.Context;
import android.graphics.Point;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.Intent;
import android.database.Cursor;

// own classes
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.dialogs.ViewportWidthAdjustmentDialog;
import com.github.winterweird.jpractice.dialogs.CreateDatabaseEntryDialog;
import com.github.winterweird.jpractice.dialogs.CreateNewListDialog;

// Java API
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;

public class FindWordsActivity extends AppCompatActivity
            implements ViewportWidthAdjustmentDialog.ViewportWidthAdjustmentDialogListener {
            
    private int MIN_PAGE_WIDTH;
    private int MAX_PAGE_WIDTH;
    private int DESIRED_PAGE_WIDTH; // TODO: make this not caps
    private WebView webview;
    private SharedPreferences.Editor edit;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.find_words);
        Toolbar toolbar = (Toolbar)findViewById(R.id.genericToolbar);
        setSupportActionBar(toolbar);

        SharedPreferences prefs = this.getPreferences(Context.MODE_PRIVATE);
        edit = prefs.edit();
        Resources res = getResources();
        

        MIN_PAGE_WIDTH = res.getInteger(R.integer.viewportWidthSeekBarMin);
        MAX_PAGE_WIDTH = MIN_PAGE_WIDTH + res.getInteger(R.integer.viewportWidthSeekBarRange);
        DESIRED_PAGE_WIDTH = prefs.getInt(getString(R.string.preferencesViewportWidth), 
                res.getInteger(R.integer.viewportWidthSeekBarDefault));
        
        webview = (WebView)findViewById(R.id.findWebView);
        
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        
        final Activity activity = this;
        webview.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode,
                    String description, String failingUrl) {
                Toast.makeText(activity, "Error: " + description, Toast.LENGTH_LONG).show();
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                updateViewportWidth();
                super.onPageFinished(view, url);
            }
        });
        
        Intent intent = getIntent();
        String word = null;
        if (intent.getExtras() != null)
            word = intent.getExtras().getString(res.getString(R.string.intentJishoSearchWord));
        if (word == null)
            webview.loadUrl("https://jisho.org/");
        else
            try {
                webview.loadUrl("https://jisho.org/search/"+URLEncoder.encode(word, "UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                Toast.makeText(this, "Error loading search for " + word, Toast.LENGTH_LONG).show();
                finish();
            }
    }

    public void showViewportWidthAdjustmentDialog() {
        DialogFragment dialog = new ViewportWidthAdjustmentDialog(MIN_PAGE_WIDTH, MAX_PAGE_WIDTH, DESIRED_PAGE_WIDTH);
        dialog.show(getSupportFragmentManager(), "ViewportWidthAdjustmentDialog");
    }

    public void showCreateEntryDialog() {
        DatabaseHelper dbhelper = DatabaseHelper.getHelper(this);
        int count = dbhelper.getLists().size();
        
        if (count == 0) {
            Toast.makeText(this, "You haven't created any lists", Toast.LENGTH_LONG).show();
        }
        else {
            DialogFragment dialog = new CreateDatabaseEntryDialog();
            dialog.show(getSupportFragmentManager(), "CreateDatabaseEntryDialog");
        }
    }

    public void showCreateNewListDialog() {
        DialogFragment dialog = new CreateNewListDialog();
        dialog.show(getSupportFragmentManager(), "CreateNewListDialog");
    }

    public void changeViewportWidth(int value) {
        DESIRED_PAGE_WIDTH = value;
        updateViewportWidth();
    }

    private void updateViewportWidth() {
        final String jsCode = "document.getElementsByName('viewport')[0]" +
                ".setAttribute('content', 'width=" + DESIRED_PAGE_WIDTH + "');";
        webview.evaluateJavascript(jsCode, null);
        webview.setInitialScale(1);
    }

    @Override
    public void onSliderAdjustment(DialogFragment dialog, int progressValue) {
        changeViewportWidth(progressValue);
    }

    @Override
    public void onDialogDismiss(DialogFragment dialog) {
        edit.putInt(getString(R.string.preferencesViewportWidth), DESIRED_PAGE_WIDTH);
        edit.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.findActionOpenWidthDialog:
                showViewportWidthAdjustmentDialog();
                return true;
            case R.id.findActionOpenCreateEntryDialog:
                showCreateEntryDialog();
                return true;
            case R.id.findActionOpenCreateNewListDialog:
                showCreateNewListDialog();
            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_words_menu, menu);
        return true;
    }
}
