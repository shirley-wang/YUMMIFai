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

import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;




public class RecipePage extends AppCompatActivity{
    float x1 ,x2;
    float y1, y2;
    private static final String TAG = RecipePage.class.getSimpleName();
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

        myWebView.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction())
                {
                    // when user first touches the screen we get x and y coordinate
                    case MotionEvent.ACTION_DOWN:
                    {
                        x1 = motionEvent.getX();
                        y1 = motionEvent.getY();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    {
                        x2 = motionEvent.getX();
                        y2 = motionEvent.getY();

                        Log.i(TAG, x2+"");
                        if (x1 < x2)
                        {
                            onSwipeRight();
                        }
                        if (x1 > x2)
                        {
                            onSwipeLeft();
                        }
                        break;
                    }
                }
                return false;
            }
        });

        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        myWebView.loadUrl("https://www.pinterest.com/search/pins/?q="+ MainActivity.getSearchTerm());
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.setSearchTerm("");
        Log.i(TAG, "on pause");
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.i(TAG, "on resume");
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void onSwipeRight(){
        Log.i(TAG,"swipe right");
    }

    public void onSwipeLeft(){
        Log.i(TAG,"swipe LEFT");
    }

}
