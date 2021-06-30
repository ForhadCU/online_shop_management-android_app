package com.agamilabs.smartshop;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

//import com.bumptech.glide.Glide;

public class DownloadImageActivity extends AppCompatActivity {
    String url = "https://images.pexels.com/photos/1226302/pexels-photo1226302.jpeg?auto=compress&cs=tinysrgb&dpr=1&w=500";
    ImageView imageView;
    Button button;
    ProgressDialog mProgressDialog;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downlaod_image);
        imageView = (ImageView) findViewById(R.id.image);
        button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
        /*        String imageUri = "https://i.imgur.com/tGbaZCY.jpg";
                Picasso.get().load(imageUri).into(imageView);*/
                someMethod();
            }
        });


    }
    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };

    private void someMethod() {
        Picasso.get().load("https://i.imgur.com/tGbaZCY.jpg").into(target);
    }

    @Override
    public void onDestroy() {  // could be in onPause or onStop
        Picasso.get().cancelRequest(target);
        super.onDestroy();
    }
}