package com.integration.clarifai.yummifai;


import android.annotation.TargetApi;

import android.content.Intent;

import android.icu.util.Calendar;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.CalendarContract.Events;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class RecipePage extends AppCompatActivity{
    private WebView myWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getWebView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.appbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_favorite:
                createEvent();
                return true;
            default: return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Create calendar event for
     */
    @TargetApi(Build.VERSION_CODES.N)
    private void createEvent(){
        Calendar beginTime = Calendar.getInstance();
        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.HOUR, endTime.get(Calendar.HOUR)+1);
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(Events.CONTENT_URI)
                .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis())
                .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis())
                .putExtra(Events.TITLE, "Make this on Sunday")
                .putExtra(Events.DESCRIPTION, myWebView.getUrl())
                .putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
        startActivity(intent);

    }

    public void getWebView(){
        myWebView = (WebView) findViewById(R.id.webview);
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        myWebView.loadUrl("https://www.pinterest.com/search/pins/?q="+ MainActivity.getSearchTerm());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.setSearchTerm("");
    }

}
