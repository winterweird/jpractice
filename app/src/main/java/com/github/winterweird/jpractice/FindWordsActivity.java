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
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.annotation.TargetApi;
import android.util.Log;
// own classes
import com.github.winterweird.jpractice.database.DatabaseHelper;
import com.github.winterweird.jpractice.database.FeedReaderContract;
import com.github.winterweird.jpractice.dialogs.ViewportWidthAdjustmentDialog;
import com.github.winterweird.jpractice.dialogs.CreateDatabaseEntryDialog;
import com.github.winterweird.jpractice.dialogs.CreateNewListDialog;

// Java API
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;

/**
 * Activity for discovering new words to add to your lists.
 *
 * This is chiefly a wrapper for a WebView pointing to Jisho.org.
 */
public class FindWordsActivity extends ToolbarBackButtonActivity
            implements ViewportWidthAdjustmentDialog.ViewportWidthAdjustmentDialogListener {
            
    private int MIN_PAGE_WIDTH;
    private int MAX_PAGE_WIDTH;
    private int DESIRED_PAGE_WIDTH; // TODO: make this not caps
    private WebView webview;
    private SharedPreferences prefs;
    private List<String> searchTerms;

    /**
     * Called when the activity is created.
     *
     * @param savedInstanceState The saved instance state
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.find_words);

        prefs = this.getSharedPreferences(getString(R.string.preferencesFile),
                Context.MODE_PRIVATE);
        
        Resources res = getResources();

        searchTerms = loadSearchTerms();

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
            // Solution to deprecation warning found here:
            // https://stackoverflow.com/a/33419123/4498826
            @SuppressWarnings("deprecation")
            @Override
            public void onReceivedError(WebView view, int errorCode,
                    String description, String failingUrl) {
                Toast.makeText(activity, "Error: " + description, Toast.LENGTH_LONG).show();
            }
            
            @TargetApi(android.os.Build.VERSION_CODES.M)
            @Override
            public void onReceivedError(WebView view, WebResourceRequest req,
                    WebResourceError rerr) {
                // Redirect to deprecated method, so you can use it in all SDK versions
                onReceivedError(view, rerr.getErrorCode(),
                        rerr.getDescription().toString(), req.getUrl().toString());
            } 
            @Override
            public void onPageFinished(WebView view, String url) {
                updateViewportWidth();
                String searchTerm = extractSearchTerm(url);
                if (!searchTerm.contains("/")) { // we assume that we have the right word
                    // TODO: check if we are rewinding to an earlier point in
                    // history, and if so, handle.
                    if (searchTerms.isEmpty() || !searchTerms.get(searchTerms.size()-1).equals(searchTerm)) {
                        searchTerms.add(searchTerm);
                    }
                }
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

    /**
     * Displays the viewport width adjustment dialog.
     *
     * Passes along the arguments MIN_PAGE_WIDTH, MAX_PAGE_WIDTH and
     * DESIRED_PAGE_WIDTH.
     */
    public void showViewportWidthAdjustmentDialog() {
        DialogFragment dialog = new ViewportWidthAdjustmentDialog(MIN_PAGE_WIDTH, MAX_PAGE_WIDTH, DESIRED_PAGE_WIDTH);
        dialog.show(getSupportFragmentManager(), "ViewportWidthAdjustmentDialog");
    }

    /**
     * Displays the dialog for entry creation.
     *
     * Checks if you have any lists created in the database first, and if there
     * aren't any, displays a toast informing you of this instead.
     */
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

    /**
     * Displays the dialog for new list creation.
     */
    public void showCreateNewListDialog() {
        DialogFragment dialog = new CreateNewListDialog();
        dialog.show(getSupportFragmentManager(), "CreateNewListDialog");
    }

    /**
     * Set the width of the viewport to the given value.
     *
     * @param value The new viewport width value
     */
    public void changeViewportWidth(int value) {
        DESIRED_PAGE_WIDTH = value;
        updateViewportWidth();
    }

    /**
     * Load search term history.
     *
     * NOTE: Currently does not store history in the database.
     *
     * @return An empty list for now
     */
    private List<String> loadSearchTerms() {
        // For the time being, just create a new history.
        // TODO: use together with database
        return new ArrayList<>();
    }

    /**
     * Extract the search term from the given URL.
     *
     * Assumes that the search term is of the form:
     * "https://wwww.jisho.org/search/&lt;search term&gt;"
     *
     * Will extract the search term which is encoded using the URL encoding
     * algorithm and decode it to UTF-8.
     *
     * @param url The URL to extract the search term from
     *
     * @return The search term in the URL.
     */
    private String extractSearchTerm(String url) {
        try {
            return java.net.URLDecoder.decode(url.replaceAll(".*/search/", ""), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            Log.e("Test", "Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Perform the actions required to actually update the viewport width.
     * 
     * This includes evaluating the javascript for updating the in-HTML viewport
     * value.
     */
    private void updateViewportWidth() {
        final String jsCode = "document.getElementsByName('viewport')[0]" +
                ".setAttribute('content', 'width=" + DESIRED_PAGE_WIDTH + "');";
        webview.evaluateJavascript(jsCode, null);
        webview.setInitialScale(1);
    }

    /**
     * Implementation of the slider adjustment callback of the
     * ViewportWidthAdjustmentDialog.
     *
     * @param dialog The dialog fragment that fired the event
     * @param progressValue The new value of the slider
     */
    @Override
    public void onSliderAdjustment(DialogFragment dialog, int progressValue) {
        changeViewportWidth(progressValue);
    }

    /**
     * Implementation of the dialog dismissal callback of the
     * ViewportWidthAdjustmentDialog.
     *
     * @param dialog The dialog fragment that fired the event
     */
    @Override
    public void onDialogDismiss(DialogFragment dialog) {
        SharedPreferences.Editor edit = prefs.edit();
        edit.putInt(getString(R.string.preferencesViewportWidth), DESIRED_PAGE_WIDTH);
        edit.apply();
    }

    /**
     * Defines the actions to be done when a menu item is selected.
     *
     * Menu items with defined actions: Showing dialogs of viewport width
     * adjustment, entry creation and list creation.
     *
     * @param item The selected menu item
     * @return true if the event should be consumed or false if it should be
     * further processed
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.findActionViewSearchHistory:
                Log.d("Test", "View search history action clicked");
                for (String term : searchTerms) {
                    Log.d("Test", "term: " + term);
                }
                return true;
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

    /**
     * Defines what actions need to be taken when the menu is created.
     *
     * Inflates the menu using R.menu.find_words_menu
     *
     * @param menu The menu that was created
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.find_words_menu, menu);
        return true;
    }
}
