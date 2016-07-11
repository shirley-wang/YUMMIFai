package com.integration.clarifai.yummifai;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;


public class RecipePage extends AppCompatActivity {
    float x1 ,x2;
    float y1, y2;
    private static final String TAG = "ShirleyFlag";
    private WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_page);
        getWebView();
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
        Log.i("ShirleyFlag","swipe right");

        // myWebView.loadUrl("http://allrecipes.com/search/results/?wt="+MainActivity.getSearchTerm());
    }

    public void onSwipeLeft(){
        Log.i("ShirleyFlag","swipe LEFT");
    }
}
