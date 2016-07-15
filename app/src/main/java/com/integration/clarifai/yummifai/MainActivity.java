package com.integration.clarifai.yummifai;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;

import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.ImageButton;

import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.clarifai.api.ClarifaiClient;
import com.clarifai.api.RecognitionRequest;
import com.clarifai.api.RecognitionResult;
import com.clarifai.api.Tag;



import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity{
    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int GALLERY_IMAGE_ACTIVITY_REQUEST_CODE = 200;
    // private static final String TAG = RecognitionActivity.class.getSimpleName();
    private static final String TAG = "ShirleyFlag";
    private final ClarifaiClient clarifaiClient = new ClarifaiClient(Credential.CLIENT_ID,
            Credential.CLIENT_SECRET);
    private static String searchTerm = "";
    private ListView listview;
    private ImageButton cameraButton;
    private ImageButton galleryButton;
    private Button recipeButton;
    private ArrayList<String> ingredients = new ArrayList<>();
    private static ArrayList<String> ingredientsToFind = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate");
        getViews();
        handleCameraBtnClick();
        handleGalleryBtnClick();

    }

    public void getViews(){
        cameraButton = (ImageButton) findViewById(R.id.cameraButton);
        galleryButton = (ImageButton)findViewById(R.id.galleryButton);
        recipeButton = (Button)findViewById(R.id.recipeBtn);
        listview = (ListView) findViewById(R.id.ingredient_listView);
        listview.setChoiceMode(listview.CHOICE_MODE_MULTIPLE);
    }
    public void handleCameraBtnClick() {
        cameraButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    public void handleGalleryBtnClick(){
        galleryButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        });
    }

    private void addCheckList(){
        listview.setTextFilterEnabled(true);
        listview.setAdapter(new ArrayAdapter<>(this,R.layout.recipe_list_item_checked,ingredients));

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long id) {
                CheckedTextView v = (CheckedTextView) view;
                Object obj = listview.getItemAtPosition(index);
                String item = obj.toString();
                if(v.isChecked())
                {

                    if(!ingredientsToFind.contains(item)){
                        ingredientsToFind.add(item);
                        Log.i(TAG,item+" is added");
                    }
                }
                else
                {
                    if(ingredientsToFind.contains(item)){
                        ingredientsToFind.remove(item);
                        Log.i(TAG, item + " is deleted");
                    }
                }
            }
        });
    }

    private void showRecipeBtn(){
        TextView chooseView = (TextView)findViewById(R.id.chooseIngredientText);
        chooseView.setVisibility(TextView.VISIBLE);
        recipeButton.setVisibility(Button.VISIBLE);
        recipeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, RecipePage.class);
                startActivity(i);

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ((requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE || requestCode == GALLERY_IMAGE_ACTIVITY_REQUEST_CODE )&& resultCode == RESULT_OK) {
            // Image captured and saved to fileUri specified in the Intent
            // Log.i(TAG, "its here");
            try {
                InputStream inStream = getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inStream);
                ImageView preview = (ImageView)findViewById(R.id.imageView);
                preview.setImageBitmap(bitmap);
                // Run recognition on a background thread since it makes a network call.
                new AsyncTask<Bitmap, Void, RecognitionResult>() {
                    @Override
                    protected RecognitionResult doInBackground(Bitmap... bitmaps) {
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmaps[0].compress(Bitmap.CompressFormat.JPEG, 90, stream);
                        byte[] byteArray = stream.toByteArray();
                        return clarifaiClient.recognize(new RecognitionRequest(byteArray).setModel("food-items-v0.1")).get(0);
                    }

                    @Override
                    protected void onPostExecute(RecognitionResult result) {
                        // ScrollView sV = (ScrollView)findViewById(R.id.scrollView);
                        clearScreen();

                        for (Tag tag : result.getTags()) {


                            Log.i(TAG, tag.getName() + tag.getProbability());
                            ingredients.add(tag.getName());

                        }
                        addCheckList();
                        showRecipeBtn();

                    }
                }.execute(bitmap);


            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            }
        } else if (resultCode == RESULT_CANCELED) {
            // User cancelled the image capture
        } else {
            // Image capture failed, advise user
        }



    }
    public static String getSearchTerm(){
        for(String str: ingredientsToFind){
            if(searchTerm.equals("")){
                searchTerm+=str;
            }
            else
                searchTerm+="%20"+str;
        }
        return searchTerm;
    }


    public static void setSearchTerm(String term){
        searchTerm = term;
    }
    private void clearScreen()
    {
        ingredientsToFind = new ArrayList<>();
        ingredients = new ArrayList<>();
        searchTerm = "";
    }


}
