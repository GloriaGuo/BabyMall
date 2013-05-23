package com.goodbaby.babymall.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import com.goodbaby.babymall.R;

public class AdvertismentActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.advertisment);
        
        ImageView mAdvertismentImage = (ImageView) findViewById(R.id.image_advertisment);
        //mAdvertismentImage.setImageURI(uri);
        
        new Handler().postDelayed(new Runnable(){  
            @Override  
            public void run() {
                Intent mainIntent = new Intent(AdvertismentActivity.this, NavigationActivity.class);  
                AdvertismentActivity.this.startActivity(mainIntent);  
                AdvertismentActivity.this.finish();  
            }  
        }, 2000);
    }

}
